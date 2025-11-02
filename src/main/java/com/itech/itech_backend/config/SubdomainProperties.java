package com.itech.itech_backend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "app.subdomain")
@Data
public class SubdomainProperties {

    /**
     * Enable subdomain routing
     */
    private boolean enabled = true;

    /**
     * Base domain for the application
     */
    private String baseDomain = "example.com";

    /**
     * Development domain for local testing
     */
    private String devDomain = "localhost:3000";

    /**
     * Allowed subdomains and their configurations
     */
    private Map<String, SubdomainConfig> subdomains = new HashMap<>();

    /**
     * CORS configuration for subdomains
     */
    private CorsConfig cors = new CorsConfig();

    /**
     * Default subdomain to redirect to when no subdomain is specified
     */
    private String defaultSubdomain = "www";

    /**
     * Reserved subdomains that cannot be used by vendors
     */
    private List<String> reservedSubdomains = new ArrayList<>();

    @Data
    public static class SubdomainConfig {
        private String name;
        private String description;
        private boolean enabled = true;
        private String redirectTo;
        private Map<String, String> customHeaders = new HashMap<>();
        private List<String> allowedOrigins = new ArrayList<>();
    }

    @Data
    public static class CorsConfig {
        private List<String> allowedMethods = new ArrayList<>();
        private List<String> allowedHeaders = new ArrayList<>();
        private boolean allowCredentials = true;
        private long maxAge = 3600;
    }
}
