package com.itech.itech_backend.service;

import com.itech.itech_backend.config.SubdomainProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubdomainService {

    private final SubdomainProperties subdomainProperties;
    
    // Pattern for valid subdomain names
    private static final Pattern SUBDOMAIN_PATTERN = Pattern.compile("^[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?$");
    
    /**
     * Extract subdomain from HTTP request
     */
    public Optional<String> extractSubdomain(HttpServletRequest request) {
        String host = getHostFromRequest(request);
        
        if (!StringUtils.hasText(host)) {
            return Optional.empty();
        }
        
        // Handle localhost for development
        if (host.startsWith("localhost") || host.startsWith("127.0.0.1")) {
            return extractDevSubdomain(host);
        }
        
        return extractProdSubdomain(host);
    }
    
    /**
     * Extract subdomain from development environment
     */
    private Optional<String> extractDevSubdomain(String host) {
        // For development, we can use different approaches:
        // 1. vendor.localhost:3000
        // 2. localhost:3000/vendor (handled by frontend routing)
        // 3. vendor-localhost:3000
        
        if (host.contains(".localhost")) {
            String[] parts = host.split("\\.");
            if (parts.length >= 2 && !parts[0].equals("www")) {
                String subdomain = parts[0].toLowerCase();
                log.debug("🔍 Dev subdomain extracted: {}", subdomain);
                return Optional.of(subdomain);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Extract subdomain from production environment
     */
    private Optional<String> extractProdSubdomain(String host) {
        String baseDomain = subdomainProperties.getBaseDomain();
        
        if (host.endsWith("." + baseDomain)) {
            String potentialSubdomain = host.substring(0, host.length() - baseDomain.length() - 1);
            
            // Handle multi-level subdomains (take the first part)
            if (potentialSubdomain.contains(".")) {
                potentialSubdomain = potentialSubdomain.substring(0, potentialSubdomain.indexOf("."));
            }
            
            potentialSubdomain = potentialSubdomain.toLowerCase();
            
            // Skip www and empty subdomains
            if (!potentialSubdomain.equals("www") && !potentialSubdomain.isEmpty()) {
                log.debug("🔍 Prod subdomain extracted: {}", potentialSubdomain);
                return Optional.of(potentialSubdomain);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Get host from request, considering proxy headers
     */
    private String getHostFromRequest(HttpServletRequest request) {
        // Check for forwarded host (when behind proxy/load balancer)
        String forwardedHost = request.getHeader("X-Forwarded-Host");
        if (StringUtils.hasText(forwardedHost)) {
            // Take the first host if multiple
            return forwardedHost.split(",")[0].trim();
        }
        
        // Check for original host
        String originalHost = request.getHeader("X-Original-Host");
        if (StringUtils.hasText(originalHost)) {
            return originalHost.trim();
        }
        
        // Fallback to standard Host header
        return request.getHeader("Host");
    }
    
    /**
     * Validate if subdomain is allowed
     */
    public boolean isValidSubdomain(String subdomain) {
        if (!StringUtils.hasText(subdomain)) {
            return false;
        }
        
        subdomain = subdomain.toLowerCase();
        
        // Check if subdomain matches pattern
        if (!SUBDOMAIN_PATTERN.matcher(subdomain).matches()) {
            log.warn("⚠️ Invalid subdomain pattern: {}", subdomain);
            return false;
        }
        
        // Check if subdomain is reserved
        if (subdomainProperties.getReservedSubdomains().contains(subdomain)) {
            log.warn("⚠️ Reserved subdomain attempted: {}", subdomain);
            return false;
        }
        
        return true;
    }
    
    /**
     * Get subdomain configuration
     */
    public Optional<SubdomainProperties.SubdomainConfig> getSubdomainConfig(String subdomain) {
        if (!StringUtils.hasText(subdomain)) {
            return Optional.empty();
        }
        
        return Optional.ofNullable(subdomainProperties.getSubdomains().get(subdomain.toLowerCase()));
    }
    
    /**
     * Check if subdomain routing is enabled
     */
    public boolean isSubdomainRoutingEnabled() {
        return subdomainProperties.isEnabled();
    }
    
    /**
     * Generate CORS origins for subdomain
     */
    public String[] generateCorsOrigins(String subdomain) {
        if (!StringUtils.hasText(subdomain)) {
            return new String[]{"http://localhost:3000", "https://" + subdomainProperties.getBaseDomain()};
        }
        
        String baseDomain = subdomainProperties.getBaseDomain();
        String devDomain = subdomainProperties.getDevDomain();
        
        return new String[]{
            "http://" + subdomain + ".localhost:3000",
            "https://" + subdomain + ".localhost:3000", 
            "http://" + subdomain + "." + baseDomain,
            "https://" + subdomain + "." + baseDomain,
            "http://localhost:3000",
            "https://" + baseDomain
        };
    }
    
    /**
     * Get subdomain context information
     */
    public SubdomainContext getSubdomainContext(HttpServletRequest request) {
        Optional<String> subdomain = extractSubdomain(request);
        
        SubdomainContext context = new SubdomainContext();
        context.setSubdomain(subdomain.orElse(null));
        context.setHasSubdomain(subdomain.isPresent());
        context.setValid(subdomain.map(this::isValidSubdomain).orElse(false));
        context.setHost(getHostFromRequest(request));
        context.setConfig(subdomain.flatMap(this::getSubdomainConfig).orElse(null));
        
        log.debug("🌐 Subdomain context: {}", context);
        
        return context;
    }
    
    /**
     * Context information for current subdomain
     */
    public static class SubdomainContext {
        private String subdomain;
        private boolean hasSubdomain;
        private boolean valid;
        private String host;
        private SubdomainProperties.SubdomainConfig config;
        
        // Getters and setters
        public String getSubdomain() { return subdomain; }
        public void setSubdomain(String subdomain) { this.subdomain = subdomain; }
        
        public boolean isHasSubdomain() { return hasSubdomain; }
        public void setHasSubdomain(boolean hasSubdomain) { this.hasSubdomain = hasSubdomain; }
        
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public String getHost() { return host; }
        public void setHost(String host) { this.host = host; }
        
        public SubdomainProperties.SubdomainConfig getConfig() { return config; }
        public void setConfig(SubdomainProperties.SubdomainConfig config) { this.config = config; }
        
        @Override
        public String toString() {
            return "SubdomainContext{" +
                "subdomain='" + subdomain + '\'' +
                ", hasSubdomain=" + hasSubdomain +
                ", valid=" + valid +
                ", host='" + host + '\'' +
                '}';
        }
    }
}
