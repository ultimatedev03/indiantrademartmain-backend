package com.itech.itech_backend.modules.shared.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import javax.sql.DataSource;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced Health Controller for iTech Backend
 * ===========================================
 * 
 * Provides comprehensive health monitoring for:
 * - Database connectivity and performance
 * - Application status and metrics
 * - System resources and JVM info
 * - Integration endpoints for frontend
 */
@RestController
public class HealthController {

    @Autowired
    private DataSource dataSource;
    
    @Autowired
    private Environment env;
    
    @Value("${app.keep-alive.enabled:true}")
    private boolean keepAliveEnabled;
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(HealthController.class);

    /**
     * Root health endpoint for simple frontend checks and Keep-Alive pings
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> rootHealth(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();
        String timestamp = LocalDateTime.now().format(formatter);
        String userAgent = request.getHeader("User-Agent");
        String clientIP = getClientIP(request);
        
        // Check if this is a keep-alive ping (Java User-Agent)
        boolean isKeepAlivePing = userAgent != null && userAgent.toLowerCase().contains("java");
        
        if (isKeepAlivePing) {
            logger.info("Keep-Alive ping received from: {} at {}", clientIP, timestamp);
        } else {
            logger.debug("Health check from: {} (User-Agent: {}) at {}", clientIP, userAgent, timestamp);
        }
        
        response.put("status", "UP");
        response.put("application", "itech-backend");
        response.put("timestamp", timestamp);
        response.put("keepAliveEnabled", keepAliveEnabled);
        response.put("message", "Service is healthy and running");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Main health endpoint - Frontend compatible with Keep-Alive information
     */
    @GetMapping("/api/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> healthInfo = new HashMap<>();
        
        try {
            // Basic system info
            healthInfo.put("status", "UP");
            healthInfo.put("timestamp", LocalDateTime.now().format(formatter));
            healthInfo.put("application", "itech-backend");
            healthInfo.put("keepAliveService", keepAliveEnabled ? "ACTIVE" : "INACTIVE");
            healthInfo.put("version", "1.0.0");
            healthInfo.put("environment", env.getProperty("spring.profiles.active", "development"));
            healthInfo.put("port", env.getProperty("server.port", "8080"));
            
            // System resources
            Runtime runtime = Runtime.getRuntime();
            Map<String, Object> memory = new HashMap<>();
            memory.put("total", runtime.totalMemory());
            memory.put("free", runtime.freeMemory());
            memory.put("used", runtime.totalMemory() - runtime.freeMemory());
            memory.put("max", runtime.maxMemory());
            healthInfo.put("memory", memory);
            
            // JVM info
            Map<String, Object> jvm = new HashMap<>();
            jvm.put("version", System.getProperty("java.version"));
            jvm.put("vendor", System.getProperty("java.vendor"));
            jvm.put("uptime", java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime());
            healthInfo.put("jvm", jvm);
            
            // Database health
            Map<String, Object> database = checkDatabaseHealth();
            healthInfo.put("database", database);
            
            // Check overall health status
            boolean isHealthy = "healthy".equals(database.get("status"));
            healthInfo.put("status", isHealthy ? "healthy" : "degraded");
            
            return ResponseEntity.ok(healthInfo);
            
        } catch (Exception e) {
            healthInfo.put("status", "unhealthy");
            healthInfo.put("error", e.getMessage());
            healthInfo.put("timestamp", LocalDateTime.now().toString());
            
            return ResponseEntity.status(503).body(healthInfo);
        }
    }

    /**
     * API status endpoint for backend status
     */
    @GetMapping("/api/status")
    public ResponseEntity<Map<String, Object>> apiStatus() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Test database connection
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(5);
            connection.close();
            
            response.put("status", "UP");
            response.put("database", isValid ? "UP" : "DOWN");
            response.put("profiles", env.getActiveProfiles());
            response.put("port", env.getProperty("server.port"));
            response.put("app", "itech-backend");
            
        } catch (Exception e) {
            response.put("status", "DOWN");
            response.put("database", "DOWN");
            response.put("error", e.getMessage());
            response.put("profiles", env.getActiveProfiles());
            response.put("port", env.getProperty("server.port"));
            response.put("app", "itech-backend");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Keep-Alive service status endpoint
     */
    @GetMapping("/keep-alive/status")
    public ResponseEntity<Map<String, Object>> keepAliveStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("keepAliveEnabled", keepAliveEnabled);
        response.put("status", keepAliveEnabled ? "ACTIVE" : "INACTIVE");
        response.put("message", keepAliveEnabled ? 
            "Keep-Alive service is preventing the application from sleeping" : 
            "Keep-Alive service is disabled");
        response.put("timestamp", LocalDateTime.now().format(formatter));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/db")
    public ResponseEntity<Map<String, Object>> databaseHealth() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Connection connection = dataSource.getConnection();
            boolean isValid = connection.isValid(5);
            
            response.put("database", isValid ? "UP" : "DOWN");
            response.put("url", env.getProperty("spring.datasource.url"));
            response.put("username", env.getProperty("spring.datasource.username"));
            response.put("driver", env.getProperty("spring.datasource.driver-class-name"));
            
            connection.close();
            
        } catch (Exception e) {
            response.put("database", "DOWN");
            response.put("error", e.getMessage());
            response.put("url", env.getProperty("spring.datasource.url"));
            response.put("username", env.getProperty("spring.datasource.username"));
            response.put("driver", env.getProperty("spring.datasource.driver-class-name"));
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Additional health endpoints for monitoring
     */
    @GetMapping("/health/ready")
    public ResponseEntity<Map<String, String>> ready() {
        try {
            // Quick database connectivity check
            try (Connection connection = dataSource.getConnection()) {
                if (connection.isValid(5)) {
                    return ResponseEntity.ok(Map.of(
                        "status", "ready",
                        "timestamp", LocalDateTime.now().toString()
                    ));
                }
            }
            
            return ResponseEntity.status(503).body(Map.of(
                "status", "not ready",
                "reason", "Database connection failed",
                "timestamp", LocalDateTime.now().toString()
            ));
            
        } catch (Exception e) {
            return ResponseEntity.status(503).body(Map.of(
                "status", "not ready",
                "reason", e.getMessage(),
                "timestamp", LocalDateTime.now().toString()
            ));
        }
    }

    @GetMapping("/health/alive")
    public ResponseEntity<Map<String, String>> alive() {
        return ResponseEntity.ok(Map.of(
            "status", "alive",
            "timestamp", LocalDateTime.now().toString(),
            "uptime", String.valueOf(java.lang.management.ManagementFactory.getRuntimeMXBean().getUptime())
        ));
    }

    /**
     * Check database connectivity and performance
     */
    private Map<String, Object> checkDatabaseHealth() {
        Map<String, Object> dbHealth = new HashMap<>();
        
        try {
            long startTime = System.currentTimeMillis();
            
            try (Connection connection = dataSource.getConnection()) {
                boolean isValid = connection.isValid(5); // 5 second timeout
                long responseTime = System.currentTimeMillis() - startTime;
                
                if (isValid) {
                    dbHealth.put("status", "healthy");
                    dbHealth.put("responseTime", responseTime + "ms");
                    dbHealth.put("driver", connection.getMetaData().getDriverName());
                    dbHealth.put("url", connection.getMetaData().getURL());
                    dbHealth.put("autoCommit", connection.getAutoCommit());
                } else {
                    dbHealth.put("status", "unhealthy");
                    dbHealth.put("error", "Connection validation failed");
                    dbHealth.put("responseTime", responseTime + "ms");
                }
            }
            
        } catch (Exception e) {
            dbHealth.put("status", "unhealthy");
            dbHealth.put("error", e.getMessage());
            dbHealth.put("errorType", e.getClass().getSimpleName());
        }
        
        return dbHealth;
    }
    
    /**
     * Get client IP address from request
     */
    private String getClientIP(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getHeader("X-Real-IP");
        }
        if (clientIP == null || clientIP.isEmpty() || "unknown".equalsIgnoreCase(clientIP)) {
            clientIP = request.getRemoteAddr();
        }
        // Handle multiple IPs in X-Forwarded-For header
        if (clientIP != null && clientIP.contains(",")) {
            clientIP = clientIP.split(",")[0].trim();
        }
        return clientIP != null ? clientIP : "unknown";
    }
}
