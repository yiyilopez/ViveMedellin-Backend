# 📑 Índice de Pruebas de Integración

## Tabla de Contenidos

- [Controllers](#controllers)
  - [AuthControllerIntegrationTest](#authcontrollerintegratio ntest)
  - [CategoryControllerIntegrationTest](#categorycontrollerintegratio ntest)
  - [CommentControllerIntegrationTest](#commentcontrollerintegratio ntest)
  - [PostControllerIntegrationTest](#postcontrollerintegratio ntest)
  - [UserControllerIntegrationTest](#usercontrollerintegratio ntest)
- [Services](#services)
  - [CategoryServiceIntegrationTest](#categoryserviceintegratio ntest)
  - [UserServiceIntegrationTest](#userserviceintegratio ntest)
- [Repositories](#repositories)
  - [CategoryRepositoryIntegrationTest](#categoryrepositoryintegration test)
  - [UserRepositoryIntegrationTest](#userrepositoryintegration test)
- [End-to-End](#end-to-end)
  - [EndToEndFlowIntegrationTest](#endtoendflowintegrationtest)

---

## Controllers

### AuthControllerIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/controllers/AuthControllerIntegrationTest.java`

| #   | Nombre del Test                   | Propósito                                                   |
| --- | --------------------------------- | ----------------------------------------------------------- |
| 1   | `testShowAuthPageSuccess()`       | Verifica que la página de autenticación carga correctamente |
| 2   | `testUnauthorizedRouteNotFound()` | Verifica que las rutas no existentes retornan 404           |

### CategoryControllerIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/controllers/CategoryControllerIntegrationTest.java`

| #   | Nombre del Test                 | Propósito                                     |
| --- | ------------------------------- | --------------------------------------------- |
| 1   | `testCreateCategorySuccess()`   | Crea una categoría vía POST                   |
| 2   | `testGetAllCategoriesSuccess()` | Obtiene todas las categorías vía GET          |
| 3   | `testGetCategoryByIdSuccess()`  | Obtiene una categoría por ID vía GET          |
| 4   | `testGetCategoryByIdNotFound()` | Verifica error 404 cuando categoría no existe |
| 5   | `testUpdateCategorySuccess()`   | Actualiza una categoría vía PUT               |
| 6   | `testDeleteCategorySuccess()`   | Elimina una categoría vía DELETE              |

### CommentControllerIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/controllers/CommentControllerIntegrationTest.java`

| #   | Nombre del Test                           | Propósito                                      |
| --- | ----------------------------------------- | ---------------------------------------------- |
| 1   | `testCreateCommentSuccess()`              | Crea un comentario vía POST                    |
| 2   | `testGetAllCommentsSuccess()`             | Obtiene todos los comentarios vía GET          |
| 3   | `testGetCommentByIdSuccess()`             | Obtiene un comentario por ID vía GET           |
| 4   | `testGetCommentByIdNotFound()`            | Verifica error 404 cuando comentario no existe |
| 5   | `testUpdateCommentSuccess()`              | Actualiza un comentario vía PUT                |
| 6   | `testDeleteCommentSuccess()`              | Elimina un comentario vía DELETE               |
| 7   | `testGetCommentsByPostSuccess()`          | Obtiene comentarios por post vía GET           |
| 8   | `testGetCommentsByUserSuccess()`          | Obtiene comentarios por usuario vía GET        |
| 9   | `testCreateCommentWithoutContentFails()`  | Verifica validación de contenido vacío         |
| 10  | `testCreateCommentWithInvalidPostFails()` | Verifica error con post inexistente            |

### PostControllerIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/controllers/PostControllerIntegrationTest.java`

| #   | Nombre del Test                   | Propósito                                |
| --- | --------------------------------- | ---------------------------------------- |
| 1   | `testCreatePostSuccess()`         | Crea un post/evento vía POST             |
| 2   | `testGetAllPostsSuccess()`        | Obtiene todos los posts vía GET          |
| 3   | `testGetPostByIdSuccess()`        | Obtiene un post por ID vía GET           |
| 4   | `testGetPostByIdNotFound()`       | Verifica error 404 cuando post no existe |
| 5   | `testUpdatePostSuccess()`         | Actualiza un post vía PUT                |
| 6   | `testDeletePostSuccess()`         | Elimina un post vía DELETE               |
| 7   | `testGetPostsByCategorySuccess()` | Obtiene posts por categoría vía GET      |

### UserControllerIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/controllers/UserControllerIntegrationTest.java`

| #   | Nombre del Test                           | Propósito                                   |
| --- | ----------------------------------------- | ------------------------------------------- |
| 1   | `testCreateUserSuccess()`                 | Crea un usuario vía POST                    |
| 2   | `testGetAllUsersSuccess()`                | Obtiene todos los usuarios vía GET          |
| 3   | `testGetUserByIdSuccess()`                | Obtiene un usuario por ID vía GET           |
| 4   | `testGetUserByIdNotFound()`               | Verifica error 404 cuando usuario no existe |
| 5   | `testUpdateUserSuccess()`                 | Actualiza un usuario vía PUT                |
| 6   | `testDeleteUserSuccess()`                 | Elimina un usuario vía DELETE               |
| 7   | `testCreateUserWithDuplicateEmailFails()` | Verifica validación de email duplicado      |

**Total Controllers: 33 tests**

---

## Services

### CategoryServiceIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/services/CategoryServiceIntegrationTest.java`

| #   | Nombre del Test                               | Propósito                                 |
| --- | --------------------------------------------- | ----------------------------------------- |
| 1   | `testCreateCategorySuccess()`                 | Verifica creación de categoría en service |
| 2   | `testGetCategoryByIdSuccess()`                | Verifica obtención por ID en service      |
| 3   | `testGetAllCategoriesSuccess()`               | Verifica obtención de todas en service    |
| 4   | `testUpdateCategorySuccess()`                 | Verifica actualización en service         |
| 5   | `testDeleteCategorySuccess()`                 | Verifica eliminación en service           |
| 6   | `testGetNonExistentCategoryThrowsException()` | Verifica manejo de excepciones            |

### UserServiceIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/services/UserServiceIntegrationTest.java`

| #   | Nombre del Test                           | Propósito                               |
| --- | ----------------------------------------- | --------------------------------------- |
| 1   | `testCreateUserSuccess()`                 | Verifica creación de usuario en service |
| 2   | `testGetUserByIdSuccess()`                | Verifica obtención por ID en service    |
| 3   | `testGetAllUsersSuccess()`                | Verifica obtención de todos en service  |
| 4   | `testUpdateUserSuccess()`                 | Verifica actualización en service       |
| 5   | `testDeleteUserSuccess()`                 | Verifica eliminación en service         |
| 6   | `testGetUsersByRoleSuccess()`             | Verifica filtrado por rol en service    |
| 7   | `testGetNonExistentUserThrowsException()` | Verifica manejo de excepciones          |

**Total Services: 13 tests**

---

## Repositories

### CategoryRepositoryIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/repositories/CategoryRepositoryIntegrationTest.java`

| #   | Nombre del Test                             | Propósito                         |
| --- | ------------------------------------------- | --------------------------------- |
| 1   | `testSaveCategorySuccess()`                 | Verifica guardado en BD           |
| 2   | `testFindCategoryByIdSuccess()`             | Verifica búsqueda por ID en BD    |
| 3   | `testFindNonExistentCategoryReturnsEmpty()` | Verifica retorno vacío            |
| 4   | `testFindAllCategoriesSuccess()`            | Verifica obtención de todas en BD |
| 5   | `testUpdateCategorySuccess()`               | Verifica actualización en BD      |
| 6   | `testDeleteCategorySuccess()`               | Verifica eliminación en BD        |
| 7   | `testCountCategoriesSuccess()`              | Verifica conteo en BD             |
| 8   | `testExistsByCategoryIdSuccess()`           | Verifica existencia en BD         |

### UserRepositoryIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/repositories/UserRepositoryIntegrationTest.java`

| #   | Nombre del Test                         | Propósito                           |
| --- | --------------------------------------- | ----------------------------------- |
| 1   | `testSaveUserSuccess()`                 | Verifica guardado en BD             |
| 2   | `testFindUserByIdSuccess()`             | Verifica búsqueda por ID en BD      |
| 3   | `testFindUserByEmailSuccess()`          | Verifica búsqueda por email en BD   |
| 4   | `testFindUserByUsernameSuccess()`       | Verifica búsqueda por nombre en BD  |
| 5   | `testFindNonExistentUserReturnsEmpty()` | Verifica retorno vacío              |
| 6   | `testFindAllUsersSuccess()`             | Verifica obtención de todos en BD   |
| 7   | `testUpdateUserSuccess()`               | Verifica actualización en BD        |
| 8   | `testDeleteUserSuccess()`               | Verifica eliminación en BD          |
| 9   | `testCountUsersSuccess()`               | Verifica conteo en BD               |
| 10  | `testExistsByEmailSuccess()`            | Verifica existencia por email en BD |
| 11  | `testDuplicateEmailConstraint()`        | Verifica restricción de email único |

**Total Repositories: 19 tests**

---

## End-to-End

### EndToEndFlowIntegrationTest

**Ubicación:** `src/test/java/com/vivemedellin/integration/e2e/EndToEndFlowIntegrationTest.java`

| #   | Nombre del Test             | Propósito                                               |
| --- | --------------------------- | ------------------------------------------------------- |
| 1   | `testCompleteUserFlowE2E()` | Flujo completo: Usuario → Categoría → Post con 10 pasos |
| 2   | `testDataValidationE2E()`   | Valida restricciones de datos en flujo                  |
| 3   | `testCompleteCRUDFlowE2E()` | Prueba CRUD completo en un flujo                        |

**Total E2E: 3 tests**

---

## Resumen Estadístico

| Categoría             | Cantidad |
| --------------------- | -------- |
| Tests de Controllers  | 33       |
| Tests de Services     | 13       |
| Tests de Repositories | 19       |
| Tests E2E             | 3        |
| **Total**             | **68**   |

## Casos Cubiertos

### CRUD Operations

- ✅ **Create (POST)** - 12 tests
- ✅ **Read (GET)** - 18 tests
- ✅ **Update (PUT)** - 10 tests
- ✅ **Delete (DELETE)** - 8 tests

### HTTP Status Codes

- ✅ **201 Created** - Creación exitosa
- ✅ **200 OK** - Operaciones exitosas
- ✅ **400 Bad Request** - Validaciones fallidas
- ✅ **404 Not Found** - Recursos no encontrados

### Validaciones

- ✅ Email único
- ✅ Contenido no vacío
- ✅ Entidades relacionadas
- ✅ Restricciones de BD

### Casos de Error

- ✅ Recursos no encontrados
- ✅ Datos inválidos
- ✅ Duplicados
- ✅ Excepciones de negocio

---

## Cómo Ejecutar Tests Específicos

### Ejecutar un test individual

```bash
mvn test -Dtest=CategoryControllerIntegrationTest#testCreateCategorySuccess
```

### Ejecutar todos los tests de una clase

```bash
mvn test -Dtest=CategoryControllerIntegrationTest
```

### Ejecutar todos los tests de Controllers

```bash
mvn test -Dtest=*ControllerIntegrationTest
```

### Ejecutar todos los tests de Services

```bash
mvn test -Dtest=*ServiceIntegrationTest
```

### Ejecutar todos los tests de Repositories

```bash
mvn test -Dtest=*RepositoryIntegrationTest
```

### Ejecutar solo tests E2E

```bash
mvn test -Dtest=EndToEndFlowIntegrationTest
```

### Ejecutar todas las pruebas

```bash
mvn clean test
```

---

## Patrones de Test Utilizados

### Arrange-Act-Assert (AAA)

```java
// Arrange - Preparar datos
UserDto userDto = new UserDto();
userDto.setUserName("test");

// Act - Ejecutar
UserDto created = userService.createUser(userDto);

// Assert - Verificar
assertNotNull(created.getUserId());
```

### Given-When-Then (BDD)

```java
// Given
Category category = new Category();

// When
Category saved = categoryRepo.save(category);

// Then
assertTrue(categoryRepo.existsById(saved.getCategoryId()));
```

---

## Documentación Relacionada

- **TESTING_GUIDE.md** - Guía completa de ejecución
- **TESTING_PATTERNS.md** - Patrones y ejemplos avanzados
- **INTEGRATION_TESTING_README.md** - README general

---

## Última Actualización

- **Fecha:** November 28, 2025
- **Versión:** 1.0
- **Estado:** ✅ Completado
- **Total Tests:** 68

---

_Este índice está diseñado para ayudarte a encontrar rápidamente el test que necesitas._
