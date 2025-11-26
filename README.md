# ViveMedellin - Backend API

<div align="center">

**Plataforma inteligente para descubrir Medellín**

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue.svg)](https://www.postgresql.org/)
[![JWT](https://img.shields.io/badge/JWT-Auth-red.svg)](https://jwt.io/)

</div>

## Descripción

**ViveMedellin** es una plataforma inteligente que permite a los usuarios descubrir, explorar y participar activamente en la amplia variedad de eventos, actividades y lugares de interés disponibles en Medellín. La aplicación facilita la interacción social, la personalización de la experiencia del usuario y el acceso eficiente a la información relevante sobre la ciudad.

Este repositorio contiene el **backend REST API** desarrollado con Spring Boot que gestiona toda la lógica de negocio de la plataforma.

## Características Principales

### Gestión de Eventos
- Publicación y exploración de eventos en Medellín
- Categorización de eventos (música, deportes, cultura, gastronomía, etc.)
- Búsqueda y filtrado inteligente
- Paginación y ordenamiento de resultados
- Subida de imágenes para eventos (hasta 10MB)

### Sistema de Usuarios
- Registro y autenticación con JWT (30 minutos de expiración)
- Perfiles de usuario personalizables
- **Imagen de perfil** - Los usuarios pueden subir foto que aparece en posts y comentarios
- Sistema de roles (USER, ADMIN)
- Gestión de sesiones seguras

### Interacción Social
- **Comentarios en eventos** con respuestas anidadas (hilos)
- **Edición de comentarios** con tracking de cambios (campo `editedDate`)
- Sistema de notificaciones inteligente:
  - Notificación al autor del evento cuando alguien comenta
  - Notificación a usuarios que guardaron el evento
  - Notificación cuando responden a tus comentarios
- **Posts guardados (favoritos)** - Guarda eventos de interés
- Participación comunitaria activa

### Dashboard y Estadísticas
- Estadísticas generales de la plataforma
- Posts más comentados
- Eventos más guardados
- Usuarios más activos
- Categorías populares
- Feed de actividad reciente

### Seguridad
- Autenticación basada en JWT (JSON Web Tokens)
- Encriptación de contraseñas con BCrypt (factor 12)
- Control de acceso basado en roles
- Categorías administradas exclusivamente por ADMIN
- CORS configurado para frontend
- Sesiones stateless

## Arquitectura

Arquitectura Monolítica en Capas

```
┌─────────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN                  │
│   controllers/                          │
│   - PostController (Eventos)            │
│   - UserController (+ Imágenes)         │
│   - CategoryController (ADMIN)          │
│   - CommentController (+ Edición)       │
│   - NotificationController              │
│   - SavedPostController                 │
│   - DashboardController                 │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   CAPA DE NEGOCIO                       │
│   services/                             │
│   - PostService (Lógica de eventos)     │
│   - UserService (+ ProfileImage)        │
│   - CategoryService                     │
│   - CommentService (+ Edición)          │
│   - NotificationService                 │
│   - SavedPostService                    │
│   - DashboardService.                   │
│   - FileService (Imágenes)              │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   CAPA DE ACCESO A DATOS                │
│   repositories/ (Spring Data JPA)       │
│   - PostRepo                            │
│   - UserRepo                            │
│   - CategoryRepo                        │
│   - CommentRepo                         │
│   - NotificationRepo ⭐                 │
│   - SavedPostRepo ⭐                    │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   BASE DE DATOS                         │
│   PostgreSQL (9 tablas)                 │
│   - users (+ profile_image)             │
│   - posts                               │
│   - categories                          │
│   - comments (+ edited_date)            │
│   - saved_posts                         │
│   - notifications                       │
│   - roles                               │
└─────────────────────────────────────────┘
```

## Tecnologías Utilizadas

| Tecnología | Versión | Propósito |
|------------|---------|-----------|
| **Spring Boot** | 3.3.5 | Framework principal |
| **Java** | 17 | Lenguaje de programación |
| **Spring Security** | 6.x | Seguridad y autenticación |
| **Spring Data JPA** | 3.x | ORM y persistencia |
| **PostgreSQL** | Latest | Base de datos |
| **JWT (jjwt)** | 0.11.2 | Tokens de autenticación |
| **Lombok** | 1.18.34 | Reducción de boilerplate |
| **ModelMapper** | 3.1.1 | Mapeo DTO-Entity |
| **Maven** | 3.x | Gestión de dependencias |
| **SpringDoc OpenAPI** | Latest | Documentación API |

## Estructura del Proyecto

```
vivemedellinbackend/
├── src/main/java/com/vivemedellin/
│   ├── ViveMedellinApplication.java    # Punto de entrada
│   ├── config/                         # Configuraciones
│   │   ├── SecurityConfig.java         # Seguridad JWT
│   │   ├── CorsConfig.java            # CORS
│   │   ├── OpenApiConfig.java        # Swagger/OpenAPI
│   │   └── AppConstants.java          # Constantes
│   ├── controllers/                    # Endpoints REST (9)
│   │   ├── PostController.java        # API de eventos
│   │   ├── UserController.java        # API de usuarios + imágenes 
│   │   ├── CategoryController.java    # API de categorías (ADMIN) 
│   │   ├── CommentController.java     # API de comentarios + edición 
│   │   ├── NotificationController.java   # API de notificaciones
│   │   ├── SavedPostController.java     # API de posts guardados
│   │   ├── DashboardController.java     # API de estadísticas
│   │   ├── AuthController.java        # Autenticación
│   │   └── HomeController.java        # Landing page
│   ├── models/                        # Entidades JPA (8)
│   │   ├── Post.java                  # Modelo de evento
│   │   ├── User.java                 # Modelo de usuario + profileImage
│   │   ├── Category.java              # Modelo de categoría
│   │   ├── Comment.java              # Modelo de comentario + editedDate
│   │   ├── SavedPost.java            # Posts guardados por usuarios
│   │   ├── Notification.java         # Notificaciones del sistema
│   │   ├── Role.java                  # Enum de roles
│   │   └── CustomUserDetails.java    # Detalles de seguridad
│   ├── repositories/                  # Interfaces JPA
│   ├── services/                      # Lógica de negocio
│   │   ├── impl/                      # Implementaciones
│   │   └── FileService.java          # Manejo de archivos
│   ├── payloads/                      # DTOs
│   │   ├── UserDto.java              # DTO con profileImage
│   │   ├── UserResponseDto.java      # DTO con profileImage
│   │   ├── CommentDto.java           # DTO con editedDate
│   │   ├── NotificationDto.java      # DTO de notificaciones
│   │   ├── DashboardDto.java         # DTO de estadísticas
│   │   └── ...
│   ├── security/                      # Servicios de seguridad
│   ├── filters/                       # Filtros JWT
│   ├── exceptions/                    # Manejo de errores
│   └── utils/                         # Utilidades
└── src/main/resources/
    ├── application.properties         # Configuración

    └── templates/                     # Plantillas HTML
```

## Instalación y Configuración

### Prerrequisitos

- Java 17 o superior
- Maven 3.x
- PostgreSQL 12 o superior
- Git

### 1. Clonar el repositorio

```bash
git clone https://github.com/yiyilopez/ViveMedellin-Backend.git
cd ViveMedellin-Backend/vivemedellinbackend
```

### 2. Configurar la Base de Datos

Crear la base de datos en PostgreSQL:

```sql
CREATE DATABASE vivemedellin;
```

### 3. Configurar `application.properties`

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/vivemedellin?currentSchema=public&stringtype=unspecified
spring.datasource.username=postgres
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=org.postgresql.Driver

# JPA/Hibernate Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.properties.hibernate.jdbc.batch_size=25
spring.jpa.properties.hibernate.order_inserts=true
spring.jpa.properties.hibernate.order_updates=true

# HikariCP Connection Pool
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.maximum-pool-size=10
spring.datasource.hikari.minimum-idle=2
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
spring.datasource.hikari.pool-name=ViveMedellinPool

# JWT Configuration
jwt.secret=843567893696976453275974432697R634976R738467TR678T34865R6834R8763T478378637664538745673865783678548735687R3
jwt.expiration=1800000
jwt.refresh-expiration=604800000

# File Upload Configuration
spring.servlet.multipart.max-file-size=10MB
spring.servlet.multipart.max-request-size=10MB

# Server Configuration
server.port=8081

# Logging Configuration
logging.level.org.springframework.security=DEBUG
logging.level.com.vivemedellin=DEBUG
```

### 4. Configurar Java 17

Asegurarse de tener JDK 17 instalado y configurado:

```bash
# Verificar versión de Java
java -version

# En macOS, configurar JAVA_HOME para JDK 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
```

### 5. Compilar y ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

### 5. Compilar y ejecutar

```bash
# Compilar el proyecto con JDK 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8081`

## API Endpoints

### 🔓 Autenticación y Registro (Públicos)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/users/register` | Registrar nuevo usuario |
| POST | `/api/users/login` | Iniciar sesión (obtener JWT) |

**Ejemplo de Registro:**
```bash
curl -X POST http://localhost:8081/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Juan Pérez",
    "email": "juan@example.com",
    "password": "password123",
    "about": "Amante de Medellín"
  }'
```

**Ejemplo de Login:**
```bash
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "juan@example.com",
    "password": "password123"
  }'
```

---

### 📝 Posts/Eventos

#### Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/posts` | Listar todos los eventos (paginado: ?pageNumber=0&pageSize=10&sortBy=date&sortDir=desc) |
| GET | `/api/posts/{postId}` | Obtener evento específico por ID |
| GET | `/api/posts/user/{userId}` | Obtener eventos de un usuario específico |
| GET | `/api/posts/category/{categoryId}` | Obtener eventos de una categoría (paginado) |
| GET | `/api/posts/search/{keywords}` | Buscar eventos por palabras clave (paginado) |
| GET | `/api/posts/image/{imageName}` | Obtener imagen de un evento |

#### Endpoints Protegidos (Requieren JWT)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/posts/user/{userId}/category/{categoryId}` | Crear nuevo evento |
| PUT | `/api/posts/{postId}` | Actualizar evento propio |
| DELETE | `/api/posts/{postId}` | Eliminar evento propio (ADMIN puede eliminar cualquiera) |
| POST | `/api/posts/image/upload/{postId}` | Subir imagen a evento (max 10MB) |

**Ejemplo de Crear Evento:**
```bash
curl -X POST http://localhost:8081/api/posts/user/1/category/1 \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Festival de Música en el Parque",
    "content": "Gran festival con artistas locales",
    "date": "2025-12-15T18:00:00"
  }'
```

---

### 👤 Usuarios

#### Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users/profile-image/{imageName}` | Obtener imagen de perfil de usuario |

#### Endpoints Protegidos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users/{userId}` | Obtener perfil de usuario |
| PUT | `/api/users/{userId}` | Actualizar perfil propio |
| POST | `/api/users/profile-image/upload/{userId}` | Subir/actualizar imagen de perfil |

#### Endpoints ADMIN

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/users/` | Listar todos los usuarios |
| DELETE | `/api/users/{userId}` | Eliminar cualquier usuario |

---

### 🗂️ Categorías

#### Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/categories` | Listar todas las categorías |
| GET | `/api/categories/{categoryId}` | Obtener categoría específica |

#### Endpoints ADMIN (Solo administradores)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/categories` | Crear nueva categoría |
| PUT | `/api/categories/{categoryId}` | Actualizar categoría |
| DELETE | `/api/categories/{categoryId}` | Eliminar categoría |

**Ejemplo de Crear Categoría (ADMIN):**
```bash
curl -X POST http://localhost:8081/api/categories \
  -H "Authorization: Bearer <admin_token>" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryTitle": "Música",
    "categoryDescription": "Eventos musicales en Medellín"
  }'
```

---

### 💬 Comentarios

#### Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/posts/{postId}/comments` | Ver comentarios de un evento |

#### Endpoints Protegidos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/posts/{postId}/comments` | Crear comentario en evento |
| POST | `/api/comments/{commentId}/replies` | Responder a un comentario |
| PUT | `/api/comments/{commentId}` | Editar comentario propio (actualiza `editedDate`) |
| DELETE | `/api/comments/{commentId}` | Eliminar comentario propio (ADMIN puede eliminar cualquiera) |

**Características de comentarios:**
- Soporte para **respuestas anidadas** (hilos de conversación)
- **Edición de comentarios** con tracking de fecha de edición
- Los comentarios muestran información del autor (nombre, imagen de perfil)
- Al comentar se generan notificaciones automáticas

**Ejemplo de Crear Comentario:**
```bash
curl -X POST http://localhost:8081/api/posts/1/comments \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "¡Me encanta este evento!"
  }'
```

**Ejemplo de Responder a Comentario:**
```bash
curl -X POST http://localhost:8081/api/comments/5/replies \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{
    "content": "Estoy de acuerdo contigo"
  }'
```

---

### ⭐ Posts Guardados (Favoritos)

Permite a los usuarios guardar eventos de su interés para acceso rápido.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/saved-posts/{postId}` | Guardar evento en favoritos |
| DELETE | `/api/saved-posts/{postId}` | Quitar evento de favoritos |
| GET | `/api/saved-posts` | Listar eventos guardados del usuario autenticado |
| GET | `/api/saved-posts/user/{userId}` | Listar eventos guardados por userId |
| GET | `/api/saved-posts/user/email/{email}` | Listar eventos guardados por email |
| GET | `/api/saved-posts/{postId}/check` | Verificar si evento está guardado |

**Ejemplo de Guardar Evento:**
```bash
curl -X POST http://localhost:8081/api/saved-posts/1 \
  -H "Authorization: Bearer <token>"
```

**Ejemplo de Listar Eventos Guardados:**
```bash
curl -X GET http://localhost:8081/api/saved-posts \
  -H "Authorization: Bearer <token>"
```

---

### 🔔 Notificaciones

Sistema inteligente de notificaciones que informa a los usuarios sobre actividad relevante.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/notifications` | Listar todas las notificaciones del usuario |
| GET | `/api/notifications/unread` | Listar solo notificaciones no leídas |
| GET | `/api/notifications/unread/count` | Contar notificaciones pendientes |
| PUT | `/api/notifications/{notificationId}/read` | Marcar notificación como leída |
| PUT | `/api/notifications/read-all` | Marcar todas las notificaciones como leídas |

**Tipos de Notificaciones:**
- `NEW_COMMENT_ON_POST` - Alguien comentó en tu evento
- `NEW_COMMENT_ON_SAVED_POST` - Alguien comentó en un evento que guardaste
- `COMMENT_REPLY` - Alguien respondió a tu comentario

**Información incluida en notificaciones:**
- Tipo de notificación
- Mensaje descriptivo
- ID y título del post relacionado
- ID del comentario
- Usuario que desencadenó la notificación (nombre e imagen)
- Estado de lectura (`isRead`)
- Fecha de creación

**Ejemplo de Obtener Notificaciones:**
```bash
curl -X GET http://localhost:8081/api/notifications \
  -H "Authorization: Bearer <token>"
```

**Respuesta:**
```json
[
  {
    "id": 9,
    "type": "NEW_COMMENT_ON_SAVED_POST",
    "message": "Juan comentó en el evento \"Festival de Música\" que guardaste",
    "postId": 1,
    "postTitle": "Festival de Música en el Parque",
    "commentId": 10,
    "triggeredByUser": {
      "id": 353,
      "name": "Juan Pérez",
      "profileImage": "juan_profile.jpg"
    },
    "isRead": false,
    "createdDate": "2025-11-25T13:56:47.585+00:00"
  }
]
```

---

### 📊 Dashboard y Estadísticas

Endpoints públicos para visualizar estadísticas y métricas de la plataforma.

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/dashboard` | Obtener todas las estadísticas (completo) |
| GET | `/api/dashboard/stats` | Estadísticas generales (totales) |
| GET | `/api/dashboard/top-commented-posts` | Eventos más comentados (top 5) |
| GET | `/api/dashboard/most-saved-posts` | Eventos más guardados (top 5) |
| GET | `/api/dashboard/most-active-users` | Usuarios más activos (top 5) |
| GET | `/api/dashboard/popular-categories` | Categorías populares |
| GET | `/api/dashboard/recent-activity` | Actividad reciente (últimos 10) |

**Ejemplo de Respuesta de Stats:**
```json
{
  "totalPosts": 45,
  "totalUsers": 120,
  "totalComments": 230,
  "totalCategories": 8,
  "totalSavedPosts": 67
}
```

---

## 🔐 Autenticación y Seguridad

La API usa **JWT (JSON Web Tokens)** para autenticación segura.

### Flujo de Autenticación

1. **Registrarse** con `/api/users/register`
2. **Iniciar sesión** con `/api/users/login` → Recibir token JWT
3. **Incluir token** en header `Authorization: Bearer <token>` para endpoints protegidos

### Configuración de Seguridad

- **Tokens JWT**: Expiran en 30 minutos (`jwt.expiration=1800000`)
- **Refresh Tokens**: Válidos por 7 días
- **Encriptación**: BCrypt con factor 12 para contraseñas
- **Roles**: USER (por defecto) y ADMIN
- **CORS**: Configurado para permitir frontend
- **Sesiones**: Stateless (sin servidor de sesiones)

### Ejemplo de Petición Autenticada

```bash
curl -X GET http://localhost:8081/api/saved-posts \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJVU0VSIl0..."
```

### Manejo de Errores

| Código | Descripción |
|--------|-------------|
| 401 | No autenticado (token inválido o expirado) |
| 403 | No autorizado (sin permisos) |
| 404 | Recurso no encontrado |
| 409 | Conflicto (email ya existe) |
| 500 | Error interno del servidor |

---

## 📚 Documentación API (Swagger)

Una vez ejecutada la aplicación, accede a la documentación interactiva:

- **Swagger UI**: `http://localhost:8081/swagger-ui/index.html`
- **OpenAPI JSON**: `http://localhost:8081/v3/api-docs`

### Características de Swagger UI

✅ Documentación completa de todos los endpoints  
✅ Autenticación JWT integrada (botón "Authorize")  
✅ Pruebas interactivas de la API  
✅ Esquemas de request/response detallados  
✅ Ejemplos de uso para cada endpoint  
✅ Códigos de estado HTTP y respuestas de error  

---

## 🗄️ Modelo de Datos

### Entidades Principales

```
┌─────────────────────────────────────────────────────────────┐
│ User                                                        │
├─────────────────────────────────────────────────────────────┤
│ - id: Integer (PK)                                          │
│ - name: String                                              │
│ - email: String (unique)                                    │
│ - password: String (encrypted)                              │
│ - about: String                                             │
│ - profileImage: String                                      │
│ - roles: Set<Role>                                          │
│ - posts: List<Post>                                         │
│ - comments: List<Comment>                                   │
│ - savedPosts: List<SavedPost>                               │
│ - notifications: List<Notification>                         │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Post                                                        │
├─────────────────────────────────────────────────────────────┤
│ - id: Integer (PK)                                          │
│ - title: String                                             │
│ - content: String (TEXT)                                    │
│ - imageName: String                                         │
│ - date: Date                                                │
│ - addedDate: Date                                           │
│ - category: Category (FK)                                   │
│ - user: User (FK)                                           │
│ - comments: Set<Comment>                                    │
│ - savedByUsers: List<SavedPost>                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Comment                                                     │
├─────────────────────────────────────────────────────────────┤
│ - id: Integer (PK)                                          │
│ - content: String                                           │
│ - createdDate: Date                                         │
│ - editedDate: Date (nullable)                               │
│ - post: Post (FK)                                           │
│ - user: User (FK)                                           │
│ - parentComment: Comment (FK, nullable)                     │
│ - replies: Set<Comment>                                     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ SavedPost                                                   │
├─────────────────────────────────────────────────────────────┤
│ - id: Integer (PK)                                          │
│ - user: User (FK)                                           │
│ - post: Post (FK)                                           │
│ - savedDate: Date                                           │
│ - UNIQUE(user_id, post_id)                                  │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Notification                                                │
├─────────────────────────────────────────────────────────────┤
│ - id: Integer (PK)                                          │
│ - type: NotificationType (ENUM)                             │
│ - message: String                                           │
│ - isRead: Boolean                                           │
│ - createdDate: Date                                         │
│ - recipient: User (FK)                                      │
│ - triggeredByUser: User (FK)                                │
│ - post: Post (FK, nullable)                                 │
│ - comment: Comment (FK, nullable)                           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ Category                                                    │
├─────────────────────────────────────────────────────────────┤
│ - categoryId: Integer (PK)                                  │
│ - categoryTitle: String                                     │
│ - categoryDescription: String                               │
│ - posts: List<Post>                                         │
└─────────────────────────────────────────────────────────────┘
```

### Relaciones

- **User ↔ Post**: One-to-Many (un usuario puede crear múltiples posts)
- **Post ↔ Category**: Many-to-One (un post pertenece a una categoría)
- **Post ↔ Comment**: One-to-Many (un post puede tener múltiples comentarios)
- **User ↔ Comment**: One-to-Many (un usuario puede crear múltiples comentarios)
- **Comment ↔ Comment**: Self-referential (comentarios con respuestas anidadas)
- **User ↔ SavedPost ↔ Post**: Many-to-Many (usuarios guardan posts)
- **User ↔ Notification**: One-to-Many (un usuario recibe múltiples notificaciones)

---

## 🚀 Características Avanzadas

### 1. Sistema de Notificaciones Inteligente

El sistema detecta automáticamente eventos relevantes y notifica a usuarios:

- **Autor del evento** recibe notificación cuando alguien comenta
- **Usuarios que guardaron el evento** son notificados de nuevos comentarios
- **Autor de comentario** recibe notificación cuando alguien responde
- Notificaciones incluyen contexto completo (post, comentario, usuario)
- Contador de notificaciones no leídas en tiempo real

### 2. Posts Guardados (Favoritos)

- Los usuarios pueden guardar eventos de interés
- Acceso rápido a eventos guardados por usuario autenticado
- Consulta por userId o email para flexibilidad
- Verificación de estado guardado para UI reactiva
- Dashboard muestra posts más guardados

### 3. Comentarios con Respuestas Anidadas

- Hilos de conversación completos
- Respuestas ilimitadas a comentarios
- Edición de comentarios con tracking de fecha
- Eliminación cascada de respuestas
- Información completa del autor en cada comentario

### 4. Gestión de Imágenes

- Subida de imágenes de perfil (usuarios)
- Subida de imágenes de eventos (posts)
- Límite de 10MB por archivo
- Almacenamiento local en `/images/`
- Endpoints para recuperar imágenes

### 5. Dashboard con Métricas

- Estadísticas en tiempo real
- Top 5 posts más comentados
- Top 5 posts más guardados
- Top 5 usuarios más activos
- Categorías populares
- Feed de actividad reciente (últimos 10 eventos)

### 6. Búsqueda y Filtrado

- Búsqueda por palabras clave en título y contenido
- Filtrado por categoría
- Filtrado por usuario
- Paginación en todos los listados
- Ordenamiento configurable (fecha, título, etc.)

### 7. Seguridad Robusta

- JWT con expiración de tokens
- Refresh tokens para sesiones largas
- BCrypt para contraseñas (factor 12)
- Control de acceso basado en roles
- CORS configurado para producción
- Validación de entrada en todos los endpoints

---

## 🛠️ Tecnologías y Dependencias

### Core Framework
- **Spring Boot 3.3.5** - Framework principal
- **Spring Web** - REST API
- **Spring Data JPA** - ORM y persistencia
- **Spring Security** - Autenticación y autorización

### Base de Datos
- **PostgreSQL** - Base de datos relacional
- **HikariCP** - Connection pooling (max 10 conexiones)
- **Hibernate** - ORM con optimizaciones de batch

### Seguridad
- **JWT (jjwt 0.11.2)** - JSON Web Tokens
- **BCrypt** - Encriptación de contraseñas

### Utilidades
- **Lombok 1.18.34** - Reducción de boilerplate
- **ModelMapper 3.1.1** - Mapeo DTO-Entity
- **Validation API** - Validación de datos

### Documentación
- **SpringDoc OpenAPI** - Generación de Swagger UI

### Build Tool
- **Maven 3.x** - Gestión de dependencias y build

---

## 🧪 Testing

### Ejecutar Tests

```bash
# Ejecutar todos los tests
mvn test

# Ejecutar tests con cobertura
mvn test jacoco:report
```

### Estructura de Tests

```
src/test/java/com/vivemedellin/
└── ViveMedellinApplicationTests.java
```

---

## 📦 Deployment

### Compilar JAR

```bash
mvn clean package
```

El archivo JAR se generará en: `target/vivemedellinbackend-0.0.1-SNAPSHOT.jar`

### Ejecutar JAR

```bash
java -jar target/vivemedellinbackend-0.0.1-SNAPSHOT.jar
```

### Docker (Opcional)

El proyecto incluye un `Dockerfile` para containerización:

```bash
# Construir imagen
docker build -t vivemedellin-backend .

# Ejecutar contenedor
docker run -p 8081:8081 vivemedellin-backend
```

---

## 🐛 Troubleshooting

### Error: JDK incompatible con Lombok

**Problema**: `java.lang.ExceptionInInitializerError` con JDK 25

**Solución**: Usar JDK 17
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn clean compile
```

### Error: Puerto 8081 ya en uso

**Problema**: `Port 8081 is already in use`

**Solución**: 
```bash
# Encontrar proceso
lsof -i :8081

# Matar proceso
kill -9 <PID>

# O cambiar puerto en application.properties
server.port=8082
```

### Error: No se puede conectar a PostgreSQL

**Problema**: `Connection refused` o `database does not exist`

**Solución**:
1. Verificar que PostgreSQL esté corriendo: `pg_isready`
2. Crear base de datos: `CREATE DATABASE vivemedellin;`
3. Verificar credenciales en `application.properties`

### Error: JWT token inválido

**Problema**: `401 Unauthorized`

**Solución**:
1. Verificar que el token no haya expirado (30 min)
2. Incluir prefijo "Bearer " en el header
3. Verificar formato: `Authorization: Bearer <token>`

---

## 🤝 Contribuir

1. Fork el proyecto
2. Crear rama feature (`git checkout -b feature/AmazingFeature`)
3. Commit cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abrir Pull Request

---

## 👥 Autores

- **Equipo ViveMedellin** - Desarrollo inicial

---

## 📄 Licencia

Este proyecto es parte de **ViveMedellin** - Plataforma para descubrir Medellín.

---

## 📞 Contacto

Para preguntas o soporte, contactar al equipo de desarrollo.

---

## 🔗 Enlaces Útiles

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)
- [JWT Introduction](https://jwt.io/introduction)
- [Swagger/OpenAPI](https://swagger.io/)

---

**⭐ ViveMedellin - Descubre, Conecta, Vive la ciudad**
