package com.itech.itech_backend.middleware;

import com.itech.itech_backend.service.SubdomainService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 10)
@RequiredArgsConstructor
@Slf4j
public class SubdomainMiddleware implements Filter {

    private final SubdomainService subdomainService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("🌐 Subdomain Middleware initialized");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) 
            throws IOException, ServletException {
        
        if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            processSubdomain(httpRequest, httpResponse, chain);
        } catch (Exception e) {
            log.error("❌ Error in subdomain middleware: {}", e.getMessage(), e);
            // Continue with request processing even if subdomain processing fails
            chain.doFilter(request, response);
        }
    }

    private void processSubdomain(HttpServletRequest request, HttpServletResponse response, FilterChain chain) 
            throws IOException, ServletException {

        // Skip processing for certain paths
        if (shouldSkipProcessing(request)) {
            chain.doFilter(request, response);
            return;
        }

        // Get subdomain context
        SubdomainService.SubdomainContext context = subdomainService.getSubdomainContext(request);
        
        // Set subdomain context as request attribute
        request.setAttribute("subdomainContext", context);
        
        // Add subdomain headers for easier access
        if (context.isHasSubdomain()) {
            response.setHeader("X-Subdomain", context.getSubdomain());
            response.setHeader("X-Subdomain-Valid", String.valueOf(context.isValid()));
            
            log.debug("🔍 Processing request for subdomain: {} (valid: {})", 
                context.getSubdomain(), context.isValid());
        }

        // Handle invalid subdomains
        if (context.isHasSubdomain() && !context.isValid()) {
            handleInvalidSubdomain(request, response, context);
            return;
        }

        // Add CORS headers for subdomain
        addSubdomainCorsHeaders(response, context);

        // Continue with the request
        chain.doFilter(request, response);
    }

    /**
     * Check if we should skip subdomain processing for certain paths
     */
    private boolean shouldSkipProcessing(HttpServletRequest request) {
        String uri = request.getRequestURI();
        
        // Skip for static resources
        if (uri.startsWith("/static/") || 
            uri.startsWith("/assets/") || 
            uri.startsWith("/favicon.ico") ||
            uri.startsWith("/uploads/") ||
            uri.startsWith("/api/files/")) {
            return true;
        }

        // Skip for health checks and monitoring
        if (uri.startsWith("/actuator/") || 
            uri.startsWith("/health") ||
            uri.startsWith("/metrics")) {
            return true;
        }

        // Skip for WebSocket connections
        if ("websocket".equalsIgnoreCase(request.getHeader("Upgrade"))) {
            return true;
        }

        return false;
    }

    /**
     * Handle invalid subdomain requests
     */
    private void handleInvalidSubdomain(HttpServletRequest request, HttpServletResponse response, 
            SubdomainService.SubdomainContext context) throws IOException {
        
        log.warn("⚠️ Invalid subdomain request: {} from host: {}", 
            context.getSubdomain(), context.getHost());

        // For API requests, return JSON error
        if (request.getRequestURI().startsWith("/api/")) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            String errorJson = String.format(
                "{\"error\": \"Invalid subdomain\", \"message\": \"Subdomain '%s' is not valid or not found\", \"subdomain\": \"%s\"}", 
                context.getSubdomain(), context.getSubdomain()
            );
            
            response.getWriter().write(errorJson);
            response.getWriter().flush();
            return;
        }

        // For web requests, redirect to main domain
        String mainDomain = request.getScheme() + "://" + 
            subdomainService.getSubdomainContext(request).getHost().replaceAll("^[^.]+\\.", "");
        
        log.info("🔄 Redirecting invalid subdomain to: {}", mainDomain);
        response.sendRedirect(mainDomain);
    }

    /**
     * Add CORS headers specific to subdomain
     */
    private void addSubdomainCorsHeaders(HttpServletResponse response, SubdomainService.SubdomainContext context) {
        if (!context.isHasSubdomain() || !context.isValid()) {
            return;
        }

        // Generate CORS origins for the subdomain
        String[] origins = subdomainService.generateCorsOrigins(context.getSubdomain());
        
        // Add the origins to the response
        String allowedOrigins = String.join(", ", origins);
        response.setHeader("Access-Control-Allow-Origin-Subdomain", allowedOrigins);
        
        // Add subdomain-specific headers if configured
        if (context.getConfig() != null && context.getConfig().getCustomHeaders() != null) {
            context.getConfig().getCustomHeaders().forEach((key, value) -> {
                response.setHeader(key, value);
                log.debug("🔧 Added custom header: {} = {}", key, value);
            });
        }
    }

    @Override
    public void destroy() {
        log.info("🌐 Subdomain Middleware destroyed");
    }
}
