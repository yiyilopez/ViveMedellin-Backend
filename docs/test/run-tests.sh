#!/bin/bash
# Script para ejecutar pruebas de integración - Linux/Mac
# Uso: ./run-tests.sh [opcion]

set -e  # Exit on error

case "$1" in
    "")
        echo "Ejecutando todas las pruebas..."
        mvn clean test
        ;;
    "all")
        echo "Ejecutando todas las pruebas de integración..."
        mvn clean test -Dgroups="integration"
        ;;
    "controllers")
        echo "Ejecutando pruebas de Controllers..."
        mvn clean test -Dtest=*ControllerIntegrationTest
        ;;
    "services")
        echo "Ejecutando pruebas de Services..."
        mvn clean test -Dtest=*ServiceIntegrationTest
        ;;
    "repositories")
        echo "Ejecutando pruebas de Repositories..."
        mvn clean test -Dtest=*RepositoryIntegrationTest
        ;;
    "e2e")
        echo "Ejecutando pruebas End-to-End..."
        mvn clean test -Dtest=EndToEndFlowIntegrationTest
        ;;
    "coverage")
        echo "Ejecutando pruebas con cobertura..."
        mvn clean test jacoco:report
        echo ""
        echo "✓ Reporte generado en: target/site/jacoco/index.html"
        ;;
    "parallel")
        echo "Ejecutando pruebas en paralelo..."
        mvn clean test -T 1C
        ;;
    "help"|"-h"|"--help")
        echo "Uso: ./run-tests.sh [opcion]"
        echo ""
        echo "Opciones:"
        echo "  all           - Ejecutar todas las pruebas de integración"
        echo "  controllers   - Ejecutar solo tests de Controllers"
        echo "  services      - Ejecutar solo tests de Services"
        echo "  repositories  - Ejecutar solo tests de Repositories"
        echo "  e2e           - Ejecutar solo tests End-to-End"
        echo "  coverage      - Ejecutar tests con reporte de cobertura"
        echo "  parallel      - Ejecutar tests en paralelo (más rápido)"
        echo "  help          - Mostrar esta ayuda"
        echo ""
        echo "Ejemplo: ./run-tests.sh controllers"
        ;;
    *)
        echo "Opción desconocida: $1"
        echo "Ejecuta: ./run-tests.sh help"
        exit 1
        ;;
esac
