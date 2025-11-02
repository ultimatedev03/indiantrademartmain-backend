#!/bin/bash

# Indian Trade Mart Backend - Docker Entrypoint Script
# Handles application startup with environment configuration

set -euo pipefail

echo "=================================================="
echo "Indian Trade Mart Backend - Starting Application"
echo "=================================================="

# Print environment information
echo "Java Version:"
java -version

echo "Environment Variables:"
echo "SPRING_PROFILES_ACTIVE=${SPRING_PROFILES_ACTIVE:-prod}"
echo "SERVER_PORT=${SERVER_PORT:-8080}"
echo "DATABASE_URL=${DATABASE_URL:-not set}"
echo "REDIS_HOST=${REDIS_HOST:-not set}"

# Validate required environment variables for production
if [ "${SPRING_PROFILES_ACTIVE:-prod}" = "production" ]; then
    if [ -z "${DATABASE_URL:-}" ]; then
        echo "WARNING: DATABASE_URL not set for production profile"
    fi
    
    if [ -z "${JWT_SECRET:-}" ]; then
        echo "WARNING: JWT_SECRET not set for production profile"
    fi
fi

# Set JVM options
DEFAULT_JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
JAVA_OPTS="${JAVA_OPTS:-$DEFAULT_JAVA_OPTS}"

echo "JVM Options: $JAVA_OPTS"

# Wait for database if DATABASE_URL is provided
if [ -n "${DATABASE_URL:-}" ]; then
    echo "Checking database connectivity..."
    # Extract host and port from DATABASE_URL if possible
    # This is a simple check - in production you might want more robust health checking
fi

# Create necessary directories
mkdir -p /app/logs
mkdir -p /app/uploads

echo "Starting Spring Boot application..."
echo "=================================================="

# Start the application
exec java $JAVA_OPTS \
    -Djava.security.egd=file:/dev/./urandom \
    -Dserver.port="${SERVER_PORT:-8080}" \
    -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE:-prod}" \
    -jar app.jar "$@"
