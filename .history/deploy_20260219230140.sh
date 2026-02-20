#!/bin/bash
echo "🚀 Iniciando despliegue de CasaVida..."

# 1. Actualizar repositorio (Esto ya se hace en el comando de una línea, pero por seguridad)
# git pull origin branch-v1

# 2. Construir el Backend
echo "📦 Construyendo el JAR del backend..."
cd backend
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo "✅ Backend construido con éxito."
else
    echo "❌ Error al construir el backend. Abortando."
    exit 1
fi

# 3. Reiniciar el servicio
echo "🔄 Reiniciando el servicio casavida.service..."
sudo systemctl restart casavida.service

if [ $? -eq 0 ]; then
    echo "🚀 Despliegue completado con éxito."
else
    echo "❌ Error al reiniciar el servicio. Verifica con: sudo journalctl -u casavida.service"
    exit 1
fi
