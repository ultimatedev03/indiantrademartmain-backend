package com.itech.itech_backend.modules.admin.controller;

import com.itech.itech_backend.modules.admin.service.CTODashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cto-dashboard")
@RequiredArgsConstructor
public class CTODashboardController {

    private final CTODashboardService ctoDashboardService;

    @GetMapping("/metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics() {
        try {
            Map<String, Object> metrics = ctoDashboardService.getDashboardMetrics();
            return ResponseEntity.ok(metrics);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/record-metrics")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> recordSystemMetrics() {
        try {
            ctoDashboardService.recordSystemMetrics();
            return ResponseEntity.ok("System metrics recorded successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to record system metrics");
        }
    }

    @PostMapping("/log-error")
    public ResponseEntity<String> logError(@RequestBody Map<String, Object> errorData) {
        try {
            ctoDashboardService.logError(
                (String) errorData.get("errorType"),
                (String) errorData.get("errorMessage"),
                (String) errorData.get("stackTrace"),
                (String) errorData.get("endpoint"),
                (String) errorData.get("httpMethod"),
                (Integer) errorData.get("httpStatus"),
                (String) errorData.get("userId"),
                (String) errorData.get("ipAddress"),
                (String) errorData.get("userAgent")
            );
            return ResponseEntity.ok("Error logged successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to log error");
        }
    }
}

