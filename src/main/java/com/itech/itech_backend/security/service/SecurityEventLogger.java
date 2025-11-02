package com.itech.itech_backend.security.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class SecurityEventLogger {
    
    public void logSecurityEvent(String eventType, String description, String userId, String ipAddress) {
        log.warn("SECURITY EVENT [{}] - User: {}, IP: {}, Description: {}", 
                eventType, userId, ipAddress, description);
    }
    
    public void logAuthenticationFailure(String username, String ipAddress, String reason) {
        log.warn("AUTHENTICATION FAILURE - Username: {}, IP: {}, Reason: {}", 
                username, ipAddress, reason);
    }
    
    public void logAuthenticationSuccess(String username, String ipAddress) {
        log.info("AUTHENTICATION SUCCESS - Username: {}, IP: {}", username, ipAddress);
    }
    
    public void logPermissionDenied(String userId, String resource, String action) {
        log.warn("PERMISSION DENIED - User: {}, Resource: {}, Action: {}", 
                userId, resource, action);
    }
    
    public void logSuspiciousActivity(String eventType, String details, Map<String, Object> metadata) {
        log.warn("SUSPICIOUS ACTIVITY [{}] - Details: {}, Metadata: {}", 
                eventType, details, metadata);
    }
    
    public void logVulnerabilityDetected(String vulnerabilityType, String severity, String details) {
        log.error("VULNERABILITY DETECTED [{}] - Severity: {}, Details: {}", 
                vulnerabilityType, severity, details);
    }
    
    public void logSecurityConfigChange(String configType, String oldValue, String newValue, String changedBy) {
        log.warn("SECURITY CONFIG CHANGE - Type: {}, From: {} To: {}, By: {}", 
                configType, oldValue, newValue, changedBy);
    }
}
