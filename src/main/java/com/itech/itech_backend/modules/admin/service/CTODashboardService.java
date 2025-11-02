package com.itech.itech_backend.modules.admin.service;

import com.itech.itech_backend.modules.shared.model.ErrorLog;
import com.itech.itech_backend.modules.shared.model.SystemMetrics;
import com.itech.itech_backend.modules.shared.repository.ErrorLogRepository;
import com.itech.itech_backend.modules.shared.repository.SystemMetricsRepository;
import com.itech.itech_backend.modules.shared.repository.ApiLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CTODashboardService {

    private final SystemMetricsRepository systemMetricsRepository;
    private final ErrorLogRepository errorLogRepository;
    private final ApiLogRepository apiLogRepository;

    public Map<String, Object> getDashboardMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        
        // System Health
        metrics.put("systemHealth", getSystemHealth());
        
        // Error Statistics
        metrics.put("errorStats", getErrorStatistics());
        
        // API Performance
        metrics.put("apiStats", getApiStatistics());
        
        // Database Stats
        metrics.put("dbStats", getDatabaseStatistics());
        
        return metrics;
    }

    private Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new HashMap<>();
        
        // JVM Memory Usage
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        double memoryUsagePercent = (double) usedMemory / maxMemory * 100;
        
        health.put("memoryUsagePercent", Math.round(memoryUsagePercent * 100.0) / 100.0);
        health.put("usedMemoryMB", usedMemory / (1024 * 1024));
        health.put("maxMemoryMB", maxMemory / (1024 * 1024));
        
        // CPU Usage - try to get CPU load, fallback to 0 if not available
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        double cpuUsage = 0.0;
        try {
            if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                cpuUsage = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad() * 100;
            }
        } catch (Exception e) {
            log.warn("Unable to get CPU usage: {}", e.getMessage());
        }
        health.put("cpuUsagePercent", Math.round(cpuUsage * 100.0) / 100.0);
        
        // Uptime
        long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        health.put("uptimeMinutes", uptime / (1000 * 60));
        
        // Available Processors
        health.put("availableProcessors", osBean.getAvailableProcessors());
        
        return health;
    }

    private Map<String, Object> getErrorStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        
        stats.put("errorsLast24Hours", errorLogRepository.countSince(last24Hours));
        stats.put("errorsLastWeek", errorLogRepository.countSince(lastWeek));
        
        // Error type breakdown
        List<Object[]> errorTypes = errorLogRepository.getErrorTypeStatistics();
        stats.put("errorTypeBreakdown", errorTypes);
        
        // Most problematic endpoints
        List<Object[]> endpointErrors = errorLogRepository.getEndpointErrorStatistics();
        stats.put("problematicEndpoints", endpointErrors);
        
        // Recent errors
        List<ErrorLog> recentErrors = errorLogRepository.findByOrderByCreatedAtDesc(PageRequest.of(0, 10)).getContent();
        stats.put("recentErrors", recentErrors);
        
        return stats;
    }

    private Map<String, Object> getApiStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        LocalDateTime last24Hours = LocalDateTime.now().minusHours(24);
        
        // Total API calls in last 24 hours
        Long totalCalls = apiLogRepository.countByCreatedAtAfter(last24Hours);
        stats.put("totalCallsLast24Hours", totalCalls);
        
        // Success rate
        Long successfulCalls = apiLogRepository.countSuccessfulSince(last24Hours);
        if (totalCalls > 0) {
            double successRate = (double) successfulCalls / totalCalls * 100;
            stats.put("successRatePercent", Math.round(successRate * 100.0) / 100.0);
        } else {
            stats.put("successRatePercent", 100.0);
        }
        
        // Average response time
        Double avgResponseTime = apiLogRepository.getAverageResponseTimeSince(last24Hours);
        stats.put("averageResponseTimeMs", avgResponseTime != null ? Math.round(avgResponseTime * 100.0) / 100.0 : 0);
        
        return stats;
    }

    private Map<String, Object> getDatabaseStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            // Database connection pool info would go here
            // For now, just basic counts
            stats.put("systemMetricsCount", systemMetricsRepository.count());
            stats.put("errorLogCount", errorLogRepository.count());
            stats.put("apiLogCount", apiLogRepository.count());
            
        } catch (Exception e) {
            log.error("Error getting database statistics", e);
            stats.put("error", "Unable to fetch database statistics");
        }
        
        return stats;
    }

    public void recordSystemMetrics() {
        try {
            MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
            OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
            
            // Get CPU usage safely
            double cpuUsage = 0.0;
            try {
                if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
                    cpuUsage = ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad() * 100;
                }
            } catch (Exception e) {
                log.warn("Unable to get CPU usage for metrics: {}", e.getMessage());
            }
            
            SystemMetrics metrics = SystemMetrics.builder()
                    .metricType("SYSTEM_HEALTH")
                    .metricName("System Health Metrics")
                    .metricValue(String.valueOf(cpuUsage))
                    .description("CPU: " + cpuUsage + "%, Memory: " + 
                        ((double) memoryBean.getHeapMemoryUsage().getUsed() / memoryBean.getHeapMemoryUsage().getMax() * 100) + "%")
                    .build();
                    
            systemMetricsRepository.save(metrics);
            
        } catch (Exception e) {
            log.error("Error recording system metrics", e);
        }
    }

    public void logError(String errorType, String errorMessage, String stackTrace, 
                        String endpoint, String httpMethod, Integer httpStatus,
                        String userId, String ipAddress, String userAgent) {
        try {
            ErrorLog errorLog = ErrorLog.builder()
                    .errorType(errorType)
                    .errorMessage(errorMessage)
                    .stackTrace(stackTrace)
                    .endpoint(endpoint)
                    .httpMethod(httpMethod)
                    .httpStatus(httpStatus)
                    .userId(userId)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .build();
                    
            errorLogRepository.save(errorLog);
            
        } catch (Exception e) {
            log.error("Error logging error", e);
        }
    }
}

