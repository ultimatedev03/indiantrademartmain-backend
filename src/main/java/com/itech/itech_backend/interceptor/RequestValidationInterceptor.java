package com.itech.itech_backend.interceptor;

import com.itech.itech_backend.exception.InvalidOperationException;
import com.itech.itech_backend.modules.performance.service.PerformanceMonitoringService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RequestValidationInterceptor implements HandlerInterceptor {

    private final PerformanceMonitoringService performanceMonitoringService;
    private static final String REQUEST_ID_HEADER = "X-Request-ID";
    private static final String START_TIME_ATTRIBUTE = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // Generate or get request ID
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null) {
            requestId = UUID.randomUUID().toString();
            response.setHeader(REQUEST_ID_HEADER, requestId);
        }
        
        // Store request start time
        request.setAttribute(START_TIME_ATTRIBUTE, System.currentTimeMillis());

        // Validate request size
        validateRequestSize(request);

        // Validate content type
        validateContentType(request);

        // Log incoming request
        logRequest(request, requestId);

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        Long startTime = (Long) request.getAttribute(START_TIME_ATTRIBUTE);
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            performanceMonitoringService.recordApiMetrics(request.getRequestURI(), startTime);
            
            // Log slow requests
            if (duration > 1000) { // More than 1 second
                log.warn("Slow request detected - URI: {}, Duration: {}ms", 
                    request.getRequestURI(), duration);
            }
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (ex != null) {
            log.error("Request processing failed - URI: {}, Error: {}", 
                request.getRequestURI(), ex.getMessage());
            performanceMonitoringService.recordError("REQUEST_PROCESSING", ex);
        }
    }

    private void validateRequestSize(HttpServletRequest request) {
        String contentLength = request.getHeader("Content-Length");
        if (contentLength != null) {
            long size = Long.parseLong(contentLength);
            if (size > 10_000_000) { // 10MB
                throw new InvalidOperationException("Request size exceeds maximum allowed limit");
            }
        }
    }

    private void validateContentType(HttpServletRequest request) {
        if ("POST".equals(request.getMethod()) || "PUT".equals(request.getMethod())) {
            String contentType = request.getContentType();
            if (contentType == null || 
                (!contentType.contains("application/json") && 
                 !contentType.contains("multipart/form-data") &&
                 !contentType.contains("application/x-www-form-urlencoded"))) {
                throw new InvalidOperationException("Unsupported content type: " + contentType);
            }
        }
    }

    private void logRequest(HttpServletRequest request, String requestId) {
        log.info("Incoming request - Method: {}, URI: {}, RequestID: {}", 
            request.getMethod(), 
            request.getRequestURI(),
            requestId);
    }
}
