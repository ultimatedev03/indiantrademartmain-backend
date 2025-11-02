# =============================================================================
# iTech Backend - Makefile
# =============================================================================

# Variables
PROJECT_NAME := itech-backend
JAR_NAME := itech-backend-0.0.1-SNAPSHOT.jar
DOCKER_IMAGE := itech-backend:latest
MAVEN_OPTS := -Dmaven.compiler.source=21 -Dmaven.compiler.target=21

# Default profile
PROFILE ?= development

# =============================================================================
# Help target (default)
# =============================================================================
.PHONY: help
help:
	@echo ""
	@echo "============================================================================="
	@echo " iTech Backend Build System (Makefile)"
	@echo "============================================================================="
	@echo ""
	@echo "Usage: make <target> [PROFILE=<profile>]"
	@echo ""
	@echo "Targets:"
	@echo "  clean        - Clean all build artifacts"
	@echo "  compile      - Compile source code only"
	@echo "  test         - Run tests only"
	@echo "  package      - Create JAR package"
	@echo "  docker-build - Build Docker image"
	@echo "  docker-run   - Run with Docker Compose"
	@echo "  run          - Run application locally"
	@echo "  full-build   - Clean + Compile + Test + Package"
	@echo "  help         - Show this help message"
	@echo ""
	@echo "Profiles:"
	@echo "  development  - Development profile (default)"
	@echo "  production   - Production profile"
	@echo "  test         - Test profile"
	@echo ""
	@echo "Examples:"
	@echo "  make package"
	@echo "  make run PROFILE=development"
	@echo "  make docker-run"
	@echo "  make full-build PROFILE=production"
	@echo ""

# =============================================================================
# Build targets
# =============================================================================
.PHONY: clean
clean:
	@echo "→ Cleaning project..."
	mvn clean $(MAVEN_OPTS)
	@echo "✓ Project cleaned successfully"

.PHONY: compile
compile:
	@echo "→ Compiling source code..."
	mvn compile $(MAVEN_OPTS)
	@echo "✓ Compilation completed successfully"

.PHONY: test
test:
	@echo "→ Running tests..."
	mvn test $(MAVEN_OPTS) -Dspring.profiles.active=test
	@echo "✓ All tests passed"

.PHONY: package
package:
	@echo "→ Creating JAR package..."
	mvn package $(MAVEN_OPTS) -DskipTests
	@echo "✓ JAR package created: target/$(JAR_NAME)"

.PHONY: package-with-tests
package-with-tests:
	@echo "→ Creating JAR package with tests..."
	mvn package $(MAVEN_OPTS)
	@echo "✓ JAR package created with tests: target/$(JAR_NAME)"

.PHONY: docker-build
docker-build:
	@echo "→ Building Docker image..."
	docker build -t $(DOCKER_IMAGE) .
	@echo "✓ Docker image built: $(DOCKER_IMAGE)"

.PHONY: docker-run
docker-run:
	@echo "→ Starting services with Docker Compose..."
	docker-compose up --build

.PHONY: docker-run-detached
docker-run-detached:
	@echo "→ Starting services in background..."
	docker-compose up --build -d
	@echo "✓ Services started in background"

.PHONY: docker-stop
docker-stop:
	@echo "→ Stopping Docker Compose services..."
	docker-compose down
	@echo "✓ Services stopped"

.PHONY: run
run:
	@echo "→ Starting Spring Boot application..."
	@echo "Profile: $(PROFILE)"
	mvn spring-boot:run $(MAVEN_OPTS) -Dspring-boot.run.profiles=$(PROFILE)

.PHONY: full-build
full-build: clean compile test package docker-build
	@echo "✓ Full build completed successfully"

.PHONY: quick-build
quick-build: clean compile package
	@echo "✓ Quick build completed (tests skipped)"

# =============================================================================
# Development utilities
# =============================================================================
.PHONY: dev-setup
dev-setup:
	@echo "→ Setting up development environment..."
	docker-compose up -d mysql redis
	@echo "✓ Development services started (MySQL, Redis)"

.PHONY: dev-logs
dev-logs:
	@echo "→ Showing application logs..."
	docker-compose logs -f app

.PHONY: check-health
check-health:
	@echo "→ Checking application health..."
	curl -f http://localhost:8080/actuator/health || echo "Application not responding"

.PHONY: format
format:
	@echo "→ Formatting code..."
	mvn spotless:apply 2>/dev/null || echo "Spotless plugin not configured"

.PHONY: analyze
analyze:
	@echo "→ Running static analysis..."
	mvn spotbugs:check 2>/dev/null || echo "SpotBugs plugin not configured"

# =============================================================================
# Cleanup targets
# =============================================================================
.PHONY: clean-all
clean-all: clean
	@echo "→ Cleaning Docker artifacts..."
	docker system prune -f 2>/dev/null || true
	docker image rm $(DOCKER_IMAGE) -f 2>/dev/null || true
	@echo "✓ All artifacts cleaned"

.PHONY: reset-data
reset-data:
	@echo "→ Resetting development data..."
	docker-compose down -v
	@echo "✓ Development data reset (volumes removed)"

# =============================================================================
# Info targets
# =============================================================================
.PHONY: info
info:
	@echo ""
	@echo "============================================================================="
	@echo " Project Information"
	@echo "============================================================================="
	@echo "Project: $(PROJECT_NAME)"
	@echo "JAR: $(JAR_NAME)"
	@echo "Docker Image: $(DOCKER_IMAGE)"
	@echo "Current Profile: $(PROFILE)"
	@echo ""
	@echo "Maven version:"
	@mvn --version | head -1
	@echo ""
	@echo "Java version:"
	@java -version 2>&1 | head -1
	@echo ""
	@if command -v docker >/dev/null 2>&1; then \
		echo "Docker version:"; \
		docker --version; \
	else \
		echo "Docker: Not installed"; \
	fi
	@echo ""
