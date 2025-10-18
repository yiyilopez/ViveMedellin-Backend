# Documentación de API - ViveMedellín

## Acceso a la Documentación Interactiva

Una vez que el backend esté corriendo, puedes acceder a la documentación interactiva de Swagger en:

### URLs de Documentación

- **Swagger UI (Interfaz Interactiva)**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **Swagger UI Alternativo**: http://localhost:8080/swagger-ui/index.html

---

## ¿Qué es Swagger/OpenAPI?

**Swagger UI** es una interfaz web interactiva que te permite:

**Ver todos los endpoints** disponibles en la API  
**Probar requests** directamente desde el navegador  
**Ver schemas** de request/response  
**Autenticarte con JWT** y probar endpoints protegidos  
**Explorar** parámetros, códigos de respuesta y ejemplos  

---

## Cómo Autenticarte en Swagger

### Paso 1: Obtener un Token JWT

1. Abre Swagger UI: http://localhost:8080/swagger-ui.html
2. Busca la sección **"Usuarios"**
3. Expande el endpoint **POST /api/users/login**
4. Haz clic en **"Try it out"**
5. Ingresa tus credenciales:
   ```json
   {
     "username": "tu-email@ejemplo.com",
     "password": "tu-password"
   }
   ```
6. Haz clic en **"Execute"**
7. **Copia el token** de la respuesta (campo `token`)

### Paso 2: Autorizar en Swagger

1. En la parte superior derecha de Swagger UI, haz clic en el botón **"Authorize" 🔒**
2. Pega tu token en el formato: `Bearer TU_TOKEN_AQUÍ`
   - **IMPORTANTE**: Incluye la palabra `Bearer` seguida de un espacio antes del token
   - Ejemplo: `Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`
3. Haz clic en **"Authorize"**
4. Haz clic en **"Close"**

---

## 📂 Secciones de la API

### **Usuarios**
- `POST /api/users/register` - Registrar nuevo usuario
- `POST /api/users/login` - Iniciar sesión (obtener JWT)
- `POST /api/users/logout` - Cerrar sesión
- `GET /api/users/` - Listar todos los usuarios (Admin)
- `GET /api/users/{userId}` - Obtener usuario por ID
- `PUT /api/users/{userId}` - Actualizar usuario
- `DELETE /api/users/{userId}` - Eliminar usuario (Admin)

### 🎉 **Posts (Eventos)**
- `POST /api/user/{userId}/category/{categoryId}/posts` - Crear evento
- `GET /api/posts` - Listar todos los eventos (paginado)
- `GET /api/posts/{postId}` - Obtener evento por ID
- `PUT /api/posts/{postId}` - Actualizar evento
- `DELETE /api/posts/{postId}` - Eliminar evento
- `GET /api/category/{categoryId}/posts` - Eventos por categoría
- `GET /api/user/{userId}/posts` - Eventos por usuario
- `GET /api/posts/search/{keywords}` - Buscar eventos

### **Categorías**
- `POST /api/categories/` - Crear categoría (Admin)
- `GET /api/categories/` - Listar todas las categorías
- `GET /api/categories/{categoryId}` - Obtener categoría por ID
- `PUT /api/categories/{categoryId}` - Actualizar categoría (Admin)
- `DELETE /api/categories/{categoryId}` - Eliminar categoría (Admin)

### **Comentarios**
- `POST /api/posts/{postId}/comments` - Crear comentario
- `POST /api/comments/{commentId}/replies` - Responder a comentario
- `GET /api/posts/{postId}/comments` - Comentarios de un evento
- `GET /api/comments/{commentId}` - Obtener comentario por ID
- `DELETE /api/comments/{commentId}` - Eliminar comentario

### **Notificaciones**
- `GET /api/notifications/{userId}` - Obtener notificaciones del usuario
- `GET /api/notifications/{userId}/unread-count` - Contar notificaciones no leídas
- `PUT /api/notifications/{notificationId}/read` - Marcar como leída
- `PUT /api/notifications/{userId}/mark-all-read` - Marcar todas como leídas

### **Posts Guardados**
- `POST /api/saved-posts/save/{postId}` - Guardar evento
- `DELETE /api/saved-posts/unsave/{postId}` - Dejar de guardar evento
- `GET /api/saved-posts/user/{userId}` - Posts guardados del usuario
- `GET /api/saved-posts/is-saved/{postId}` - Verificar si está guardado

### **Dashboard**
- `GET /api/dashboard/activity/{userId}` - Actividad del usuario
- `GET /api/dashboard/popular-posts` - Posts más populares
- `GET /api/dashboard/recent-activity/{userId}` - Actividad reciente
- `GET /api/dashboard/stats/{userId}` - Estadísticas del usuario
- `GET /api/dashboard/trending-posts` - Posts en tendencia
- `GET /api/dashboard/top-categories` - Categorías principales
- `GET /api/dashboard/engagement/{userId}` - Engagement del usuario

---

## Ejemplos de Uso

### Ejemplo 1: Crear un Usuario

**Request:**
```bash
POST http://localhost:8080/api/users/register
Content-Type: application/json

{
  "name": "Juan Pérez",
  "email": "juan@ejemplo.com",
  "password": "password123",
  "about": "Amante de eventos en Medellín"
}
```

**Response:**
```json
{
  "id": 1,
  "name": "Juan Pérez",
  "email": "juan@ejemplo.com",
  "about": "Amante de eventos en Medellín",
  "roles": ["ROLE_USER"]
}
```

### Ejemplo 2: Crear un Evento

**Request:**
```bash
POST http://localhost:8080/api/user/1/category/2/posts
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: multipart/form-data

postTitle: Concierto de Rock en El Poblado
content: Gran concierto de rock este sábado 8pm
image: [archivo de imagen]
```

### Ejemplo 3: Buscar Eventos

**Request:**
```bash
GET http://localhost:8080/api/posts/search/concierto?pageNumber=0&pageSize=10
```

---

## 🔧 Códigos de Respuesta HTTP

| Código | Significado | Descripción |
|--------|-------------|-------------|
| 200 | OK | Solicitud exitosa |
| 201 | Created | Recurso creado exitosamente |
| 400 | Bad Request | Datos de entrada inválidos |
| 401 | Unauthorized | No autenticado (token inválido/expirado) |
| 403 | Forbidden | No autorizado (sin permisos) |
| 404 | Not Found | Recurso no encontrado |
| 409 | Conflict | Conflicto (ej: email ya existe) |
| 500 | Internal Server Error | Error del servidor |

---

## Características de Swagger UI

### Filtros y Ordenamiento
- **Ordenar por método**: GET, POST, PUT, DELETE
- **Ordenar por tag**: Alfabéticamente
- **Buscar endpoints**: Usa el campo de búsqueda

### Probar Endpoints
1. Expande el endpoint que quieres probar
2. Haz clic en **"Try it out"**
3. Completa los parámetros requeridos
4. Haz clic en **"Execute"**
5. Ve la respuesta en tiempo real

### Schemas
- Ve los modelos de datos en la sección **"Schemas"** al final
- Explora la estructura de UserDto, PostDto, CategoryDto, etc.

---

## Probar con Postman

Si prefieres usar Postman, puedes importar la especificación OpenAPI:

1. Abre Postman
2. Ve a **Import**
3. Ingresa la URL: `http://localhost:8080/v3/api-docs`
4. Haz clic en **Import**

Ahora tendrás toda la colección de endpoints en Postman 🎉

---

## Iniciar el Backend

```bash
cd /Users/yiyi/ViveMedellin-Backend/vivemedellinbackend
export JAVA_HOME=$(/usr/libexec/java_home -v 17)
mvn spring-boot:run
```

O ejecuta el JAR compilado:

```bash
java -jar target/vivemedellinbackend-0.0.1-SNAPSHOT.jar
```

Una vez iniciado, accede a: **http://localhost:8080/swagger-ui.html** 🎉


