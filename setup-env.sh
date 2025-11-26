#!/bin/bash
# Script para configurar variables de entorno de ViveMedellin Backend
# Uso: source setup-env.sh

echo "🔧 Configurando variables de entorno para ViveMedellin Backend..."

# Generar JWT Secret seguro si no existe
if [ -z "$JWT_SECRET" ]; then
    echo "📝 Generando JWT Secret aleatorio..."
    export JWT_SECRET=$(openssl rand -base64 64 | tr -d '\n')
    echo "✅ JWT_SECRET generado (no se muestra por seguridad)"
else
    echo "✅ JWT_SECRET ya configurado"
fi

# Configurar otras variables con valores por defecto
export DB_USERNAME=${DB_USERNAME:-postgres}
export DB_PASSWORD=${DB_PASSWORD:-udea}
export JWT_EXPIRATION=${JWT_EXPIRATION:-1800000}
export JWT_REFRESH_EXPIRATION=${JWT_REFRESH_EXPIRATION:-604800000}

echo ""
echo "✅ Variables de entorno configuradas:"
echo "   DB_USERNAME: $DB_USERNAME"
echo "   DB_PASSWORD: *** (oculto)"
echo "   JWT_SECRET: *** (oculto)"
echo "   JWT_EXPIRATION: $JWT_EXPIRATION ms ($(($JWT_EXPIRATION / 60000)) minutos)"
echo "   JWT_REFRESH_EXPIRATION: $JWT_REFRESH_EXPIRATION ms ($(($JWT_REFRESH_EXPIRATION / 86400000)) días)"
echo ""
echo "💡 Estas variables solo están disponibles en esta sesión de terminal"
echo "💡 Para hacerlas permanentes, agrégalas a ~/.zshrc"
echo ""
echo "🚀 Ahora puedes ejecutar: mvn spring-boot:run"
