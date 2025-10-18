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

### Sistema de Usuarios
- Registro y autenticación con JWT
- Perfiles de usuario personalizables
- Sistema de roles (USER, ADMIN)
- Gestión de sesiones seguras

### Interacción Social
- Comentarios en eventos
- Sistema de reacciones
- Participación comunitaria

### Seguridad
- Autenticación basada en JWT (JSON Web Tokens)
- Encriptación de contraseñas con BCrypt
- Control de acceso basado en roles
- CORS configurado para frontend

## Arquitectura

Arquitectura Monolítica en Capas

```
┌─────────────────────────────────────────┐
│   CAPA DE PRESENTACIÓN                  │
│   controllers/                          │
│   - PostController (Eventos)            │
│   - UserController                      │
│   - CategoryController                  │
│   - CommentController                   │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   CAPA DE NEGOCIO                       │
│   services/                             │
│   - PostService (Lógica de eventos)     │
│   - UserService                         │
│   - CategoryService                     │
│   - CommentService                      │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   CAPA DE ACCESO A DATOS                │
│   repositories/ (Spring Data JPA)       │
│   - PostRepo                            │
│   - UserRepo                            │
│   - CategoryRepo                        │
│   - CommentRepo                         │
└─────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────┐
│   BASE DE DATOS                         │
│   PostgreSQL                            │
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
│   │   └── AppConstants.java          # Constantes
│   ├── controllers/                    # Endpoints REST
│   │   ├── PostController.java        # API de eventos
│   │   ├── UserController.java        # API de usuarios
│   │   ├── CategoryController.java    # API de categorías
│   │   └── CommentController.java     # API de comentarios
│   ├── models/                        # Entidades JPA
│   │   ├── Post.java                  # Modelo de evento
│   │   ├── User.java                  # Modelo de usuario
│   │   ├── Category.java              # Modelo de categoría
│   │   ├── Comment.java               # Modelo de comentario
│   │   └── Role.java                  # Enum de roles
│   ├── repositories/                  # Interfaces JPA
│   ├── services/                      # Lógica de negocio
│   ├── payloads/                      # DTOs
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

### 2. Configurar la base de datos

Crear una base de datos PostgreSQL:

```sql
CREATE DATABASE vivemedellin_db;
```

### 3. Configurar `application.properties`

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


## Endpoints Principales

### Endpoints Públicos

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/users/register` | Registrar nuevo usuario |
| POST | `/api/users/login` | Iniciar sesión |
| GET | `/api/posts` | Listar todos los eventos |
| GET | `/api/posts/{id}` | Obtener evento específico |
| GET | `/api/categories` | Listar categorías |
| GET | `/api/posts/{id}/comments` | Ver comentarios de un evento |

### Endpoints Protegidos (Requieren Autenticación)

| Método | Endpoint | Rol Requerido | Descripción |
|--------|----------|---------------|-------------|
| POST | `/api/posts` | USER | Crear nuevo evento |
| PUT | `/api/posts/{id}` | USER | Actualizar evento |
| DELETE | `/api/posts/{id}` | USER/ADMIN | Eliminar evento |
| POST | `/api/posts/{id}/comments` | USER | Comentar en evento |
| PUT | `/api/users/{id}` | USER | Actualizar perfil |
| DELETE | `/api/users/{id}` | ADMIN | Eliminar usuario |
| GET | `/api/users` | ADMIN | Listar todos los usuarios |

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

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Contribución

Las contribuciones son bienvenidas. Por favor:

1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## Licencia

Este proyecto es parte de **ViveMedellin** - Plataforma para descubrir Medellín.

## Base URL
```
http://localhost:8080/api
```

## Autenticación

La API utiliza JWT (JSON Web Tokens) para autenticación. Para acceder a endpoints protegidos, incluye el token en el header:

```http
Authorization: Bearer <tu_token_jwt>
```

---

## Endpoints de Usuarios

### Registrar Usuario
Crea una nueva cuenta de usuario.

```http
POST /users/register
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "username": "juanperez",
  "password": "password123",
  "about": "Amante de los eventos culturales en Medellín"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "username": "juanperez",
  "about": "Amante de los eventos culturales en Medellín",
  "roles": ["ROLE_USER"]
}
```

---

### Iniciar Sesión
Autentica un usuario y devuelve un token JWT.

```http
POST /users/login
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "juanperez",
  "password": "password123"
}
```

**Response:** `200 OK`
```json
{
  "message": "Login successful",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

### Actualizar Perfil
Actualiza la información del usuario autenticado.

```http
PUT /users/{userId}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "name": "Juan Pérez Actualizado",
  "about": "Nueva descripción"
}
```

**Response:** `200 OK`

---

### Obtener Usuario por ID
Obtiene información de un usuario específico.

```http
GET /users/{userId}
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@example.com",
  "username": "juanperez",
  "about": "Amante de los eventos culturales"
}
```

---

## Endpoints de Eventos (Posts)

### Listar Todos los Eventos
Obtiene una lista paginada de todos los eventos publicados.

```http
GET /posts?pageNumber=0&pageSize=10&sortBy=createdDate&sortDir=desc
```

**Query Parameters:**
- `pageNumber` (opcional): Número de página (default: 0)
- `pageSize` (opcional): Elementos por página (default: 10)
- `sortBy` (opcional): Campo para ordenar (default: createdDate)
- `sortDir` (opcional): Dirección del ordenamiento (asc/desc, default: desc)

**Response:** `200 OK`
```json
{
  "content": [
    {
      "postId": 1,
      "title": "Festival de Jazz en Medellín",
      "content": "Evento musical con artistas internacionales...",
      "imageName": "jazz-festival.jpg",
      "addedDate": "2025-10-15T10:30:00",
      "category": {
        "categoryId": 1,
        "categoryTitle": "Música",
        "categoryDescription": "Eventos musicales"
      },
      "user": {
        "id": 1,
        "name": "Juan Pérez"
      }
    }
  ],
  "pageNumber": 0,
  "pageSize": 10,
  "totalElements": 50,
  "totalPages": 5,
  "lastPage": false
}
```

---

### Obtener Evento por ID
Obtiene detalles completos de un evento específico.

```http
GET /posts/{postId}
```

**Response:** `200 OK`
```json
{
  "postId": 1,
  "title": "Festival de Jazz en Medellín",
  "content": "Descripción detallada del evento musical con artistas nacionales e internacionales. Fecha: 20 de octubre, Lugar: Teatro Metropolitano...",
  "imageName": "jazz-festival.jpg",
  "addedDate": "2025-10-15T10:30:00",
  "category": {
    "categoryId": 1,
    "categoryTitle": "Música"
  },
  "user": {
    "id": 1,
    "name": "Juan Pérez",
    "username": "juanperez"
  }
}
```

---

### Crear Evento
Publica un nuevo evento en la plataforma.

```http
POST /posts
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Caminata Ecológica en el Arví",
  "content": "Únete a nuestra caminata guiada por el Parque Arví. Exploraremos senderos naturales y aprenderemos sobre la biodiversidad local.",
  "categoryId": 5,
  "userId": 1
}
```

**Response:** `201 Created`
```json
{
  "postId": 15,
  "title": "Caminata Ecológica en el Arví",
  "content": "Únete a nuestra caminata...",
  "imageName": "default.png",
  "addedDate": "2025-10-17T14:20:00",
  "category": {
    "categoryId": 5,
    "categoryTitle": "Naturaleza"
  },
  "user": {
    "id": 1,
    "name": "Juan Pérez"
  }
}
```

---

### Actualizar Evento
Modifica un evento existente.

```http
PUT /posts/{postId}
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "title": "Caminata Ecológica ACTUALIZADA",
  "content": "Contenido actualizado..."
}
```

**Response:** `200 OK`

---

### Eliminar Evento
Elimina un evento de la plataforma.

```http
DELETE /posts/{postId}
Authorization: Bearer <token>
```

**Response:** `200 OK`
```json
{
  "message": "Post deleted successfully",
  "success": true
}
```

---

### Buscar Eventos
Busca eventos por palabra clave.

```http
GET /posts/search?keyword=jazz
```

**Response:** `200 OK` (Array de eventos)

---

### Obtener Eventos por Categoría
Lista eventos de una categoría específica.

```http
GET /category/{categoryId}/posts?pageNumber=0&pageSize=10
```

**Response:** `200 OK` (Respuesta paginada similar a /posts)

---

### Subir Imagen de Evento
Sube una imagen para un evento.

```http
POST /posts/image/upload/{postId}
Authorization: Bearer <token>
Content-Type: multipart/form-data
```

**Form Data:**
- `image`: Archivo de imagen (JPG, PNG)

**Response:** `200 OK`
```json
{
  "imageName": "abc123.jpg",
  "message": "Image uploaded successfully"
}
```

---

## Endpoints de Categorías

### Listar Todas las Categorías
Obtiene todas las categorías de eventos disponibles.

```http
GET /categories
```

**Response:** `200 OK`
```json
[
  {
    "categoryId": 1,
    "categoryTitle": "Música",
    "categoryDescription": "Conciertos, festivales y eventos musicales"
  },
  {
    "categoryId": 2,
    "categoryTitle": "Deportes",
    "categoryDescription": "Eventos deportivos y actividades físicas"
  },
  {
    "categoryId": 3,
    "categoryTitle": "Cultura",
    "categoryDescription": "Teatro, danza, exposiciones culturales"
  }
]
```

---

### Crear Categoría
Crea una nueva categoría (solo ADMIN).

```http
POST /categories
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "categoryTitle": "Gastronomía",
  "categoryDescription": "Eventos culinarios y gastronómicos"
}
```

**Response:** `201 Created`

---

### Actualizar Categoría
Actualiza una categoría existente (solo ADMIN).

```http
PUT /categories/{categoryId}
Authorization: Bearer <token>
Content-Type: application/json
```

---

### Eliminar Categoría
Elimina una categoría (solo ADMIN).

```http
DELETE /categories/{categoryId}
Authorization: Bearer <token>
```

---

## Endpoints de Comentarios

### Obtener Comentarios de un Evento
Lista todos los comentarios de un evento específico.

```http
GET /posts/{postId}/comments
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "content": "¡Excelente evento! Muy recomendado.",
    "user": {
      "id": 2,
      "name": "María García",
      "username": "mariagarcia"
    },
    "createdDate": "2025-10-16T15:30:00"
  }
]
```

---

### Crear Comentario
Agrega un comentario a un evento.

```http
POST /posts/{postId}/comments
Authorization: Bearer <token>
Content-Type: application/json
```

**Request Body:**
```json
{
  "content": "¡Me encantó este evento! Definitivamente asistiré."
}
```

**Response:** `201 Created`
```json
{
  "id": 10,
  "content": "¡Me encantó este evento!...",
  "user": {
    "id": 1,
    "name": "Juan Pérez"
  },
  "createdDate": "2025-10-17T14:25:00"
}
```

---

### Eliminar Comentario
Elimina un comentario (propio o siendo ADMIN).

```http
DELETE /comments/{commentId}
Authorization: Bearer <token>
```

**Response:** `200 OK`

---

## Códigos de Error

| Código | Descripción |
|--------|-------------|
| `400` | Bad Request - Datos inválidos |
| `401` | Unauthorized - No autenticado |
| `403` | Forbidden - Sin permisos |
| `404` | Not Found - Recurso no encontrado |
| `409` | Conflict - Email/Username ya existe |
| `500` | Internal Server Error |

**Ejemplo de Error:**
```json
{
  "message": "Resource not found: Post with id 999",
  "timestamp": "2025-10-17T14:30:00",
  "details": "/api/posts/999"
}
```

---

## Testing con cURL

### Ejemplo: Registro y Login

```bash
# 1. Registrarse
curl -X POST http://localhost:8080/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Test User",
    "email": "test@example.com",
    "username": "testuser",
    "password": "password123"
  }'

# 2. Login
curl -X POST http://localhost:8080/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "password": "password123"
  }'

# 3. Listar eventos (guarda el token de la respuesta anterior)
curl -X GET http://localhost:8080/api/posts \
  -H "Authorization: Bearer <tu_token>"
```

---

<div align="center">
Para más información, visita la documentación Swagger en: http://localhost:8080/swagger-ui.html
</div>
