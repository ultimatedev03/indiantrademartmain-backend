package com.itech.itech_backend.modules.core.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
public class RootController {

    @Autowired
    private Environment env;

    @GetMapping
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("application", "iTech Backend API");
        response.put("version", "1.0.0");
        response.put("status", "Running");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        response.put("profiles", env.getActiveProfiles());
        response.put("port", env.getProperty("server.port"));
        
        // API endpoints information
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("health", "/health");
        endpoints.put("actuator_health", "/actuator/health");
        endpoints.put("api_auth", "/api/auth");
        endpoints.put("api_users", "/api/users");
        endpoints.put("api_products", "/api/products");
        endpoints.put("api_orders", "/api/orders");
        endpoints.put("api_admin", "/api/admin");
        endpoints.put("api_vendors", "/api/vendors");
        endpoints.put("api_cart", "/api/cart");
        endpoints.put("api_checkout", "/api/checkout");
        endpoints.put("api_leads", "/api/leads");
        endpoints.put("api_chatbot", "/api/chatbot");
        
        response.put("endpoints", endpoints);
        
        // Environment information
        Map<String, String> environment = new HashMap<>();
        environment.put("java_version", System.getProperty("java.version"));
        environment.put("spring_profiles", String.join(",", env.getActiveProfiles()));
        environment.put("database_url", env.getProperty("spring.datasource.url") != null ? 
            env.getProperty("spring.datasource.url").replaceAll("password=[^&]*", "password=***") : "Not configured");
        
        response.put("environment", environment);
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api")
    public ResponseEntity<Map<String, Object>> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("message", "iTech Backend API v1.0.0");
        response.put("status", "operational");
        response.put("documentation", "Contact admin for API documentation");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> status() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("status", "UP");
        response.put("application", "iTech Backend");
        response.put("version", "1.0.0");
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        
        return ResponseEntity.ok(response);
    }
}

