package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.modules.shared.dto.ExcelImportResponseDto;
import com.itech.itech_backend.modules.vendor.service.VendorProductImportService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/excel")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:3001"})
public class ExcelImportController {

    private final VendorProductImportService vendorProductImportService;

    /**
     * Import products from Excel/CSV file
     */
    @PostMapping("/import/{vendorId}")
    public ResponseEntity<ExcelImportResponseDto> importProducts(
            @PathVariable Long vendorId,
            @RequestParam("file") MultipartFile file) {
        
        log.info("📊 Excel import request received for vendor: {} with file: {}", 
                vendorId, file.getOriginalFilename());

        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(
                    ExcelImportResponseDto.builder()
                        .success(false)
                        .message("File is empty")
                        .totalRows(0)
                        .successfulImports(0)
                        .failedImports(0)
                        .build()
                );
            }

            // Check file type
            String fileName = file.getOriginalFilename();
            if (fileName == null || (!fileName.toLowerCase().endsWith(".xlsx") 
                    && !fileName.toLowerCase().endsWith(".xls") 
                    && !fileName.toLowerCase().endsWith(".csv"))) {
                return ResponseEntity.badRequest().body(
                    ExcelImportResponseDto.builder()
                        .success(false)
                        .message("Invalid file format. Only Excel (.xlsx, .xls) and CSV (.csv) files are supported")
                        .totalRows(0)
                        .successfulImports(0)
                        .failedImports(0)
                        .build()
                );
            }

            // Process the file
            ExcelImportResponseDto response = vendorProductImportService.importProductsFromExcel(file, vendorId);
            
            log.info("📈 Import completed - Success: {}, Failed: {}, Total: {}", 
                    response.getSuccessfulImports(), 
                    response.getFailedImports(), 
                    response.getTotalRows());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error during Excel import: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ExcelImportResponseDto.builder()
                    .success(false)
                    .message("Import failed: " + e.getMessage())
                    .totalRows(0)
                    .successfulImports(0)
                    .failedImports(0)
                    .build()
            );
        }
    }

    /**
     * Download sample template for product import
     */
    @GetMapping("/template")
    public ResponseEntity<String> downloadTemplate() {
        String csvTemplate = "Category,Subcategory,Minor Category,Product/Services Name,Description,Price\n" +
                            "Electronics,Mobile Phones,,iPhone 14,Latest Apple smartphone,75000\n" +
                            "Electronics,Laptops,,MacBook Pro,High-performance laptop,150000\n" +
                            "Clothing,Men's Wear,Shirts,Cotton Shirt,Comfortable cotton shirt,1500\n";
        
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=product_import_template.csv")
                .header("Content-Type", "text/csv")
                .body(csvTemplate);
    }
}

