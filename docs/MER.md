## Modelo Entidad-Relación (MER) - ViveMedellin Backend

Este documento describe el MER refinado a partir de las entidades JPA encontradas en `com.vivemedellin.models`.

Resumen de entidades principales (mapadas en el código):

- User (users)
  - PK: id (integer)
  - Atributos: name, email (unique), password, about
  - Relaciones: 1 User -> N Post, 1 User -> N Comment, 1 User -> N Role (via user_roles)

- Role (elemento enumerado)
  - Valores: ROLE_USER, ROLE_ADMIN
  - Implementación en BD: tabla auxiliar `user_roles(user_id, role)` con PK compuesta
  - Mapeado como `@ElementCollection` en `User`

- Category (categories)
  - PK: categoryId (integer)
  - Atributos: categoryTitle, categoryDescription
  - Relaciones: 1 Category -> N Post

- Post (post)
  - PK: postId (integer)
  - Atributos: postTitle, content, imageName, creationDate
  - FK: category_id, user_id
  - Relaciones: N Post -> 1 Category, N Post -> 1 User, 1 Post -> N Comment

- Comment (comments)
  - PK: id (integer)
  - Atributos: content
  - FK: post_id, user_id
  - Relaciones: N Comment -> 1 Post, N Comment -> 1 User


Cardinalidades (resumen):
- User 1---* Post
- User 1---* Comment
- Category 1---* Post
- Post 1---* Comment

Notas sobre mapeo JPA detectado:
- `User.roles` está modelado como `@ElementCollection` de `Enum Role` (se generará una tabla auxiliar `user_roles` con columna `role` tipo TEXT/ENUM).
- Las relaciones usan `@ManyToOne` y `@OneToMany` con `CascadeType.ALL` en algunos casos (p. ej. eliminar un `Post` liberará `Comment` por `orphanRemoval=true`).

PlantUML (texto) — puede pegarse en un editor PlantUML para generar el diagrama gráfico:

```plantuml
@startuml ViveMedellin_MER

entity "users" as users {
  *id : integer <<PK>>
  --
  name : varchar(100) NOT NULL
  email : varchar(255) UNIQUE NOT NULL
  password : varchar(255)
  about : text
}

entity "categories" as categories {
  *categoryId : integer <<PK>>
  --
  categoryTitle : varchar(100) NOT NULL
  categoryDescription : text
}

entity "post" as post {
  *postId : integer <<PK>>
  --
  postTitle : varchar(100) NOT NULL
  content : text
  imageName : varchar(255)
  creationDate : date/timestamp
  category_id : integer <<FK>>
  user_id : integer <<FK>>
}

entity "comments" as comments {
  *id : integer <<PK>>
  --
  content : text
  post_id : integer <<FK>>
  user_id : integer <<FK>>
}

entity "user_roles" as user_roles {
  *user_id : integer <<FK, PK>>
  *role : varchar <<PK>>
}

users ||--o{ post : "creates"
users ||--o{ comments : "writes"
categories ||--o{ post : "contains"
post ||--o{ comments : "has"
users ||--o{ user_roles : "has_role"

note right of users
  Tabla principal de usuarios
  - Email único para autenticación
  - Roles asignados via ElementCollection
end note

note right of post
  Tabla de posts/eventos
  - Relación con categoría y usuario
  - Cascade delete de comentarios
end note

note right of comments
  Comentarios en posts
  - FK a post y usuario
  - Orphan removal activo
end note

note right of user_roles
  Mapeo de roles por usuario
  - Generada desde @ElementCollection
end note

@enduml
```

