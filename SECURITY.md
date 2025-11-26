# 🔒 Guía de Seguridad - ViveMedellin Backend

## Resumen de Configuración de Seguridad

### ✅ Estado Actual: SEGURO

La aplicación está correctamente configurada para una API REST stateless con JWT.

---

## 🛡️ Protecciones Implementadas

### 1. Autenticación JWT (JSON Web Tokens)

**Implementación:**
- Tokens firmados con clave secreta (HS256)
- Expiración: 30 minutos (1800000 ms)
- Refresh tokens: 7 días (604800000 ms)
- Tokens enviados en header `Authorization: Bearer <token>`

**Por qué es seguro:**
- Los tokens no pueden ser falsificados sin la clave secreta
- Expiración automática limita ventana de ataque
- No se almacenan en el servidor (stateless)

### 2. CSRF Deshabilitado

**¿Por qué está deshabilitado?**

```java
.csrf(AbstractHttpConfigurer::disable)
```

**Esto es SEGURO porque:**

✅ **La aplicación es stateless** - No usa sesiones del servidor  
✅ **No usa cookies** - JWT se envía en headers, no en cookies  
✅ **Frontend SPA** - React/Vue/Angular envía tokens explícitamente  
✅ **Session Policy: STATELESS** - Sin estado entre peticiones  

**¿Cuándo CSRF sería necesario?**
- ❌ Si usaras cookies para autenticación
- ❌ Si mantuvieras sesiones en el servidor
- ❌ Si tuvieras formularios HTML tradicionales

### 3. CORS (Cross-Origin Resource Sharing)

**Configuración actual:**

```java
// Orígenes permitidos (whitelist)
- http://localhost:3000
- http://localhost:8080
- https://vivemedellin-backend.onrender.com
- https://frontend-vivamedellin.vercel.app

// Métodos HTTP permitidos
GET, POST, PUT, DELETE, PATCH, OPTIONS

// Headers específicos (no usar "*")
Authorization, Content-Type, Accept, X-Requested-With, Cache-Control

// Credenciales permitidas
allowCredentials: true
```

**Mejora aplicada:** Headers específicos en vez de `"*"` para mayor seguridad.

### 4. Encriptación de Contraseñas

```java
BCryptPasswordEncoder(12)
```

- **BCrypt con factor 12**: Altamente resistente a ataques de fuerza bruta
- Factor 12 = 4096 iteraciones
- Cada contraseña tiene salt único

### 5. Control de Acceso por Roles

```java
// Endpoints públicos
.permitAll() - Registro, login, ver posts

// Requiere autenticación
.authenticated() - Perfil, notificaciones, posts guardados

// Solo USER
.hasRole("USER") - Crear posts, comentar

// Solo ADMIN
.hasRole("ADMIN") - Gestionar categorías, eliminar usuarios
```

---

## 🔐 Variables de Entorno Sensibles

**IMPORTANTE:** Los secretos NO deben estar en el código fuente.

### Configuración Actual:

```properties
# application.properties
jwt.secret=${JWT_SECRET:valor_por_defecto}
spring.datasource.password=${DB_PASSWORD:valor_por_defecto}
```

### ⚠️ ADVERTENCIA:

Los valores por defecto son solo para desarrollo local. En producción:

1. **Configurar variables de entorno:**
```bash
export JWT_SECRET=$(openssl rand -base64 64)
export DB_PASSWORD=tu_contraseña_segura
```

2. **O usar archivo .env** (git-ignored):
```bash
JWT_SECRET=clave_aleatoria_256_bits
DB_PASSWORD=contraseña_segura
```

3. **En servicios cloud** (Render, Heroku, AWS):
   - Usar gestores de secretos del proveedor
   - Variables de entorno del panel de control
   - NO hardcodear en el código

---

## 🚨 Vectores de Ataque Mitigados

### ✅ SQL Injection
**Protección:** Spring Data JPA con PreparedStatements automáticos

### ✅ XSS (Cross-Site Scripting)
**Protección:** 
- Spring Security escapa output por defecto
- Content-Type: application/json (no HTML)
- Validación de entrada

### ✅ CSRF (Cross-Site Request Forgery)
**No aplica:** API stateless con JWT en headers

### ✅ Brute Force en Passwords
**Protección:** BCrypt con factor 12 (muy lento para atacantes)

### ✅ Session Hijacking
**No aplica:** Sin sesiones del servidor (stateless)

### ✅ Token Theft
**Mitigación:** 
- Tokens de corta duración (30 min)
- HTTPS en producción (recomendado)
- No almacenar en localStorage (recomendación para frontend)

---

## 📋 Checklist de Seguridad

### Configuración Backend ✅

- [x] JWT con expiración corta
- [x] BCrypt para contraseñas (factor 12)
- [x] CORS configurado con whitelist
- [x] Sesiones STATELESS
- [x] CSRF deshabilitado (correcto para JWT)
- [x] Headers CORS específicos (no "*")
- [x] Variables de entorno para secretos
- [x] .gitignore para archivos sensibles
- [x] Control de acceso por roles
- [x] Manejo de errores 401/403 en JSON

### Producción (TODO antes de deployar)

- [ ] Generar JWT_SECRET seguro (256+ bits)
- [ ] Usar HTTPS obligatorio
- [ ] Configurar rate limiting (prevenir DDoS)
- [ ] Habilitar logs de seguridad
- [ ] Configurar firewall
- [ ] Actualizar CORS origins a dominios reales
- [ ] Usar gestor de secretos del proveedor cloud
- [ ] Implementar monitoreo de intentos de login fallidos
- [ ] Configurar headers de seguridad adicionales (Helmet)

### Frontend (Recomendaciones)

- [ ] Almacenar JWT en memoria (no localStorage)
- [ ] Implementar refresh token automático
- [ ] Limpiar token al hacer logout
- [ ] HTTPS obligatorio
- [ ] Sanitizar inputs del usuario
- [ ] Implementar CAPTCHA en login/registro (opcional)

---

## 🔧 Configuración de Producción Recomendada

### 1. Headers de Seguridad HTTP

Agregar a `application.properties`:

```properties
# Security Headers
server.servlet.session.cookie.http-only=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.same-site=strict
```

### 2. Rate Limiting

Considerar agregar dependencia:

```xml
<dependency>
    <groupId>com.github.vladimir-bukhtoyarov</groupId>
    <artifactId>bucket4j-core</artifactId>
    <version>8.1.0</version>
</dependency>
```

### 3. HTTPS Obligatorio

En producción, redirigir HTTP -> HTTPS:

```java
http.requiresChannel(channel -> 
    channel.anyRequest().requiresSecure()
);
```

---

## 📚 Referencias

- [OWASP Top 10](https://owasp.org/www-project-top-ten/)
- [Spring Security Documentation](https://docs.spring.io/spring-security/reference/)
- [JWT Best Practices](https://tools.ietf.org/html/rfc8725)
- [CORS Explained](https://developer.mozilla.org/en-US/docs/Web/HTTP/CORS)

---

## ✅ Conclusión

**Tu configuración actual de CSRF deshabilitado es correcta y segura** para una API REST stateless con JWT. Las mejoras implementadas (headers CORS específicos y variables de entorno) refuerzan aún más la seguridad.

**Próximo paso crítico:** Antes de deployar a producción, asegúrate de configurar las variables de entorno con valores seguros y únicos.
