package com.itech.itech_backend.controller;

import com.itech.itech_backend.config.SubdomainProperties;
import com.itech.itech_backend.service.SubdomainService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/subdomain")
@RequiredArgsConstructor
@Slf4j
public class SubdomainController {

    private final SubdomainService subdomainService;
    private final SubdomainProperties subdomainProperties;

    /**
     * Get current subdomain information
     */
    @GetMapping("/info")
    public ResponseEntity<?> getSubdomainInfo(HttpServletRequest request) {
        try {
            SubdomainService.SubdomainContext context = subdomainService.getSubdomainContext(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subdomain", context.getSubdomain());
            response.put("hasSubdomain", context.isHasSubdomain());
            response.put("valid", context.isValid());
            response.put("host", context.getHost());
            response.put("config", context.getConfig());
            response.put("routingEnabled", subdomainService.isSubdomainRoutingEnabled());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting subdomain info: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get subdomain info",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Test subdomain routing
     */
    @GetMapping("/test")
    public ResponseEntity<?> testSubdomain(HttpServletRequest request) {
        try {
            SubdomainService.SubdomainContext context = subdomainService.getSubdomainContext(request);
            String[] corsOrigins = subdomainService.generateCorsOrigins(context.getSubdomain());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Subdomain routing test successful");
            response.put("context", context);
            response.put("corsOrigins", corsOrigins);
            response.put("timestamp", System.currentTimeMillis());
            
            log.info("🧪 Subdomain test for: {} from host: {}", 
                context.getSubdomain(), context.getHost());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error testing subdomain: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Subdomain test failed",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Validate subdomain name
     */
    @GetMapping("/validate/{subdomain}")
    public ResponseEntity<?> validateSubdomain(@PathVariable String subdomain) {
        try {
            boolean valid = subdomainService.isValidSubdomain(subdomain);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subdomain", subdomain);
            response.put("valid", valid);
            response.put("reserved", subdomainProperties.getReservedSubdomains().contains(subdomain.toLowerCase()));

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error validating subdomain {}: {}", subdomain, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to validate subdomain",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Get subdomain configuration
     */
    @GetMapping("/config")
    public ResponseEntity<?> getSubdomainConfig() {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("enabled", subdomainProperties.isEnabled());
            response.put("baseDomain", subdomainProperties.getBaseDomain());
            response.put("devDomain", subdomainProperties.getDevDomain());
            response.put("defaultSubdomain", subdomainProperties.getDefaultSubdomain());
            response.put("reservedSubdomains", subdomainProperties.getReservedSubdomains());
            response.put("configuredSubdomains", subdomainProperties.getSubdomains().keySet());

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error getting subdomain config: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to get subdomain config",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Generate CORS origins for a subdomain
     */
    @GetMapping("/cors-origins/{subdomain}")
    public ResponseEntity<?> getCorsOrigins(@PathVariable String subdomain) {
        try {
            String[] origins = subdomainService.generateCorsOrigins(subdomain);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subdomain", subdomain);
            response.put("origins", origins);

            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ Error generating CORS origins for {}: {}", subdomain, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(Map.of(
                "success", false,
                "error", "Failed to generate CORS origins",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck(HttpServletRequest request) {
        SubdomainService.SubdomainContext context = subdomainService.getSubdomainContext(request);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", "healthy");
        response.put("subdomain", context.getSubdomain());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}
