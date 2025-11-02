package com.itech.itech_backend.modules.imports.controller;

import com.itech.itech_backend.modules.imports.model.ImportResult;
import com.itech.itech_backend.modules.imports.service.ExcelImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imports")
@RequiredArgsConstructor
@Slf4j
public class ImportController {

    private final ExcelImportService excelImportService;

    /**
     * Import categories from Excel file
     */
    @PostMapping("/categories")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importCategories(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeeId") Long employeeId) {
        try {
            log.info("📂 Category import request from employee: {}, file: {}", employeeId, file.getOriginalFilename());

            // Validate file
            if (file.isEmpty()) {
                return createErrorResponse("File is empty", HttpStatus.BAD_REQUEST);
            }

            if (!isValidExcelFile(file)) {
                return createErrorResponse("Invalid file format. Please upload .xls or .xlsx files only.", HttpStatus.BAD_REQUEST);
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return createErrorResponse("File size too large. Maximum size is 10MB.", HttpStatus.BAD_REQUEST);
            }

            // Start import process
            ImportResult result = excelImportService.importCategories(file, employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category import started successfully");
            response.put("data", result);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            log.error("❌ Error starting category import: {}", e.getMessage(), e);
            return createErrorResponse("Failed to start import: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Import cities from Excel file
     */
    @PostMapping("/cities")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> importCities(
            @RequestParam("file") MultipartFile file,
            @RequestParam("employeeId") Long employeeId) {
        try {
            log.info("🏙️ City import request from employee: {}, file: {}", employeeId, file.getOriginalFilename());

            // Validate file
            if (file.isEmpty()) {
                return createErrorResponse("File is empty", HttpStatus.BAD_REQUEST);
            }

            if (!isValidExcelFile(file)) {
                return createErrorResponse("Invalid file format. Please upload .xls or .xlsx files only.", HttpStatus.BAD_REQUEST);
            }

            if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return createErrorResponse("File size too large. Maximum size is 10MB.", HttpStatus.BAD_REQUEST);
            }

            // Start import process
            ImportResult result = excelImportService.importCities(file, employeeId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "City import started successfully");
            response.put("data", result);

            return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);

        } catch (Exception e) {
            log.error("❌ Error starting city import: {}", e.getMessage(), e);
            return createErrorResponse("Failed to start import: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get import result by ID
     */
    @GetMapping("/results/{importId}")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getImportResult(@PathVariable String importId) {
        try {
            log.info("📊 Get import result request for ID: {}", importId);

            ImportResult result = excelImportService.getImportResult(importId);
            
            if (result == null) {
                return createErrorResponse("Import result not found", HttpStatus.NOT_FOUND);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import result retrieved successfully");
            response.put("data", result);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting import result: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get import result: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get import history for a user
     */
    @GetMapping("/history")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getImportHistory(@RequestParam Long userId) {
        try {
            log.info("📜 Get import history request for user: {}", userId);

            List<ImportResult> history = excelImportService.getImportHistory(userId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import history retrieved successfully");
            response.put("data", history);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting import history: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get import history: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download category import template
     */
    @GetMapping("/templates/categories")
    public ResponseEntity<Map<String, Object>> getCategoryTemplate() {
        try {
            log.info("📄 Category template download request");

            Map<String, Object> template = createCategoryTemplate();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Category template generated successfully");
            response.put("data", template);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error generating category template: {}", e.getMessage(), e);
            return createErrorResponse("Failed to generate template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Download city import template
     */
    @GetMapping("/templates/cities")
    public ResponseEntity<Map<String, Object>> getCityTemplate() {
        try {
            log.info("📄 City template download request");

            Map<String, Object> template = createCityTemplate();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "City template generated successfully");
            response.put("data", template);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error generating city template: {}", e.getMessage(), e);
            return createErrorResponse("Failed to generate template: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Get import statistics
     */
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('EMPLOYEE') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getImportStatistics(@RequestParam(required = false) Long userId) {
        try {
            log.info("📈 Import statistics request for user: {}", userId);

            List<ImportResult> imports = userId != null ? 
                excelImportService.getImportHistory(userId) : 
                excelImportService.getImportHistory(null);

            Map<String, Object> stats = calculateImportStatistics(imports);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Import statistics retrieved successfully");
            response.put("data", stats);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error getting import statistics: {}", e.getMessage(), e);
            return createErrorResponse("Failed to get statistics: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Private helper methods

    private boolean isValidExcelFile(MultipartFile file) {
        String contentType = file.getContentType();
        String fileName = file.getOriginalFilename();
        
        return (contentType != null && (
                contentType.equals("application/vnd.ms-excel") || 
                contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
        )) || (fileName != null && (
                fileName.endsWith(".xls") || 
                fileName.endsWith(".xlsx")
        ));
    }

    private ResponseEntity<Map<String, Object>> createErrorResponse(String message, HttpStatus status) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        return ResponseEntity.status(status).body(errorResponse);
    }

    private Map<String, Object> createCategoryTemplate() {
        Map<String, Object> template = new HashMap<>();
        
        // Headers
        template.put("headers", List.of(
            "Category Name", "Description", "Parent Category", "Display Order",
            "Commission %", "Vendor Visible", "Customer Visible", "Icon URL"
        ));
        
        // Sample data
        template.put("sampleData", List.of(
            Map.of(
                "Category Name", "Electronics",
                "Description", "Electronic items and gadgets",
                "Parent Category", "",
                "Display Order", "1",
                "Commission %", "5.0",
                "Vendor Visible", "true",
                "Customer Visible", "true",
                "Icon URL", "/icons/electronics.png"
            ),
            Map.of(
                "Category Name", "Smartphones",
                "Description", "Mobile phones and accessories",
                "Parent Category", "Electronics",
                "Display Order", "1",
                "Commission %", "7.5",
                "Vendor Visible", "true",
                "Customer Visible", "true",
                "Icon URL", "/icons/phone.png"
            ),
            Map.of(
                "Category Name", "Laptops",
                "Description", "Portable computers",
                "Parent Category", "Electronics",
                "Display Order", "2",
                "Commission %", "6.0",
                "Vendor Visible", "true",
                "Customer Visible", "true",
                "Icon URL", "/icons/laptop.png"
            )
        ));
        
        // Instructions
        template.put("instructions", List.of(
            "Fill in the Category Name (required) - must be unique",
            "Description is optional but recommended",
            "Parent Category should match an existing category name (leave blank for root category)",
            "Display Order determines sorting (0-999, default is 0)",
            "Commission % is the commission rate for this category (0-100)",
            "Vendor Visible: true/false - whether vendors can select this category",
            "Customer Visible: true/false - whether customers can see this category",
            "Icon URL is optional - URL to category icon image"
        ));
        
        return template;
    }

    private Map<String, Object> createCityTemplate() {
        Map<String, Object> template = new HashMap<>();
        
        // Headers
        template.put("headers", List.of(
            "City Name", "State/Province", "Country", "Postal Code", "Status",
            "Latitude", "Longitude", "Time Zone", "Notes", "Major City"
        ));
        
        // Sample data
        template.put("sampleData", List.of(
            Map.of(
                "City Name", "Mumbai",
                "State/Province", "Maharashtra",
                "Country", "India",
                "Postal Code", "400001",
                "Status", "Active",
                "Latitude", "19.0760",
                "Longitude", "72.8777",
                "Time Zone", "Asia/Kolkata",
                "Notes", "Financial capital of India",
                "Major City", "true"
            ),
            Map.of(
                "City Name", "Delhi",
                "State/Province", "Delhi",
                "Country", "India",
                "Postal Code", "110001",
                "Status", "Active",
                "Latitude", "28.6139",
                "Longitude", "77.2090",
                "Time Zone", "Asia/Kolkata",
                "Notes", "Capital city of India",
                "Major City", "true"
            ),
            Map.of(
                "City Name", "Bangalore",
                "State/Province", "Karnataka",
                "Country", "India",
                "Postal Code", "560001",
                "Status", "Active",
                "Latitude", "12.9716",
                "Longitude", "77.5946",
                "Time Zone", "Asia/Kolkata",
                "Notes", "IT hub of India",
                "Major City", "true"
            )
        ));
        
        // Instructions
        template.put("instructions", List.of(
            "Fill in the City Name (required) - must be unique within the country",
            "State/Province is optional but recommended",
            "Country is required",
            "Postal Code is optional",
            "Status: Active/Inactive (default is Active)",
            "Latitude: decimal degrees (-90 to 90, optional)",
            "Longitude: decimal degrees (-180 to 180, optional)",
            "Time Zone is optional (e.g., Asia/Kolkata, America/New_York)",
            "Notes field for additional information",
            "Major City: true/false - whether this is a major city"
        ));
        
        return template;
    }

    private Map<String, Object> calculateImportStatistics(List<ImportResult> imports) {
        Map<String, Object> stats = new HashMap<>();
        
        long totalImports = imports.size();
        long completedImports = imports.stream()
                .mapToLong(i -> i.getStatus() == ImportResult.ImportStatus.COMPLETED ? 1 : 0)
                .sum();
        long failedImports = imports.stream()
                .mapToLong(i -> i.getStatus() == ImportResult.ImportStatus.FAILED ? 1 : 0)
                .sum();
        
        long totalRowsProcessed = imports.stream()
                .mapToLong(ImportResult::getSuccessfulRows)
                .sum();
        
        long totalErrors = imports.stream()
                .mapToLong(ImportResult::getFailedRows)
                .sum();
        
        // Calculate success rate
        double successRate = totalImports > 0 ? (double) completedImports / totalImports * 100.0 : 0.0;
        
        stats.put("totalImports", totalImports);
        stats.put("completedImports", completedImports);
        stats.put("failedImports", failedImports);
        stats.put("inProgressImports", totalImports - completedImports - failedImports);
        stats.put("totalRowsProcessed", totalRowsProcessed);
        stats.put("totalErrors", totalErrors);
        stats.put("successRate", Math.round(successRate * 100.0) / 100.0);
        
        // Get recent imports (last 10)
        List<ImportResult> recentImports = imports.stream()
                .limit(10)
                .toList();
        stats.put("recentImports", recentImports);
        
        return stats;
    }
}
