package com.itech.itech_backend.modules.analytics.controller;

import com.itech.itech_backend.modules.analytics.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardAnalytics() {
        Map<String, Object> analytics = analyticsService.getDashboardAnalytics();
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/vendor/{vendorId}")
    @PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getVendorAnalytics(@PathVariable Long vendorId) {
        Map<String, Object> analytics = analyticsService.getVendorAnalytics(vendorId);
        return ResponseEntity.ok(analytics);
    }

    @GetMapping("/system-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSystemMetrics() {
        Map<String, Object> metrics = analyticsService.getSystemMetrics();
        return ResponseEntity.ok(metrics);
    }
}

