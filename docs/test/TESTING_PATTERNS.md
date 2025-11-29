# Patrones y Mejores Prácticas de Testing - ViveMedellin Backend

## 1. Patrón Arrange-Act-Assert (AAA)

Todo test debe seguir este patrón para claridad:

```java
@Test
@DisplayName("Debe crear un usuario y validar sus datos")
public void testCreateUserSuccess() {
    // ARRANGE - Preparar los datos de prueba
    UserDto userDto = new UserDto();
    userDto.setUserName("testuser");
    userDto.setUserEmail("test@example.com");
    userDto.setUserPassword("SecurePass123");

    // ACT - Ejecutar la acción bajo prueba
    UserDto createdUser = userService.createUser(userDto);

    // ASSERT - Verificar los resultados
    assertNotNull(createdUser.getUserId());
    assertEquals("testuser", createdUser.getUserName());
    assertEquals("test@example.com", createdUser.getUserEmail());
}
```

## 2. Pruebas de Controllers con MockMvc

### Prueba de GET (lectura)

```java
@Test
@DisplayName("Debe obtener categoría por ID")
public void testGetCategoryById() throws Exception {
    // Crear datos de prueba
    Category category = new Category();
    category.setCategoryTitle("Música");
    Category saved = categoryRepo.save(category);

    // Hacer request y validar respuesta
    mockMvc.perform(get("/api/categories/{id}", saved.getCategoryId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryTitle", equalTo("Música")))
            .andExpect(jsonPath("$.categoryId", notNullValue()));
}
```

### Prueba de POST (creación)

```java
@Test
@DisplayName("Debe crear una nueva categoría")
public void testCreateCategory() throws Exception {
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryTitle("Deportes");
    categoryDto.setCategoryDescription("Eventos deportivos");

    mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.categoryTitle", equalTo("Deportes")))
            .andExpect(jsonPath("$.categoryId", notNullValue()));
}
```

### Prueba de PUT (actualización)

```java
@Test
@DisplayName("Debe actualizar una categoría")
public void testUpdateCategory() throws Exception {
    Category original = new Category();
    original.setCategoryTitle("Original");
    Category saved = categoryRepo.save(original);

    CategoryDto updateDto = new CategoryDto();
    updateDto.setCategoryTitle("Actualizado");

    mockMvc.perform(put("/api/categories/{id}", saved.getCategoryId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.categoryTitle", equalTo("Actualizado")));
}
```

### Prueba de DELETE (eliminación)

```java
@Test
@DisplayName("Debe eliminar una categoría")
public void testDeleteCategory() throws Exception {
    Category category = new Category();
    category.setCategoryTitle("Para Eliminar");
    Category saved = categoryRepo.save(category);

    mockMvc.perform(delete("/api/categories/{id}", saved.getCategoryId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

    // Verificar que fue eliminado
    mockMvc.perform(get("/api/categories/{id}", saved.getCategoryId())
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
}
```

## 3. Validación de Errores

### Prueba de validación 400 Bad Request

```java
@Test
@DisplayName("Debe rechazar usuario sin email")
public void testCreateUserWithoutEmailFails() throws Exception {
    UserDto invalidUser = new UserDto();
    invalidUser.setUserName("nomail");
    invalidUser.setUserPassword("password123");
    // email es null

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(invalidUser)))
            .andExpect(status().isBadRequest());
}
```

### Prueba de recurso no encontrado 404

```java
@Test
@DisplayName("Debe retornar 404 para usuario inexistente")
public void testGetNonExistentUserReturns404() throws Exception {
    mockMvc.perform(get("/api/users/999999")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
}
```

### Prueba de conflicto 409 Conflict

```java
@Test
@DisplayName("Debe rechazar email duplicado")
public void testCreateUserWithDuplicateEmailFails() throws Exception {
    // Usuario 1 con email test@example.com
    User user1 = new User();
    user1.setUserEmail("test@example.com");
    userRepo.save(user1);

    // Intentar crear usuario 2 con el mismo email
    UserDto duplicateUser = new UserDto();
    duplicateUser.setUserEmail("test@example.com");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(duplicateUser)))
            .andExpect(status().isConflict());
}
```

## 4. Pruebas de Servicios

### Con mocks de repositorios

```java
@SpringBootTest
@ActiveProfiles("test")
public class UserServiceTest {

    @MockBean
    private UserRepo userRepo;

    @Autowired
    private UserService userService;

    @Test
    public void testGetUserById() {
        // Arrange
        User mockUser = new User();
        mockUser.setUserId(1);
        mockUser.setUserName("test");
        when(userRepo.findById(1)).thenReturn(Optional.of(mockUser));

        // Act
        UserDto result = userService.getUserById(1);

        // Assert
        assertNotNull(result);
        assertEquals("test", result.getUserName());
        verify(userRepo, times(1)).findById(1);
    }
}
```

## 5. Pruebas de Repositorios

### Queries personalizadas

```java
@DataJpaTest
public class UserRepositoryTest {

    @Autowired
    private UserRepo userRepo;

    @Test
    public void testFindByEmail() {
        User user = new User();
        user.setUserEmail("unique@example.com");
        user.setUserName("testuser");
        userRepo.save(user);

        Optional<User> found = userRepo.findByUserEmail("unique@example.com");

        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUserName());
    }

    @Test
    public void testEmailUniqueness() {
        User user1 = new User();
        user1.setUserEmail("duplicate@example.com");
        userRepo.save(user1);

        User user2 = new User();
        user2.setUserEmail("duplicate@example.com");

        assertThrows(DataIntegrityViolationException.class, () -> {
            userRepo.save(user2);
            userRepo.flush();
        });
    }
}
```

## 6. Pruebas Parametrizadas

Para probar múltiples casos con la misma lógica:

```java
@ParameterizedTest
@ValueSource(strings = { "valid@example.com", "test.user@domain.co", "user+tag@example.com" })
@DisplayName("Debe aceptar emails válidos")
public void testValidEmails(String email) throws Exception {
    UserDto userDto = new UserDto();
    userDto.setUserName("testuser");
    userDto.setUserEmail(email);
    userDto.setUserPassword("password123");

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated());
}

@ParameterizedTest
@ValueSource(strings = { "invalid", "@example.com", "test@", "test@.com" })
@DisplayName("Debe rechazar emails inválidos")
public void testInvalidEmails(String email) throws Exception {
    UserDto userDto = new UserDto();
    userDto.setUserName("testuser");
    userDto.setUserEmail(email);

    mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isBadRequest());
}
```

## 7. Pruebas con Datos Complejos

### Relaciones entre entidades

```java
@Test
@DisplayName("Debe crear post con categoría y usuario")
public void testCreatePostWithRelations() {
    // Setup - Crear datos relacionados
    Category category = new Category();
    category.setCategoryTitle("Conciertos");
    Category savedCategory = categoryRepo.save(category);

    User user = new User();
    user.setUserName("organizer");
    user.setUserEmail("organizer@example.com");
    User savedUser = userRepo.save(user);

    // Act - Crear post
    Post post = new Post();
    post.setPostTitle("Concierto Medellín");
    post.setPostContent("Concierto en vivo");
    post.setCategory(savedCategory);
    post.setUser(savedUser);
    post.setCreatedAt(LocalDateTime.now());
    Post savedPost = postRepo.save(post);

    // Assert
    assertNotNull(savedPost.getPostId());
    assertEquals("Conciertos", savedPost.getCategory().getCategoryTitle());
    assertEquals("organizer", savedPost.getUser().getUserName());
}
```

## 8. Pruebas de Timestamps

```java
@Test
@DisplayName("Debe registrar timestamps de creación y actualización")
public void testTimestamps() {
    LocalDateTime beforeCreation = LocalDateTime.now();

    Post post = new Post();
    post.setPostTitle("Test");
    post.setCreatedAt(LocalDateTime.now());
    Post savedPost = postRepo.save(post);

    LocalDateTime afterCreation = LocalDateTime.now();

    assertNotNull(savedPost.getCreatedAt());
    assertTrue(savedPost.getCreatedAt().isAfter(beforeCreation) ||
               savedPost.getCreatedAt().isEqual(beforeCreation));
    assertTrue(savedPost.getCreatedAt().isBefore(afterCreation) ||
               savedPost.getCreatedAt().isEqual(afterCreation));
}
```

## 9. Flujos de Integración Completos

```java
@Test
@DisplayName("Flujo: Registrar usuario → Crear categoría → Crear post")
public void testCompleteUserJourney() throws Exception {
    // PASO 1: Registrar usuario
    UserDto userDto = new UserDto();
    userDto.setUserName("newuser");
    userDto.setUserEmail("new@example.com");
    userDto.setUserPassword("password123");

    MvcResult userResult = mockMvc.perform(post("/api/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userDto)))
            .andExpect(status().isCreated())
            .andReturn();

    String userResponse = userResult.getResponse().getContentAsString();
    int userId = objectMapper.readTree(userResponse).get("userId").asInt();

    // PASO 2: Crear categoría
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryTitle("Mi Categoría");
    categoryDto.setCategoryDescription("Para mis eventos");

    MvcResult categoryResult = mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
            .andExpect(status().isCreated())
            .andReturn();

    String categoryResponse = categoryResult.getResponse().getContentAsString();
    int categoryId = objectMapper.readTree(categoryResponse).get("categoryId").asInt();

    // PASO 3: Crear post en esa categoría
    PostDto postDto = new PostDto();
    postDto.setPostTitle("Mi Evento");
    postDto.setPostContent("Descripción del evento");
    postDto.setCategoryId((long) categoryId);

    mockMvc.perform(post("/api/posts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(postDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.postTitle", equalTo("Mi Evento")))
            .andExpect(jsonPath("$.categoryId", equalTo(categoryId)));
}
```

## 10. Pruebas de Seguridad (si aplica)

```java
@Test
@DisplayName("Debe rechazar request sin autenticación")
public void testUnauthenticatedAccessDenied() throws Exception {
    mockMvc.perform(get("/api/posts/protected")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isUnauthorized());
}

@Test
@WithMockUser(username = "testuser", roles = {"USER"})
@DisplayName("Debe permitir acceso con autenticación")
public void testAuthenticatedAccessAllowed() throws Exception {
    mockMvc.perform(get("/api/users/profile")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());
}
```

## 11. Limpieza de Datos Entre Tests

```java
@BeforeEach
public void setUp() {
    // Limpiar todas las tablas para empezar fresco
    postRepo.deleteAll();
    commentRepo.deleteAll();
    userRepo.deleteAll();
    categoryRepo.deleteAll();
}

@AfterEach
public void tearDown() {
    // Limpiar después de cada test (opcional, BeforeEach es suficiente)
    postRepo.deleteAll();
    categoryRepo.deleteAll();
}
```

## 12. Logging en Tests

```java
@Test
@DisplayName("Prueba con logging")
public void testWithLogging() {
    logger.info("=== Test iniciado ===");

    Category category = new Category();
    category.setCategoryTitle("Música");
    logger.debug("Categoría creada: " + category.getCategoryTitle());

    Category saved = categoryRepo.save(category);
    logger.info("Categoría guardada con ID: " + saved.getCategoryId());

    assertNotNull(saved.getCategoryId());
    logger.info("=== Test completado exitosamente ===");
}
```

## Resumen de Tipos de Pruebas

| Tipo        | Scope                 | Velocidad     | Caso de Uso               |
| ----------- | --------------------- | ------------- | ------------------------- |
| Unit        | Método individual     | ⚡ Muy rápido | Lógica aislada            |
| Integration | Múltiples componentes | 🚗 Lento      | Interacción entre capas   |
| E2E         | Flujo completo        | 🚌 Muy lento  | Validar flujos de usuario |

## Comandos Útiles

```bash
# Ejecutar un test específico
mvn test -Dtest=CategoryControllerIntegrationTest#testCreateCategorySuccess

# Ejecutar con salida detallada
mvn test -X

# Ejecutar en modo offline
mvn test -o

# Ejecutar solo tests que fallaron
mvn test --fail-at-end

# Generar reporte de pruebas
mvn surefire-report:report
```

---

**Última actualización:** November 28, 2025
