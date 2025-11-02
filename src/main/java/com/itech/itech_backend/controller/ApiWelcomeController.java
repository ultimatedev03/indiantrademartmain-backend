package com.itech.itech_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * API Welcome Controller - Provides basic API information and health status
 */
@RestController
@RequestMapping("/api")
public class ApiWelcomeController {

    @GetMapping({"/", ""})
    public ResponseEntity<Map<String, Object>> welcome() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "🚀 Welcome to Indian Trade Mart Backend API");
        response.put("status", "active");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now());
        response.put("documentation", "API documentation available at /swagger-ui.html");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("auth", "/api/auth/* - Authentication endpoints");
        endpoints.put("products", "/api/products/* - Product management");
        endpoints.put("categories", "/api/categories/* - Category management");
        endpoints.put("health", "/actuator/health - Health check");
        endpoints.put("status", "/api/status - API status check");
        endpoints.put("info", "/api/info - System information");
        
        response.put("availableEndpoints", endpoints);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("service", "Indian Trade Mart Backend");
        response.put("status", "UP");
        response.put("environment", "production");
        response.put("timestamp", LocalDateTime.now());
        
        // Basic system info
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> system = new HashMap<>();
        system.put("maxMemoryMB", runtime.maxMemory() / 1024 / 1024);
        system.put("totalMemoryMB", runtime.totalMemory() / 1024 / 1024);
        system.put("freeMemoryMB", runtime.freeMemory() / 1024 / 1024);
        system.put("usedMemoryMB", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024);
        
        response.put("system", system);
        
        return ResponseEntity.ok(response);
    }
}
