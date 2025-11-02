package com.itech.itech_backend.modules.admin.controller;

import com.itech.itech_backend.modules.admin.model.SiteConfiguration;
import com.itech.itech_backend.modules.admin.service.SiteConfigurationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cto/site-config")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class SiteConfigurationController {

    private final SiteConfigurationService siteConfigurationService;

    @GetMapping
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> getAllConfigurations() {
        try {
            List<SiteConfiguration> configurations = siteConfigurationService.getAllConfigurations();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "configurations", configurations,
                "count", configurations.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch configurations", "message", e.getMessage()));
        }
    }

    @GetMapping("/key/{configKey}")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> getConfigurationByKey(@PathVariable String configKey) {
        try {
            return siteConfigurationService.getConfigurationByKey(configKey)
                .map(config -> ResponseEntity.ok(Map.of("success", true, "configuration", config)))
                .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error fetching configuration by key {}: {}", configKey, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch configuration", "message", e.getMessage()));
        }
    }

    @GetMapping("/type/{configType}")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> getConfigurationsByType(@PathVariable String configType) {
        try {
            List<SiteConfiguration> configurations = siteConfigurationService.getConfigurationsByType(configType);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "configurations", configurations,
                "count", configurations.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching configurations by type {}: {}", configType, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch configurations", "message", e.getMessage()));
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> createOrUpdateConfiguration(@RequestBody Map<String, String> request) {
        try {
            String configKey = request.get("configKey");
            String configValue = request.get("configValue");
            String description = request.get("description");
            String configType = request.get("configType");

            if (configKey == null || configKey.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Config key is required"));
            }

            SiteConfiguration configuration = siteConfigurationService.createOrUpdateConfiguration(
                configKey, configValue, description, configType);

            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration saved successfully",
                "configuration", configuration
            ));
        } catch (Exception e) {
            log.error("Error creating/updating configuration: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to save configuration", "message", e.getMessage()));
        }
    }

    @DeleteMapping("/key/{configKey}")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteConfiguration(@PathVariable String configKey) {
        try {
            siteConfigurationService.deleteConfiguration(configKey);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Configuration deleted successfully"
            ));
        } catch (Exception e) {
            log.error("Error deleting configuration {}: {}", configKey, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to delete configuration", "message", e.getMessage()));
        }
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> searchConfigurations(@RequestParam String q) {
        try {
            List<SiteConfiguration> configurations = siteConfigurationService.searchConfigurations(q);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "configurations", configurations,
                "count", configurations.size()
            ));
        } catch (Exception e) {
            log.error("Error searching configurations with term {}: {}", q, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to search configurations", "message", e.getMessage()));
        }
    }

    @GetMapping("/types")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> getDistinctConfigTypes() {
        try {
            List<String> configTypes = siteConfigurationService.getDistinctConfigTypes();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "configTypes", configTypes,
                "count", configTypes.size()
            ));
        } catch (Exception e) {
            log.error("Error fetching config types: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch config types", "message", e.getMessage()));
        }
    }

    @GetMapping("/dashboard-stats")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> getDashboardStats() {
        try {
            Map<String, Object> stats = siteConfigurationService.getDashboardStats();
            return ResponseEntity.ok(Map.of("success", true, "stats", stats));
        } catch (Exception e) {
            log.error("Error fetching dashboard stats: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch dashboard stats", "message", e.getMessage()));
        }
    }

    @PostMapping("/initialize-defaults")
    @PreAuthorize("hasRole('CTO') or hasRole('ADMIN')")
    public ResponseEntity<?> initializeDefaultConfigurations() {
        try {
            siteConfigurationService.initializeDefaultConfigurations();
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Default configurations initialized successfully"
            ));
        } catch (Exception e) {
            log.error("Error initializing default configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to initialize default configurations", "message", e.getMessage()));
        }
    }

    // Public endpoint to get essential site configurations (for frontend)
    @GetMapping("/public")
    public ResponseEntity<?> getPublicConfigurations() {
        try {
            Map<String, String> publicConfigs = Map.of(
                "siteName", siteConfigurationService.getConfigValue("site.name", "Indian TradeMart"),
                "logoUrl", siteConfigurationService.getConfigValue("site.logo.url", "/assets/images/logo.png"),
                "faviconUrl", siteConfigurationService.getConfigValue("site.favicon.url", "/assets/images/favicon.ico"),
                "currency", siteConfigurationService.getConfigValue("business.currency", "INR"),
                "supportEmail", siteConfigurationService.getConfigValue("email.support.address", "support@indiantrademart.com")
            );

            return ResponseEntity.ok(Map.of("success", true, "configurations", publicConfigs));
        } catch (Exception e) {
            log.error("Error fetching public configurations: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Failed to fetch public configurations", "message", e.getMessage()));
        }
    }
}
