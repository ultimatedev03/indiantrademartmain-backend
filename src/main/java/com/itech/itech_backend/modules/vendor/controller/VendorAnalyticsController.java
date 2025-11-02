package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.modules.vendor.service.VendorAnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/vendor/analytics")
@CrossOrigin(origins = {"http://localhost:3000", "https://your-frontend-domain.com"})
@PreAuthorize("hasRole('VENDOR')")
public class VendorAnalyticsController {

    @Autowired
    private VendorAnalyticsService vendorAnalyticsService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardStats(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> stats = vendorAnalyticsService.getDashboardStats(vendorEmail);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/products/performance")
    public ResponseEntity<Map<String, Object>> getProductPerformance(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> performance = vendorAnalyticsService.getProductPerformance(vendorEmail);
        return ResponseEntity.ok(performance);
    }

    @GetMapping("/revenue")
    public ResponseEntity<Map<String, Object>> getRevenueAnalytics(
            Authentication authentication,
            @RequestParam(defaultValue = "12") int months) {
        String vendorEmail = authentication.getName();
        Map<String, Object> revenue = vendorAnalyticsService.getRevenueAnalytics(vendorEmail, months);
        return ResponseEntity.ok(revenue);
    }

    @GetMapping("/orders")
    public ResponseEntity<Map<String, Object>> getOrderAnalytics(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> orders = vendorAnalyticsService.getOrderAnalytics(vendorEmail);
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/customers")
    public ResponseEntity<Map<String, Object>> getCustomerAnalytics(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> customers = vendorAnalyticsService.getCustomerAnalytics(vendorEmail);
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/growth")
    public ResponseEntity<Map<String, Object>> getGrowthMetrics(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> growth = vendorAnalyticsService.getGrowthMetrics(vendorEmail);
        return ResponseEntity.ok(growth);
    }

    @GetMapping("/profile")
    public ResponseEntity<Map<String, Object>> getProfileAnalytics(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> profile = vendorAnalyticsService.getProfileAnalytics(vendorEmail);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/market-insights")
    public ResponseEntity<Map<String, Object>> getMarketInsights(Authentication authentication) {
        String vendorEmail = authentication.getName();
        Map<String, Object> insights = vendorAnalyticsService.getMarketInsights(vendorEmail);
        return ResponseEntity.ok(insights);
    }

    @PostMapping("/export")
    public ResponseEntity<byte[]> exportAnalyticsData(
            Authentication authentication,
            @RequestBody Map<String, Object> exportRequest) {
        String vendorEmail = authentication.getName();
        byte[] exportData = vendorAnalyticsService.exportAnalyticsData(vendorEmail, exportRequest);
        
        String format = (String) exportRequest.get("format");
        String contentType = format.equals("pdf") ? "application/pdf" : "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
        String filename = format.equals("pdf") ? "vendor-analytics.pdf" : "vendor-analytics.xlsx";
        
        return ResponseEntity.ok()
                .header("Content-Type", contentType)
                .header("Content-Disposition", "attachment; filename=" + filename)
                .body(exportData);
    }

    @PutMapping("/preferences/dashboard")
    public ResponseEntity<Map<String, Object>> updateDashboardPreferences(
            Authentication authentication,
            @RequestBody Map<String, Object> preferences) {
        String vendorEmail = authentication.getName();
        vendorAnalyticsService.updateDashboardPreferences(vendorEmail, preferences);
        return ResponseEntity.ok(Map.of("message", "Dashboard preferences updated successfully"));
    }
}

