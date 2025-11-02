#!/bin/bash

# Indian Trade Mart Backend Deployment Script
# Professional deployment script for production environments
# Usage: ./deploy.sh [environment] [options]

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
APP_NAME="itech-backend"
JAR_NAME="itech-backend-0.0.1-SNAPSHOT.jar"
PID_FILE="${APP_NAME}.pid"
LOG_FILE="${APP_NAME}.log"

# Environment default values
DEFAULT_PORT=8080
DEFAULT_PROFILE="prod"
DEFAULT_HEAP="-Xms512m -Xmx2g"

print_usage() {
    echo "Usage: $0 [COMMAND] [OPTIONS]"
    echo ""
    echo "Commands:"
    echo "  build     Build the application JAR"
    echo "  start     Start the application"
    echo "  stop      Stop the application"
    echo "  restart   Restart the application"
    echo "  status    Check application status"
    echo "  logs      Show application logs"
    echo ""
    echo "Options:"
    echo "  --port=PORT       Server port (default: 8080)"
    echo "  --profile=PROFILE Spring profile (default: prod)"
    echo "  --heap=HEAP       JVM heap settings (default: -Xms512m -Xmx2g)"
    echo "  --background      Run in background (for start command)"
    echo ""
    echo "Examples:"
    echo "  $0 build"
    echo "  $0 start --port=8081 --profile=prod --background"
    echo "  $0 restart --heap='-Xms1g -Xmx4g'"
}

log() {
    echo -e "${BLUE}[$(date '+%Y-%m-%d %H:%M:%S')] $1${NC}"
}

success() {
    echo -e "${GREEN}[$(date '+%Y-%m-%d %H:%M:%S')] ✓ $1${NC}"
}

warn() {
    echo -e "${YELLOW}[$(date '+%Y-%m-%d %H:%M:%S')] ⚠ $1${NC}"
}

error() {
    echo -e "${RED}[$(date '+%Y-%m-%d %H:%M:%S')] ✗ $1${NC}" >&2
}

check_java() {
    if ! command -v java &> /dev/null; then
        error "Java 21+ is required but not found in PATH"
        exit 1
    fi
    
    java_version=$(java -version 2>&1 | head -1 | cut -d'"' -f2 | cut -d'.' -f1)
    if [ "${java_version}" -lt 21 ]; then
        error "Java 21+ is required, found Java ${java_version}"
        exit 1
    fi
    
    log "Using Java ${java_version}"
}

check_maven() {
    if ! command -v mvn &> /dev/null; then
        error "Maven is required but not found in PATH"
        exit 1
    fi
    log "Maven found: $(mvn -version | head -1)"
}

build_application() {
    log "Building ${APP_NAME}..."
    check_maven
    
    if mvn clean package -DskipTests; then
        if [ -f "target/${JAR_NAME}" ]; then
            jar_size=$(du -h "target/${JAR_NAME}" | cut -f1)
            success "Build completed successfully (${jar_size})"
        else
            error "Build completed but JAR file not found"
            exit 1
        fi
    else
        error "Build failed"
        exit 1
    fi
}

get_pid() {
    if [ -f "${PID_FILE}" ]; then
        cat "${PID_FILE}"
    else
        echo ""
    fi
}

is_running() {
    local pid=$(get_pid)
    if [ -n "${pid}" ] && kill -0 "${pid}" 2>/dev/null; then
        return 0
    else
        return 1
    fi
}

start_application() {
    local port=${1:-$DEFAULT_PORT}
    local profile=${2:-$DEFAULT_PROFILE}
    local heap=${3:-$DEFAULT_HEAP}
    local background=${4:-false}
    
    check_java
    
    if [ ! -f "target/${JAR_NAME}" ]; then
        warn "JAR file not found, building first..."
        build_application
    fi
    
    if is_running; then
        warn "Application is already running (PID: $(get_pid))"
        return 1
    fi
    
    log "Starting ${APP_NAME} on port ${port} with profile ${profile}..."
    
    # JVM Options for production
    JVM_OPTS="${heap}"
    JVM_OPTS="${JVM_OPTS} -server"
    JVM_OPTS="${JVM_OPTS} -XX:+UseG1GC"
    JVM_OPTS="${JVM_OPTS} -XX:+UseContainerSupport"
    JVM_OPTS="${JVM_OPTS} -XX:MaxRAMPercentage=75.0"
    JVM_OPTS="${JVM_OPTS} -Djava.security.egd=file:/dev/./urandom"
    JVM_OPTS="${JVM_OPTS} -Dspring.profiles.active=${profile}"
    JVM_OPTS="${JVM_OPTS} -Dserver.port=${port}"
    
    # Application arguments
    APP_ARGS="--spring.profiles.active=${profile}"
    APP_ARGS="${APP_ARGS} --server.port=${port}"
    
    if [ "${background}" = true ]; then
        nohup java ${JVM_OPTS} -jar "target/${JAR_NAME}" ${APP_ARGS} > "${LOG_FILE}" 2>&1 &
        echo $! > "${PID_FILE}"
        sleep 2
        
        if is_running; then
            success "Application started successfully (PID: $(get_pid))"
            log "Logs: tail -f ${LOG_FILE}"
        else
            error "Failed to start application"
            cat "${LOG_FILE}" | tail -20
            exit 1
        fi
    else
        java ${JVM_OPTS} -jar "target/${JAR_NAME}" ${APP_ARGS}
    fi
}

stop_application() {
    if ! is_running; then
        warn "Application is not running"
        return 1
    fi
    
    local pid=$(get_pid)
    log "Stopping ${APP_NAME} (PID: ${pid})..."
    
    # Graceful shutdown
    kill "${pid}"
    
    # Wait for graceful shutdown (max 30 seconds)
    local count=0
    while is_running && [ ${count} -lt 30 ]; do
        sleep 1
        count=$((count + 1))
    done
    
    if is_running; then
        warn "Graceful shutdown failed, forcing termination..."
        kill -9 "${pid}"
        sleep 2
    fi
    
    if is_running; then
        error "Failed to stop application"
        exit 1
    else
        success "Application stopped successfully"
        rm -f "${PID_FILE}"
    fi
}

show_status() {
    if is_running; then
        local pid=$(get_pid)
        success "Application is running (PID: ${pid})"
        
        # Show memory usage if ps command is available
        if command -v ps &> /dev/null; then
            local mem_usage=$(ps -o pid,ppid,rss,vsz,pcpu,pmem,cmd -p "${pid}" 2>/dev/null || echo "Memory info not available")
            log "Process details:"
            echo "${mem_usage}"
        fi
        
        # Show port binding if netstat/ss is available
        if command -v netstat &> /dev/null; then
            local port_info=$(netstat -tlnp 2>/dev/null | grep "${pid}" || echo "Port info not available")
            if [ -n "${port_info}" ]; then
                log "Port bindings:"
                echo "${port_info}"
            fi
        elif command -v ss &> /dev/null; then
            local port_info=$(ss -tlnp 2>/dev/null | grep "${pid}" || echo "Port info not available")
            if [ -n "${port_info}" ]; then
                log "Port bindings:"
                echo "${port_info}"
            fi
        fi
    else
        warn "Application is not running"
        return 1
    fi
}

show_logs() {
    if [ -f "${LOG_FILE}" ]; then
        log "Showing last 50 lines of ${LOG_FILE}:"
        tail -50 "${LOG_FILE}"
    else
        warn "Log file not found: ${LOG_FILE}"
    fi
}

# Parse command line arguments
COMMAND=""
PORT=$DEFAULT_PORT
PROFILE=$DEFAULT_PROFILE
HEAP=$DEFAULT_HEAP
BACKGROUND=false

while [[ $# -gt 0 ]]; do
    case $1 in
        build|start|stop|restart|status|logs)
            COMMAND="$1"
            shift
            ;;
        --port=*)
            PORT="${1#*=}"
            shift
            ;;
        --profile=*)
            PROFILE="${1#*=}"
            shift
            ;;
        --heap=*)
            HEAP="${1#*=}"
            shift
            ;;
        --background)
            BACKGROUND=true
            shift
            ;;
        -h|--help)
            print_usage
            exit 0
            ;;
        *)
            error "Unknown option: $1"
            print_usage
            exit 1
            ;;
    esac
done

# Execute command
case "${COMMAND}" in
    build)
        build_application
        ;;
    start)
        start_application "${PORT}" "${PROFILE}" "${HEAP}" "${BACKGROUND}"
        ;;
    stop)
        stop_application
        ;;
    restart)
        if is_running; then
            stop_application
        fi
        sleep 2
        start_application "${PORT}" "${PROFILE}" "${HEAP}" true
        ;;
    status)
        show_status
        ;;
    logs)
        show_logs
        ;;
    "")
        error "No command specified"
        print_usage
        exit 1
        ;;
    *)
        error "Unknown command: ${COMMAND}"
        print_usage
        exit 1
        ;;
esac
