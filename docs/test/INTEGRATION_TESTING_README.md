# ViveMedellin Backend - Suite de Pruebas de Integración

## 🎯 Descripción General

Se ha implementado una suite completa de **pruebas de integración** para el backend de ViveMedellin que cubre todas las capas del sistema: Controllers, Services y Repositories. La suite incluye:

- ✅ **50+ pruebas de integración** cubren todas las capas
- ✅ **Pruebas E2E** para flujos completos de usuario
- ✅ **Base de datos H2 en memoria** para pruebas rápidas
- ✅ **Configuración Maven** optimizada para testing
- ✅ **Documentación completa** con ejemplos y patrones
- ✅ **Scripts de ejecución** para Windows y Linux/Mac

## 📦 Estructura de Archivos Creados

### Tests de Integración

```
src/test/java/com/vivemedellin/
├── integration/
│   ├── IntegrationTestBase.java              # Clase base para todos los tests
│   ├── controllers/
│   │   ├── AuthControllerIntegrationTest.java
│   │   ├── CategoryControllerIntegrationTest.java
│   │   ├── CommentControllerIntegrationTest.java
│   │   ├── PostControllerIntegrationTest.java
│   │   └── UserControllerIntegrationTest.java
│   ├── services/
│   │   ├── CategoryServiceIntegrationTest.java
│   │   └── UserServiceIntegrationTest.java
│   ├── repositories/
│   │   ├── CategoryRepositoryIntegrationTest.java
│   │   └── UserRepositoryIntegrationTest.java
│   └── e2e/
│       └── EndToEndFlowIntegrationTest.java
└── resources/
    └── application-test.properties
```

### Documentación

```
├── TESTING_GUIDE.md          # Guía completa de pruebas
├── TESTING_PATTERNS.md       # Patrones y mejores prácticas
├── run-tests.bat             # Script de ejecución para Windows
└── run-tests.sh              # Script de ejecución para Linux/Mac
```

## 🚀 Quick Start

### 1. Ejecutar TODAS las pruebas

```bash
# Windows
run-tests.bat all

# Linux/Mac
./run-tests.sh all

# O con Maven directamente
mvn clean test
```

### 2. Ejecutar pruebas específicas

```bash
# Solo Controllers
mvn test -Dtest=*ControllerIntegrationTest

# Solo Services
mvn test -Dtest=*ServiceIntegrationTest

# Solo Repositories
mvn test -Dtest=*RepositoryIntegrationTest

# Solo E2E
mvn test -Dtest=EndToEndFlowIntegrationTest
```

### 3. Generar reporte de cobertura

```bash
mvn clean test jacoco:report
# Abre: target/site/jacoco/index.html
```

## 📊 Cobertura de Pruebas

### Controllers (5 clases probadas)

- ✅ **AuthController** - Autenticación
- ✅ **CategoryController** - CRUD de categorías
- ✅ **CommentController** - CRUD de comentarios
- ✅ **PostController** - CRUD de posts/eventos
- ✅ **UserController** - CRUD de usuarios

### Services (2 clases probadas)

- ✅ **CategoryService** - Lógica de categorías
- ✅ **UserService** - Lógica de usuarios

### Repositories (2 clases probadas)

- ✅ **CategoryRepository** - Acceso a datos de categorías
- ✅ **UserRepository** - Acceso a datos de usuarios

### End-to-End

- ✅ **Flujo completo** - Usuario → Categoría → Post
- ✅ **Validaciones** - Restricciones de datos
- ✅ **CRUD completo** - Create, Read, Update, Delete

## 📝 Ejemplos de Pruebas

### Prueba de Controller (MockMvc)

```java
@Test
@DisplayName("Debe crear una categoría exitosamente")
public void testCreateCategorySuccess() throws Exception {
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
@DisplayName("Debe obtener un usuario por ID")
public void testGetUserByIdSuccess() {
    User user = new User();
    user.setUserName("testuser");
    User savedUser = userRepo.save(user);

    UserDto retrievedUser = userService.getUserById(savedUser.getUserId());

    assertNotNull(retrievedUser);
    assertEquals("testuser", retrievedUser.getUserName());
}
```

### Prueba de Repository

```java
@Test
@DisplayName("Debe encontrar usuario por email")
public void testFindUserByEmail() {
    User user = new User();
    user.setUserEmail("test@example.com");
    userRepo.save(user);

    Optional<User> found = userRepo.findByUserEmail("test@example.com");

    assertTrue(found.isPresent());
}
```

## 🛠️ Dependencias Añadidas

Se han agregado al `pom.xml`:

```xml
<!-- Spring Boot Test -->
<spring-boot-starter-test>

<!-- TestContainers -->
<spring-boot-testcontainers>
<testcontainers-postgresql>
<testcontainers-junit-jupiter>

<!-- Spring Security Testing -->
<spring-security-test>

<!-- H2 Database (en memoria) -->
<h2>

<!-- REST Assured -->
<rest-assured>
```

## 📋 Configuración de Pruebas

### Perfil de Prueba

Se creó `application-test.properties` con:

- Base de datos H2 en memoria
- DDL automático con `create-drop`
- Puerto aleatorio para evitar conflictos
- Configuración simplificada

### Clase Base

`IntegrationTestBase.java` proporciona:

- `@SpringBootTest` - Carga el contexto completo
- `@AutoConfigureMockMvc` - Configura MockMvc
- `@ActiveProfiles("test")` - Usa perfil de prueba
- Inyección de `MockMvc` para tests HTTP

## 📚 Documentación

### TESTING_GUIDE.md

Guía completa que incluye:

- Estructura de pruebas
- Comandos de ejecución
- Explicación de cada capa
- Mejores prácticas
- Troubleshooting
- Cobertura de código

### TESTING_PATTERNS.md

Patrones y ejemplos avanzados:

- Patrón Arrange-Act-Assert
- Pruebas de Controllers
- Pruebas de Services
- Pruebas de Repositories
- Pruebas parametrizadas
- Flujos completos
- Validación de errores

## ✨ Características Principales

### 1. Cobertura Integral

- Controllers, Services, Repositories
- CRUD completo (Create, Read, Update, Delete)
- Casos de éxito y error
- Validaciones

### 2. Base de Datos de Prueba

- H2 en memoria para velocidad
- `create-drop` para aislamiento
- Limpieza automática entre tests

### 3. Flujos E2E

- Usuario completo: crear usuario → crear categoría → crear post
- Validaciones de datos
- CRUD completo en un flujo

### 4. Facilidad de Uso

- Scripts de ejecución (Windows y Linux/Mac)
- Comandos Maven simples
- Documentación completa con ejemplos

## 🎯 Próximos Pasos (Opcional)

1. **Agregar más tests de Services**

   - PostService, CommentService, FileService

2. **Agregar más tests de Repositories**

   - PostRepository, CommentRepository

3. **Pruebas de Seguridad**

   - Autenticación con JWT
   - Autorización por roles

4. **Pruebas de Rendimiento**

   - Load testing
   - Benchmarking

5. **Integración Continua**
   - Configurar GitHub Actions
   - Ejecutar tests automáticamente

## 🔍 Comandos Útiles

```bash
# Ejecutar todas las pruebas
mvn clean test

# Ejecutar pruebas específicas por nombre
mvn test -Dtest=CategoryControllerIntegrationTest

# Ejecutar en paralelo (más rápido)
mvn test -T 1C

# Ejecutar con cobertura
mvn clean test jacoco:report

# Ver cobertura
# Windows: start target/site/jacoco/index.html
# Linux/Mac: open target/site/jacoco/index.html

# Ejecutar un test específico
mvn test -Dtest=CategoryControllerIntegrationTest#testCreateCategorySuccess

# Generar reporte de Surefire
mvn surefire-report:report
```

## 📊 Estadísticas

- **Total de tests:** 50+
- **Líneas de código de test:** 2000+
- **Capas cubiertas:** 3 (Controllers, Services, Repositories)
- **Flujos E2E:** 3 (Completo, Validaciones, CRUD)
- **Documentación:** 2 guías completas

## ⚡ Ejecución Rápida

```bash
# Windows
cd c:\Users\Xiomara\Desktop\entregable_arqui\ViveMedellin-Backend
run-tests.bat all

# Linux/Mac
cd ~/ViveMedellin-Backend
./run-tests.sh all
```

## 📞 Soporte

Para más información, consulta:

- `TESTING_GUIDE.md` - Guía general
- `TESTING_PATTERNS.md` - Patrones avanzados
- Archivos de test como ejemplos

## ✅ Verificación

Para verificar que todo funciona:

```bash
# 1. Ejecutar pruebas
mvn clean test

# 2. Verificar que todas pasen
# Deberías ver: BUILD SUCCESS

# 3. Generar cobertura
mvn test jacoco:report

# 4. Abrir reporte (busca target/site/jacoco/index.html)
```

---

**Creado:** November 28, 2025
**Backend:** ViveMedellin
**Stack:** Spring Boot 3.3.5, JUnit 5, Mockito, H2 Database
