## Análisis de Volumen de Datos - ViveMedellin Backend

Objetivo: estimar el volumen de datos esperado por entidad identificada (Users, Posts, Comments, Categories) y dar recomendaciones de índices, particionado y archivado.

Advertencia: Los cálculos se basan en suposiciones razonables; ajuste las cifras a los datos reales de producción.

1) Supuestos base
- Tamaño promedio campo `email`: 30 bytes
- Tamaño promedio `name`: 25 bytes
- Tamaño promedio `password` (hash): 60 bytes
- Tamaño promedio `about`: 300 bytes (texto libre)
- Tamaño promedio `post_title`: 60 bytes
- Tamaño promedio `content` de post: 2,000 bytes (varía mucho)
- Tamaño promedio `image_name`: 40 bytes
- Tamaño promedio `comment.content`: 200 bytes

2) Entidades y estimaciones

- Users
  - Filas estimadas (escenario inicial): 10,000 usuarios
  - Row size aproximada (sin índices): id(4) + name(25) + email(30) + password(60) + about(300) + overhead ~ 450 bytes
  - Almacenamiento total: 10k * 450 B ≈ 4.5 MB (datos), index+TOAST/overhead -> ~2x = 9 MB

- Categories
  - Filas: pocas (ej. 20 - 100)
  - Impacto de almacenamiento: despreciable

- Posts
  - Supuesto: 10k usuarios * 5 posts/usuario = 50,000 posts iniciales
  - Row size estimado: post_title(60) + content(2000) + image_name(40) + meta(creation_date, FK) ~ 2.2 KB
  - Almacenamiento: 50k * 2.2 KB ≈ 110 MB (datos). Con índices y TOAST (para contenido grande) -> 200-300 MB

- Comments
  - Supuesto: promedio 3 comentarios/post → 150,000 comments
  - Row size estimado: 200 B + meta → ~300 B
  - Almacenamiento: 150k * 300 B ≈ 45 MB

3) Crecimiento y proyección (ejemplo)
- Tasa de crecimiento mensual de posts: 5% compuesto
- Tras 12 meses (aprox): posts ≈ 50k * (1.05^12) ≈ 89k → almacenamiento proporcional (~2x)

4) Índices y recomendaciones
- Índices obligatorios:
  - `post(post_id) PK` (por defecto)
  - `post(category_id)` para consultas por categoría
  - `post(user_id)` para consultas por usuario
  - `comments(post_id)` para obtener comentarios por post
  - Índice de texto para búsqueda: `to_tsvector` en `post_title` y/o `content` (se sugirió GIN en el script)

- Considerar índices compuestos si las consultas frecuentes usan filtros combinados (p. ej. `(category_id, creation_date)`)

5) Particionado y archivado
- Particionado recomendado si la tabla `post` crece mucho (>5-10M filas): partition by RANGE (creation_date) por mes o por trimestre.
- Archivar posts antiguos (ej. >2 años) a una tabla de histórico o sistema de almacenamiento frío.

6) Recomendaciones de mantenimiento
- VACUUM y autovacuum: configurar apropiadamente para tablas con alta escritura (comments, posts).
- Monitorización de índices y uso: crear alertas cuando índices aumenten por encima de determinados umbrales (p. ej. > 1GB).
- Revisar campos TEXT/BLOB (content, image) y considerar externalizar imágenes a objeto storage y sólo guardar path en la BD (ya se usa `image_name`).

7) Resumen y umbrales operativos
- Escenario inicial (10k usuarios, 50k posts, 150k comments): almacenamiento total de BD (datos + índices) estimado ~400-600 MB.
- Punto de decisión para particionado: cuando posts > 5M o tamaño de la tabla `post` > 20-30 GB.

