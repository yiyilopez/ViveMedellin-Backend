# ViveMedellin - Modelo de Dominio

## Visión General

**ViveMedellin** es una plataforma inteligente diseñada para conectar a las personas con los eventos, actividades y lugares de interés en Medellín. Aunque técnicamente utiliza una estructura de "posts", en el contexto de la aplicación, cada "post" representa un **evento o actividad** en la ciudad.

## Conceptos Clave

### Eventos (Posts)
Los "posts" en el sistema representan eventos, actividades o lugares de interés en Medellín:
- Conciertos y presentaciones musicales
- Eventos deportivos
- Actividades culturales
- Exposiciones de arte
- Eventos gastronómicos
- Tours y recorridos
- Actividades al aire libre
- Lugares turísticos

### Usuarios
- **Usuarios Regulares (USER)**: Pueden descubrir eventos, comentar, y participar en la comunidad
- **Administradores (ADMIN)**: Gestionan el contenido, usuarios, y moderan la plataforma

### Categorías
Organizan los eventos por tipo:
- Música y Entretenimiento
- Deportes
- Arte y Cultura
- Gastronomía
- Naturaleza y Aventura
- Educación
- Festivales y Celebraciones
- Sitios Históricos

### Comentarios
Permiten la interacción social:
- Opiniones sobre eventos
- Recomendaciones
- Experiencias compartidas
- Preguntas y respuestas

## Flujo de Usuario

```
1. DESCUBRIMIENTO
   ↓
   Usuario explora eventos por categoría o búsqueda
   
2. INTERACCIÓN
   ↓
   Usuario ve detalles del evento, ubicación, comentarios
   
3. PARTICIPACIÓN
   ↓
   Usuario comenta, comparte experiencias, recomienda
   
4. PERSONALIZACIÓN
   ↓
   Sistema aprende preferencias del usuario (futuro)
```

## Entidades Principales

### Event (Post)
```
- ID único
- Título del evento
- Descripción detallada
- Categoría
- Fecha y hora
- Ubicación
- Imagen del evento
- Autor/Organizador
- Fecha de publicación
- Comentarios asociados
```

### User
```
- ID único
- Nombre de usuario
- Email
- Contraseña (encriptada)
- Rol (USER/ADMIN)
- Fecha de registro
- Eventos publicados
```

### Category
```
- ID único
- Nombre de la categoría
- Descripción
- Eventos asociados
```

### Comment
```
- ID único
- Contenido del comentario
- Autor
- Evento asociado
- Fecha de publicación
```

## Características Futuras (Roadmap)

### Fase 1 - Actual
- [x] Gestión de eventos (CRUD)
- [x] Sistema de usuarios con roles
- [x] Autenticación JWT
- [x] Comentarios en eventos
- [x] Categorización de eventos

### Fase 2 - Próximas Funcionalidades
- [ ] Sistema de calificaciones (ratings)
- [ ] Favoritos/Guardados
- [ ] Filtros avanzados (por fecha, ubicación, precio)
- [ ] Mapa interactivo de eventos
- [ ] Notificaciones de eventos cercanos
- [ ] Sistema de tags adicionales

### Fase 3 - Personalización Inteligente
- [ ] Recomendaciones basadas en IA
- [ ] Historial de eventos asistidos
- [ ] Perfil de preferencias del usuario
- [ ] Sistema de amigos/seguimiento
- [ ] Feed personalizado

### Fase 4 - Integración Social
- [ ] Compartir en redes sociales
- [ ] Chat entre usuarios
- [ ] Grupos de interés
- [ ] Sistema de verificación de eventos
- [ ] Integración con plataformas de tickets

## Arquitectura de Datos

```
User ─┬─── Post (Event) ─── Comment
      │         │
      │         └─── Category
      │
      └─── Comment
```

## Buenas Prácticas

### Nomenclatura en el Código
Aunque internamente usamos "Post", la documentación orientada al usuario debe referirse a "Eventos":
- `PostController` → Maneja eventos
- `PostService` → Lógica de negocio de eventos
- `PostDto` → Representa datos de eventos

### Contexto de Negocio
- **Post** = Evento/Actividad en Medellín
- **Category** = Tipo de evento
- **Comment** = Opinión/Experiencia sobre el evento
- **User** = Usuario de la plataforma

---

<div align="center">
ViveMedellin - Descubre lo mejor de Medellín
</div>
