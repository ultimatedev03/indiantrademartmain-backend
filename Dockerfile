# =============================================================================
# Indian Trade Mart Backend - Render Deployment Dockerfile
# =============================================================================
# Stage 1: Build the application
# =============================================================================
FROM maven:3.9.4-eclipse-temurin-21 as builder

# Set working directory
WORKDIR /app

# Copy pom.xml and download dependencies (for better caching)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application for minimal memory usage
RUN mvn clean package -DskipTests -Dspring.profiles.active=minimal

# =============================================================================
# Stage 2: Runtime image
# =============================================================================
FROM eclipse-temurin:21-jre-jammy

# Install required packages
RUN apt-get update && apt-get install -y \
    curl \
    netcat-traditional \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN useradd -m -u 1001 -s /bin/sh appuser

# Set working directory
WORKDIR /app

# Create necessary directories
RUN mkdir -p /app/logs /app/uploads && \
    chown -R appuser:appuser /app

# Copy the built JAR from builder stage and startup script
COPY --from=builder /app/target/*.jar app.jar
COPY render-start.sh render-start.sh

# Make startup script executable and switch to non-root user
RUN chmod +x render-start.sh
USER appuser

# Expose port - Render uses PORT=10000 by default
EXPOSE 10000

# Set environment variables optimized for Render 512MB memory limit
ENV SPRING_PROFILES_ACTIVE=render
ENV SERVER_PORT=10000
ENV JAVA_OPTS="-Xmx280m -Xms100m -XX:+UseSerialGC -XX:MaxDirectMemorySize=32m -XX:MaxMetaspaceSize=128m -XX:CompressedClassSpaceSize=32m -XX:ReservedCodeCacheSize=32m -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -Djava.awt.headless=true -XX:+TieredCompilation -XX:TieredStopAtLevel=1 -Dspring.jmx.enabled=false -Dfile.encoding=UTF-8 -Djava.security.egd=file:/dev/./urandom -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport"

# Health check for port 10000
HEALTHCHECK --interval=45s --timeout=15s --start-period=120s --retries=3 \
    CMD curl -f http://localhost:10000/actuator/health || exit 1

# Use the optimized startup script for Render deployment
CMD ["./render-start.sh"]
