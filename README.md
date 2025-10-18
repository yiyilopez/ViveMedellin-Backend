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

### 2. Configurar `application.properties`

Editar `src/main/resources/application.properties`:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/vivemedellin_db
spring.datasource.username=tu_usuario
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update

# JWT Configuration
jwt.secret=tu_clave_secreta_aqui
jwt.expiration=86400000

# Server Configuration
server.port=8080
```

### 4. Compilar y ejecutar

```bash
# Compilar el proyecto
mvn clean install

# Ejecutar la aplicación
mvn spring-boot:run
```

La API estará disponible en: `http://localhost:8080`

## Endpoints Principales

### Endpoints Públicos (Sin autenticación)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/users/register` | Registrar nuevo usuario |
| POST | `/api/users/login` | Iniciar sesión (obtener JWT) |
| GET | `/api/posts` | Listar todos los eventos (paginado) |
| GET | `/api/posts/{id}` | Obtener evento específico |
| GET | `/api/posts/search/{keywords}` | Buscar eventos por palabra clave |
| GET | `/api/categories` | Listar todas las categorías |
| GET | `/api/categories/{id}` | Obtener categoría específica |
| GET | `/api/posts/{id}/comments` | Ver comentarios de un evento |
| GET | `/api/users/profile-image/{imageName}` | Obtener imagen de perfil |
| GET | `/api/posts/image/{imageName}` | Obtener imagen de evento |
| GET | `/api/dashboard` | Obtener estadísticas completas |
| GET | `/api/dashboard/stats` | Estadísticas generales |

### Endpoints Protegidos (Requieren Autenticación - USER)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/posts` | Crear nuevo evento |
| PUT | `/api/posts/{id}` | Actualizar evento propio |
| DELETE | `/api/posts/{id}` | Eliminar evento propio |
| POST | `/api/posts/image/upload/{id}` | Subir imagen a evento |
| POST | `/api/posts/{postId}/comments` | Comentar en evento |
| POST | `/api/comments/{commentId}/replies` | Responder a comentario |
| PUT | `/api/comments/{id}` | Editar comentario propio |
| DELETE | `/api/comments/{id}` | Eliminar comentario propio |
| PUT | `/api/users/{id}` | Actualizar perfil propio |
| POST | `/api/users/profile-image/upload/{id}` | Subir imagen de perfil |
| POST | `/api/saved-posts/{postId}` | Guardar evento en favoritos |
| DELETE | `/api/saved-posts/{postId}` | Quitar evento de favoritos |
| GET | `/api/saved-posts` | Listar eventos guardados |
| GET | `/api/saved-posts/{postId}/check` | Verificar si evento está guardado |
| GET | `/api/notifications` | Listar todas las notificaciones |
| GET | `/api/notifications/unread` | Listar notificaciones no leídas |
| GET | `/api/notifications/unread/count` | Contar notificaciones pendientes |
| PUT | `/api/notifications/{id}/read` | Marcar notificación como leída |
| PUT | `/api/notifications/read-all` | Marcar todas como leídas |

### Endpoints ADMIN (Requieren Rol ADMIN)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/categories` | Crear nueva categoría |
| PUT | `/api/categories/{id}` | Actualizar categoría |
| DELETE | `/api/categories/{id}` | Eliminar categoría |
| GET | `/api/users/` | Listar todos los usuarios |
| DELETE | `/api/users/{id}` | Eliminar cualquier usuario |
| DELETE | `/api/posts/{id}` | Eliminar cualquier evento |
| DELETE | `/api/comments/{id}` | Eliminar cualquier comentario |

### Endpoints de Dashboard (Públicos)

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/dashboard/top-commented-posts` | Eventos más comentados |
| GET | `/api/dashboard/most-saved-posts` | Eventos más guardados |
| GET | `/api/dashboard/most-active-users` | Usuarios más activos |
| GET | `/api/dashboard/popular-categories` | Categorías populares |
| GET | `/api/dashboard/recent-activity` | Actividad reciente |

## Autenticación

La API usa JWT para autenticación. Para acceder a endpoints protegidos:

1. **Registrarse o iniciar sesión** para obtener el token
2. **Incluir el token** en el header de las peticiones:

```
Authorization: Bearer <tu_token_jwt>
```

### Ejemplo de Login

```bash
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "usuario",
    "password": "contraseña"
  }'
```

Respuesta:
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

## Documentación API (Swagger)

Una vez ejecutada la aplicación, accede a la documentación interactiva:

- **Swagger UI**: http://localhost:8080/swagger-ui/index.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

Swagger incluye:
- Documentación completa de todos los endpoints
- Autenticación JWT integrada (botón "Authorize")
- Pruebas interactivas de la API
- Esquemas de request/response
- Ejemplos de uso



---

## Licencia

Este proyecto es parte de **ViveMedellin** - Plataforma para descubrir Medellín.
