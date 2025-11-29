# Guía de Pruebas de Integración - ViveMedellin Backend

## Introducción

Las pruebas de integración verifican que los componentes del sistema funcionan correctamente cuando interactúan entre sí. Esta guía te explica cómo ejecutar, entender y escribir pruebas de integración para el backend de ViveMedellin.

## Estructura de Pruebas

```
src/test/java/com/vivemedellin/
├── integration/
│   ├── IntegrationTestBase.java              # Clase base para todos los tests
│   ├── controllers/
│   │   ├── AuthControllerIntegrationTest.java
│   │   ├── CategoryControllerIntegrationTest.java
│   │   ├── PostControllerIntegrationTest.java
│   │   └── UserControllerIntegrationTest.java
│   ├── services/
│   │   ├── CategoryServiceIntegrationTest.java
│   │   └── UserServiceIntegrationTest.java
│   ├── repositories/
│   │   ├── CategoryRepositoryIntegrationTest.java
│   │   └── UserRepositoryIntegrationTest.java
│   └── e2e/
│       └── EndToEndFlowIntegrationTest.java  # Pruebas end-to-end
└── resources/
    └── application-test.properties            # Configuración de pruebas
```

## Ejecución de Pruebas

### 1. Ejecutar TODAS las pruebas de integración

```bash
mvn test -Dgroups="integration"
```

### 2. Ejecutar pruebas de un módulo específico

```bash
# Solo tests de Controllers
mvn test -Dtest=*ControllerIntegrationTest

# Solo tests de Services
mvn test -Dtest=*ServiceIntegrationTest

# Solo tests de Repositories
mvn test -Dtest=*RepositoryIntegrationTest

# Solo tests E2E
mvn test -Dtest=EndToEndFlowIntegrationTest
```

### 3. Ejecutar un test específico

```bash
mvn test -Dtest=CategoryControllerIntegrationTest
```

### 4. Ejecutar tests con reporte de cobertura

```bash
mvn clean test jacoco:report
# El reporte se genera en: target/site/jacoco/index.html
```

### 5. Ejecutar tests en paralelo (más rápido)

```bash
mvn test -T 1C  # 1 thread por core del procesador
```

## Capas Probadas

### 1. **Capa de Controllers**

Verifica que los endpoints HTTP funcionan correctamente.

**Archivos:**

- `AuthControllerIntegrationTest.java`
- `CategoryControllerIntegrationTest.java`
- `PostControllerIntegrationTest.java`
- `UserControllerIntegrationTest.java`

**Lo que prueban:**

- ✅ Códigos de estado HTTP correctos (200, 201, 404, 400)
- ✅ Serialización/Deserialización de JSON
- ✅ Validación de entrada
- ✅ Mapeo de rutas

### 2. **Capa de Services**

Verifica la lógica de negocio.

**Archivos:**

- `CategoryServiceIntegrationTest.java`
- `UserServiceIntegrationTest.java`

**Lo que prueban:**

- ✅ Lógica de creación de entidades
- ✅ Actualización de datos
- ✅ Eliminación de datos
- ✅ Validaciones de negocio
- ✅ Manejo de excepciones

### 3. **Capa de Repositories**

Verifica las operaciones de base de datos.

**Archivos:**

- `CategoryRepositoryIntegrationTest.java`
- `UserRepositoryIntegrationTest.java`

**Lo que prueban:**

- ✅ Persistencia de datos
- ✅ Queries personalizadas
- ✅ Búsqueda por ID
- ✅ Búsqueda por otros campos
- ✅ Restricciones de base de datos

### 4. **Pruebas End-to-End (E2E)**

Prueban flujos completos del sistema.

**Archivo:**

- `EndToEndFlowIntegrationTest.java`

**Lo que prueban:**

- ✅ Flujo completo: Crear usuario → Crear categoría → Crear evento
- ✅ CRUD completo de entidades
- ✅ Validaciones de datos en el flujo
- ✅ Integración entre múltiples capas

## Configuración de Pruebas

### Base de Datos de Prueba

Las pruebas usan **H2** (base de datos en memoria) en lugar de PostgreSQL para:

- ✅ Velocidad (sin I/O de red)
- ✅ Aislamiento (cada test obtiene una BD limpia)
- ✅ Simplicidad (sin necesidad de Docker)

**Configuración en `application-test.properties`:**

```properties
# Database - H2 in memory
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
```

### Clase Base de Pruebas

Todas las pruebas heredan de `IntegrationTestBase.java`:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class IntegrationTestBase {
    @Autowired
    protected MockMvc mockMvc;  // Para hacer requests HTTP en tests
}
```

## Ejemplos de Uso

### Prueba de Controller (MockMvc)

```java
@Test
public void testCreateCategory() throws Exception {
    CategoryDto categoryDto = new CategoryDto();
    categoryDto.setCategoryTitle("Deportes");

    mockMvc.perform(post("/api/categories")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(categoryDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.categoryTitle", equalTo("Deportes")));
}
```

### Prueba de Service

```java
@Test
public void testCreateCategory() {
    CategoryDto dto = new CategoryDto();
    dto.setCategoryTitle("Música");

    CategoryDto created = categoryService.createCategory(dto);

    assertNotNull(created.getCategoryId());
    assertEquals("Música", created.getCategoryTitle());
}
```

### Prueba de Repository

```java
@Test
public void testFindByEmail() {
    User user = new User();
    user.setUserEmail("test@example.com");
    userRepo.save(user);

    Optional<User> found = userRepo.findByUserEmail("test@example.com");
    assertTrue(found.isPresent());
}
```

## Mejores Prácticas

### 1. Nombres Descriptivos

```java
@DisplayName("Debe crear una categoría exitosamente")
public void testCreateCategorySuccess() { ... }
```

### 2. Usar @BeforeEach para Setup

```java
@BeforeEach
public void setUp() {
    categoryRepo.deleteAll();  // Base de datos limpia
}
```

### 3. Assertions Claros

```java
assertNotNull(result);                    // No null
assertEquals("Expected", result.getValue()); // Valor correcto
assertTrue(condition);                    // Verdadero
assertThrows(Exception.class, () -> {});  // Lanza excepción
```

### 4. Pruebas Independientes

Cada test debe funcionar independientemente. No confíes en el estado de otros tests.

### 5. Un Assert por Concepto

```java
// ✅ Bien
mockMvc.perform(post("/api/categories")...)
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title", equalTo("Test")));

// ❌ Evitar demasiados asserts en un test
```

## Cobertura de Código

Para generar un reporte de cobertura:

```bash
mvn clean test jacoco:report
```

Abre el reporte:

```bash
# Linux/Mac
open target/site/jacoco/index.html

# Windows
start target/site/jacoco/index.html
```

**Objetivo de cobertura:**

- Services: 80%+
- Controllers: 75%+
- Repositories: 80%+

## Troubleshooting

### Problema: Tests fallan con "DataIntegrityViolationException"

**Causa:** Datos duplicados o restricciones de BD
**Solución:** Asegúrate de limpiar en `@BeforeEach`

```java
@BeforeEach
public void setUp() {
    userRepo.deleteAll();  // Limpia todos los usuarios
}
```

### Problema: Port ya en uso

**Causa:** Otro servidor corriendo en el mismo puerto
**Solución:** Spring usa puerto aleatorio por defecto:

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
```

### Problema: Tests lentos

**Solución:**

1. Usa `@DataJpaTest` para solo repository tests
2. Corre tests en paralelo: `mvn test -T 1C`
3. Usa H2 (ya está configurado)

## Integración Continua

Para CI/CD, ejecuta:

```bash
# Full test suite
mvn clean test

# Con análisis de calidad
mvn clean test sonar:sonar

# Con cobertura
mvn clean test jacoco:report
```

## Extensión: Agregar Nuevas Pruebas

### Template para nuevo Controller Test

```java
@DisplayName("Pruebas de Integración - NuevoController")
public class NuevoControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NuevoRepo nuevoRepo;

    @BeforeEach
    public void setUp() {
        nuevoRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe hacer algo exitosamente")
    public void testFunctionality() throws Exception {
        // Arrange
        NuevoDto dto = new NuevoDto();
        dto.setField("value");

        // Act & Assert
        mockMvc.perform(post("/api/nuevo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }
}
```

## Dependencias Utilizadas

Las siguientes dependencias fueron añadidas al `pom.xml`:

```xml
<!-- Spring Boot Test -->
<spring-boot-starter-test>

<!-- TestContainers (para PostgreSQL en tests) -->
<spring-boot-testcontainers>
<testcontainers-postgresql>

<!-- Security Testing -->
<spring-security-test>

<!-- H2 Database (en memoria) -->
<h2>

<!-- REST Assured (para testing APIs REST) -->
<rest-assured>
```

## Documentación Adicional

- [Spring Boot Testing](https://spring.io/guides/gs/testing-web/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/)
- [Mockito Documentation](https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html)
- [Spring Test Documentation](https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html)

## Contacto y Soporte

Para preguntas sobre las pruebas:

- Revisa los ejemplos en los archivos de test
- Consulta la documentación de Spring Boot
- Abre un issue en el repositorio

---

**Última actualización:** November 28, 2025
