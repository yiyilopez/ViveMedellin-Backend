# 🚀 REFERENCIA RÁPIDA - Pruebas de Integración

## ⚡ 30 Segundos para Comenzar

```bash
# 1. Navega al proyecto
cd c:\Users\Xiomara\Desktop\entregable_arqui\ViveMedellin-Backend

# 2. Ejecuta TODAS las pruebas
run-tests.bat all

# ✅ ¡Listo! Deberías ver 68 tests verdes
```

---

## 📋 Comandos Más Usados

### Ejecución Rápida

```bash
# Todas las pruebas (más común)
mvn clean test

# Solo controllers
mvn test -Dtest=*ControllerIntegrationTest

# Solo services
mvn test -Dtest=*ServiceIntegrationTest

# Solo repositories
mvn test -Dtest=*RepositoryIntegrationTest

# Solo E2E
mvn test -Dtest=EndToEndFlowIntegrationTest
```

### Análisis

```bash
# Con cobertura
mvn clean test jacoco:report

# Más rápido (paralelo)
mvn test -T 1C

# Con detalles
mvn test -X

# Solo limpiar
mvn clean
```

---

## 📁 Archivos Clave

| Archivo                 | Propósito                    |
| ----------------------- | ---------------------------- |
| `TESTING_GUIDE.md`      | 📖 Guía completa             |
| `TESTING_PATTERNS.md`   | 🎓 Patrones avanzados        |
| `TEST_INDEX.md`         | 📑 Índice de todos los tests |
| `QUICK_VERIFICATION.md` | ✅ Verificación rápida       |
| `ARCHITECTURE.md`       | 🏗️ Diagramas                 |
| `run-tests.bat`         | 🔧 Script Windows            |
| `run-tests.sh`          | 🔧 Script Linux/Mac          |

---

## 🎯 Tests por Categoría

### Controllers (33 tests)

- AuthController (2)
- CategoryController (6)
- CommentController (10)
- PostController (7)
- UserController (8)

### Services (13 tests)

- CategoryService (6)
- UserService (7)

### Repositories (19 tests)

- CategoryRepository (8)
- UserRepository (11)

### E2E (3 tests)

- Flujo completo
- Validaciones
- CRUD

---

## ⚙️ Estructura de Test

```java
@Test
@DisplayName("Descripción clara del test")
public void testName() throws Exception {
    // ARRANGE - Preparar
    CategoryDto dto = new CategoryDto();
    dto.setTitle("Test");

    // ACT - Ejecutar
    mockMvc.perform(post("/api/categories")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))

    // ASSERT - Verificar
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.title", equalTo("Test")));
}
```

---

## 🔍 Búsqueda Rápida

### Por Status Code

```bash
# 200 OK
mvn test -Dtest=*ControllerIntegrationTest

# 201 Created
# Search in test files: testCreate

# 404 Not Found
# Search in test files: NotFound

# 400 Bad Request
# Search in test files: Fails
```

### Por Entidad

```bash
# Usuario
mvn test -Dtest=UserControllerIntegrationTest
mvn test -Dtest=UserServiceIntegrationTest
mvn test -Dtest=UserRepositoryIntegrationTest

# Categoría
mvn test -Dtest=CategoryControllerIntegrationTest
mvn test -Dtest=CategoryServiceIntegrationTest
mvn test -Dtest=CategoryRepositoryIntegrationTest

# Comentario
mvn test -Dtest=CommentControllerIntegrationTest
```

---

## 🐛 Troubleshooting Rápido

### Error: Port already in use

```
❌ Problema: Otro proceso usa el puerto
✅ Solución: Los tests usan puerto aleatorio (automático)
```

### Error: H2 not found

```
❌ Problema: Dependencia H2 no instalada
✅ Solución: `mvn clean install -U`
```

### Tests no se ejecutan

```
❌ Problema: Archivo de test mal nombrado
✅ Solución: Debe terminar en *IntegrationTest.java
```

### Build Failed

```
❌ Problema: Java/Maven desactualizado
✅ Solución: Verifica Java 17+, Maven 3.8+
```

---

## 📊 Estadísticas Esperadas

```
Tests run: 68
Failures: 0
Errors: 0
Skipped: 0

BUILD SUCCESS ✅
Time: ~1-2 minutes
```

---

## 🎓 Primeros Pasos

### 1. Lee la documentación (15 min)

```
Start with: TESTING_GUIDE.md
```

### 2. Ejecuta los tests (5 min)

```bash
mvn clean test
```

### 3. Explora un test (10 min)

```
Open: CategoryControllerIntegrationTest.java
```

### 4. Genera cobertura (5 min)

```bash
mvn test jacoco:report
open target/site/jacoco/index.html
```

---

## 💡 Tips Rápidos

- 🚀 Usa `run-tests.bat` para Windows
- 🎯 Ejecuta tests específicos con `-Dtest=`
- ⚡ Usa `-T 1C` para ejecución paralela
- 📖 Lee `TESTING_PATTERNS.md` para ejemplos
- 🔍 Busca en `TEST_INDEX.md` por nombre
- 📋 Verifica `QUICK_VERIFICATION.md` para troubleshooting

---

## 🎯 Próximas Acciones

- [ ] Ejecuta: `mvn clean test`
- [ ] Lee: `TESTING_GUIDE.md`
- [ ] Explora: Un archivo de test
- [ ] Personaliza: Según necesites

---

## 📞 Ayuda Rápida

**¿Dónde está...?**

- Tests: `vivemedellinbackend/src/test/java/com/vivemedellin/integration/`
- Config: `vivemedellinbackend/src/test/resources/application-test.properties`
- Documentación: Archivos `.md` en raíz
- Scripts: `run-tests.bat` y `run-tests.sh`

**¿Cómo...?**

- Ejecutar: `mvn clean test`
- Específicos: `mvn test -Dtest=NombreTest`
- Cobertura: `mvn test jacoco:report`
- Limpiar: `mvn clean`

**¿Problemas?**

- Ver: `QUICK_VERIFICATION.md` (Troubleshooting)
- Verificar: Java 17+, Maven 3.8+
- Limpiar: `mvn clean install -U`

---

## 🚀 Resumen

| ¿Qué?             | ¿Dónde?        | ¿Cómo?                   |
| ----------------- | -------------- | ------------------------ |
| Ejecutar tests    | Terminal       | `mvn test`               |
| Ver documentación | Archivos .md   | Abre en editor           |
| Ver ejemplos      | Archivos .java | Abre en VS Code          |
| Generar cobertura | Terminal       | `mvn test jacoco:report` |
| Scripts rápidos   | Raíz           | `run-tests.bat`          |

---

## 📈 Próximas Mejoras (Futuro)

- [ ] Agregar más tests de services
- [ ] Tests de seguridad/autenticación
- [ ] Load testing
- [ ] CI/CD integration
- [ ] Análisis de cobertura

---

**¡Todo listo para empezar!** 🎉

Última actualización: November 28, 2025
