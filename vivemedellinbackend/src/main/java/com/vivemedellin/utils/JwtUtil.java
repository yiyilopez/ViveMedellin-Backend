package com.vivemedellin.utils;

import com.vivemedellin.models.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

@Component
public class JwtUtil {

    // Clave en Base64 (mín. 256 bits). La tuya ya está Base64.
    private static final String SECRET_KEY = "VO6PUZTSAQxedzHLvhYE9C1MGN/tgYmYrfVsNNbufjs=";

    // 30 min access
    private static final long ACCESS_EXP_MS = 1000L * 60 * 30;
    // (Opcional) 7 días refresh
    private static final long REFRESH_EXP_MS = 1000L * 60 * 60 * 24 * 7;

    // Tolerancia de reloj (segundos)
    private static final long CLOCK_SKEW_SEC = 60;

    private final Set<String> invalidatedTokens = ConcurrentHashMap.newKeySet();

    private Key getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(SECRET_KEY);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("Invalid JWT secret key. Must be at least 32 bytes.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private JwtParser getParser() {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .setAllowedClockSkewSeconds(CLOCK_SKEW_SEC)
                .build();
    }

    /* ========== GENERACIÓN ========== */

    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(buildStandardClaims(userDetails), userDetails.getUsername(), ACCESS_EXP_MS);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(Map.of("typ", "refresh"), userDetails.getUsername(), REFRESH_EXP_MS);
    }

    /** Mantén este por compatibilidad si ya lo usas */
    public String generateToken(UserDetails userDetails) {
        return generateAccessToken(userDetails);
    }

    private Map<String, Object> buildStandardClaims(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof CustomUserDetails custom) {
            claims.put("userId", custom.getId());
        }
        // Guarda roles como array limpio
        List<String> roles = new ArrayList<>();
        for (GrantedAuthority a : userDetails.getAuthorities()) {
            // Normaliza a “USER”, “ADMIN” (sin ROLE_ si prefieres)
            String auth = a.getAuthority();
            roles.add(auth.startsWith("ROLE_") ? auth.substring(5) : auth);
        }
        claims.put("roles", roles);
        claims.put("typ", "access");
        return claims;
    }

    private String buildToken(Map<String, Object> claims, String subject, long expMs) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expMs);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /* ========== EXTRACCIÓN “SAFE” ========== */

    public Optional<Claims> tryExtractAllClaims(String token) {
        try {
            return Optional.of(getParser().parseClaimsJws(token).getBody());
        } catch (JwtException | IllegalArgumentException ex) {
            return Optional.empty();
        }
    }

    private Claims extractAllClaims(String token) {
        // Úsalo solo cuando estés dispuesto a manejar excepciones arriba
        return getParser().parseClaimsJws(token).getBody();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        Claims claims = extractAllClaims(token); // puede lanzar, tu filtro lo captura
        return claimsResolver.apply(claims);
    }

    /* ========== VALIDACIÓN ========== */

    public boolean validateToken(String token, UserDetails userDetails) {
        if (invalidatedTokens.contains(token)) return false;
        try {
            Claims c = extractAllClaims(token);
            String subject = c.getSubject();
            Date exp = c.getExpiration();
            return subject != null
                    && subject.equals(userDetails.getUsername())
                    && exp != null
                    && exp.after(new Date());
        } catch (JwtException | IllegalArgumentException ex) {
            return false;
        }
    }

    public boolean isTokenExpired(String token) {
        return tryExtractAllClaims(token)
                .map(c -> c.getExpiration().before(new Date()))
                .orElse(true);
    }

    public void invalidateToken(String token) {
        invalidatedTokens.add(token);
    }

    /* ========== Helpers de roles (si los necesitas) ========== */

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        return tryExtractAllClaims(token)
                .map(c -> (List<String>) c.get("roles"))
                .orElse(List.of());
    }

    public boolean isRefreshToken(String token) {
        return tryExtractAllClaims(token)
                .map(c -> "refresh".equals(c.get("typ")))
                .orElse(false);
    }
}
