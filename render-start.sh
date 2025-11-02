#!/bin/bash

# Render startup script for Indian Trade Mart Backend
# This script ensures proper port binding and configuration for Render deployment

set -euo pipefail

echo "🚀 Starting Indian Trade Mart Backend on Render..."
echo "📊 Memory limit: 512MB, JVM max heap: 300MB"
echo "🌐 Port: ${PORT:-10000}"
echo "🏷️ Profile: ${SPRING_PROFILES_ACTIVE:-render}"

# Set JVM memory options optimized for Render 512MB limit - METASPACE FIXED
export JAVA_OPTS="${JAVA_OPTS:--Xmx280m -Xms100m -XX:+UseSerialGC -XX:MaxDirectMemorySize=32m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:ReservedCodeCacheSize=32m -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -Djava.awt.headless=true -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dspring.jmx.enabled=false -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport}"

# Ensure the PORT environment variable is properly set for Render
export SERVER_PORT="${PORT:-10000}"

echo "🔧 Java Options: $JAVA_OPTS"
echo "⚙️  Server Port: $SERVER_PORT"

# Start the application with explicit port binding
exec java $JAVA_OPTS \
    -Dserver.port="$SERVER_PORT" \
    -Dserver.address=0.0.0.0 \
    -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE:-render}" \
    -jar app.jar
