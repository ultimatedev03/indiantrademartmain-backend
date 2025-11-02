package com.itech.itech_backend.modules.analytics.controller;

import com.itech.itech_backend.modules.analytics.service.AdminAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/analytics")
@PreAuthorize("hasRole('ADMIN')")
public class AdminAnalyticsController {

    @Autowired
    private AdminAnalyticsService adminAnalyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = adminAnalyticsService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/growth")
    public ResponseEntity<Map<String, Object>> getGrowthMetrics() {
        Map<String, Object> metrics = adminAnalyticsService.getGrowthMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueMetrics() {
        Map<String, Object> revenue = adminAnalyticsService.getRevenueMetrics();
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUserAnalytics() {
        Map<String, Object> userStats = adminAnalyticsService.getUserAnalytics();
        return ResponseEntity.ok(userStats);
    }

    @GetMapping("/vendors")
    public ResponseEntity<Map<String, Object>> getVendorAnalytics() {
        Map<String, Object> vendorStats = adminAnalyticsService.getVendorAnalytics();
        return ResponseEntity.ok(vendorStats);
    }

    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> getProductAnalytics() {
        Map<String, Object> productStats = adminAnalyticsService.getProductAnalytics();
        return ResponseEntity.ok(productStats);
    }
}

