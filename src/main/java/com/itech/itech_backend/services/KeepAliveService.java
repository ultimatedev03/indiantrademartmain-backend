package com.itech.itech_backend.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@EnableScheduling
public class KeepAliveService {

    private static final Logger logger = LoggerFactory.getLogger(KeepAliveService.class);
    
    @Value("${server.port:8080}")
    private String serverPort;
    
    @Value("${app.keep-alive.enabled:true}")
    private boolean keepAliveEnabled;
    
    @Value("${app.keep-alive.interval:840000}") // 14 minutes in milliseconds
    private long keepAliveInterval;
    
    @Value("${app.render.url:https://indiantradebackend.onrender.com}")
    private String renderUrl;
    
    private final RestTemplate restTemplate;
    
    public KeepAliveService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    
    @PostConstruct
    public void init() {
        if (keepAliveEnabled) {
            logger.info("Keep-Alive Service initialized. Will ping every {} minutes", keepAliveInterval / 60000);
            logger.info("Target URL: {}", renderUrl);
        } else {
            logger.info("Keep-Alive Service is disabled");
        }
    }
    
    /**
     * Pings the server every 14 minutes to prevent it from sleeping
     * Render free tier puts services to sleep after 15 minutes of inactivity
     */
    @Scheduled(fixedRateString = "${app.keep-alive.interval:840000}")
    @Async
    public void keepAlive() {
        if (!keepAliveEnabled) {
            return;
        }
        
        try {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.info("Keep-Alive ping started at: {}", timestamp);
            
            // Ping the health endpoint
            String healthUrl = renderUrl + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(healthUrl, String.class);
            
            if (response.getStatusCode().is2xxSuccessful()) {
                logger.info("Keep-Alive ping successful - Status: {}", response.getStatusCode());
            } else {
                logger.warn("Keep-Alive ping returned non-success status: {}", response.getStatusCode());
            }
            
        } catch (Exception e) {
            logger.error("Keep-Alive ping failed: {}", e.getMessage());
            // Try backup ping to root endpoint
            try {
                ResponseEntity<String> backupResponse = restTemplate.getForEntity(renderUrl, String.class);
                logger.info("Backup ping successful - Status: {}", backupResponse.getStatusCode());
            } catch (Exception backupException) {
                logger.error("Backup ping also failed: {}", backupException.getMessage());
            }
        }
    }
    
    /**
     * Self-ping to internal health endpoint
     */
    @Scheduled(fixedRateString = "${app.keep-alive.self-ping-interval:300000}") // 5 minutes
    @Async
    public void selfPing() {
        if (!keepAliveEnabled) {
            return;
        }
        
        try {
            String selfUrl = "http://localhost:" + serverPort + "/health";
            ResponseEntity<String> response = restTemplate.getForEntity(selfUrl, String.class);
            logger.debug("Self-ping successful - Status: {}", response.getStatusCode());
        } catch (Exception e) {
            // Silent fail for self-ping as it's just a backup
            logger.debug("Self-ping failed (this is normal in some deployments): {}", e.getMessage());
        }
    }
    
    /**
     * Logs system status every hour for monitoring
     */
    @Scheduled(fixedRate = 3600000) // 1 hour
    public void logSystemStatus() {
        if (keepAliveEnabled) {
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            logger.info("System Status Check - Keep-Alive Service is running at: {}", timestamp);
            logger.info("Keep-Alive interval: {} minutes", keepAliveInterval / 60000);
            logger.info("Target URL: {}", renderUrl);
        }
    }
}