package com.vivemedellin.config;

import com.vivemedellin.filters.JwtAuthenticationFilter;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(UserDetailsService userDetailsService, JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // IMPORTANTE: En producción, especificar solo los orígenes necesarios
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:8080",
                "https://vivemedellin-backend.onrender.com",
                "https://frontend-vivamedellin.vercel.app"));
        
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // SEGURIDAD: Limitar headers específicos en vez de "*"
        configuration.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type",
                "Accept",
                "X-Requested-With",
                "Cache-Control"
        ));
        
        // Exponer headers necesarios para el frontend
        configuration.setExposedHeaders(List.of("Authorization"));
        
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CSRF deshabilitado: SEGURO porque usamos JWT stateless (no cookies)
        // Solo habilitar CSRF si usas sesiones basadas en cookies
        .csrf(AbstractHttpConfigurer::disable)
        
        .cors(Customizer.withDefaults())
        
        // Stateless: sin sesiones en el servidor, todo con JWT
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        // ⬇️ NUEVO: manejadores uniformes para 401/403 en JSON
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint((req, res, e) -> {
                res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"UNAUTHORIZED\",\"message\":\"" + e.getMessage() + "\"}");
            })
            .accessDeniedHandler((req, res, e) -> {
                res.setStatus(HttpServletResponse.SC_FORBIDDEN);
                res.setContentType("application/json");
                res.getWriter().write("{\"error\":\"FORBIDDEN\",\"message\":\"" + e.getMessage() + "\"}");
            })
        )

        .authorizeHttpRequests(auth -> auth
            // ⬇️ NUEVO: liberar /error (crítico para que no se “securice” la página de error)
            .requestMatchers("/error").permitAll()

            // (Opcional) health si usas actuator
            .requestMatchers("/actuator/health").permitAll()

            // Tus endpoints públicos existentes
            .requestMatchers(
                "/api/users/register",
                "/api/users/login",
                "/api/users/refresh-token",
                "/swagger-ui/**",
                "/v3/api-docs/**",
                "/swagger-ui.html"
            ).permitAll()

            .requestMatchers(HttpMethod.GET, "/api/categories/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/posts/*/comments").hasRole("USER")
            .requestMatchers(HttpMethod.POST, "/api/comments/*/replies").hasRole("USER")
            .requestMatchers(HttpMethod.GET, "/api/posts/*/comments").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/comments/*").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/comments/*").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/comments/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/category/{categoryId}/posts").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/posts/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/dashboard/**").permitAll()
            .requestMatchers("/api/notifications/**").authenticated()
            .requestMatchers("/api/saved-posts/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/users/").hasRole("ADMIN")
            .requestMatchers(HttpMethod.GET, "/api/users/{userId}").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/users/profile-image/upload/*").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/users/profile-image/*").permitAll()
            .requestMatchers(HttpMethod.PUT, "/api/posts/**").hasRole("USER")
            .requestMatchers(HttpMethod.DELETE, "/api/posts/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.DELETE, "/api/comments/**").hasAnyRole("USER", "ADMIN")
            .requestMatchers(HttpMethod.PUT, "/api/users/{userId}").authenticated()
            .requestMatchers(HttpMethod.DELETE, "/api/users/{userId}").hasRole("ADMIN")
            .requestMatchers("/api/categories/**").hasRole("ADMIN")
            .anyRequest().authenticated()
        )

        .authenticationProvider(authenticationProvider())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
}


}
