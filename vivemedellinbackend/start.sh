#!/bin/bash

echo "🚀 Iniciando ViveMedellín Backend..."
echo ""

# Configurar Java 17
export JAVA_HOME=$(/usr/libexec/java_home -v 17)

# Verificar que existe el JAR
if [ ! -f "target/vivemedellinbackend-0.0.1-SNAPSHOT.jar" ]; then
    echo "📦 JAR no encontrado. Compilando proyecto..."
    mvn clean package -DskipTests
    echo ""
fi

# Iniciar el backend
echo "✅ Iniciando servidor en http://localhost:8080"
echo "📚 Swagger UI: http://localhost:8080/swagger-ui.html"
echo ""
echo "Presiona Ctrl+C para detener el servidor"
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
echo ""

java -jar target/vivemedellinbackend-0.0.1-SNAPSHOT.jar
