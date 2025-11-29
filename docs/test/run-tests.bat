@echo off
REM Script para ejecutar pruebas de integración - Windows
REM Uso: run-tests.bat [opcion]

setlocal enabledelayedexpansion

if "%1"=="" (
    echo Ejecutando todas las pruebas...
    call mvn clean test
    goto end
)

if "%1"=="all" (
    echo Ejecutando todas las pruebas de integración...
    call mvn clean test -Dgroups="integration"
    goto end
)

if "%1"=="controllers" (
    echo Ejecutando pruebas de Controllers...
    call mvn clean test -Dtest=*ControllerIntegrationTest
    goto end
)

if "%1"=="services" (
    echo Ejecutando pruebas de Services...
    call mvn clean test -Dtest=*ServiceIntegrationTest
    goto end
)

if "%1"=="repositories" (
    echo Ejecutando pruebas de Repositories...
    call mvn clean test -Dtest=*RepositoryIntegrationTest
    goto end
)

if "%1"=="e2e" (
    echo Ejecutando pruebas End-to-End...
    call mvn clean test -Dtest=EndToEndFlowIntegrationTest
    goto end
)

if "%1"=="coverage" (
    echo Ejecutando pruebas con cobertura...
    call mvn clean test jacoco:report
    goto end
)

if "%1"=="help" (
    echo Uso: run-tests.bat [opcion]
    echo.
    echo Opciones:
    echo   all           - Ejecutar todas las pruebas de integración
    echo   controllers   - Ejecutar solo tests de Controllers
    echo   services      - Ejecutar solo tests de Services
    echo   repositories  - Ejecutar solo tests de Repositories
    echo   e2e           - Ejecutar solo tests End-to-End
    echo   coverage      - Ejecutar tests con reporte de cobertura
    echo   help          - Mostrar esta ayuda
    echo.
    echo Ejemplo: run-tests.bat controllers
    goto end
)

echo Opción desconocida: %1
echo Ejecuta: run-tests.bat help
goto end

:end
endlocal
pause
