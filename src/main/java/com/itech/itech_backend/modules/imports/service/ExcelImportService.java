package com.itech.itech_backend.modules.imports.service;

import com.itech.itech_backend.modules.imports.model.ImportResult;
import com.itech.itech_backend.modules.imports.model.ImportResult.ImportError;
import com.itech.itech_backend.modules.imports.model.ImportResult.ImportWarning;
import com.itech.itech_backend.modules.category.model.Category;
import com.itech.itech_backend.modules.category.service.CategoryService;
import com.itech.itech_backend.modules.city.model.City;
import com.itech.itech_backend.modules.city.service.CityService;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExcelImportService {

    private final CategoryService categoryService;
    private final CityService cityService;
    private final UserRepository userRepository;
    
    // Store import results for progress tracking
    private final Map<String, ImportResult> importResults = new ConcurrentHashMap<>();
    
    /**
     * Import categories from Excel file
     */
    @Transactional
    public ImportResult importCategories(MultipartFile file, Long employeeId) {
        String importId = generateImportId();
        ImportResult result = initializeImportResult(importId, file, ImportResult.ImportType.CATEGORIES, employeeId);
        importResults.put(importId, result);
        
        try {
            log.info("🔄 Starting category import from file: {}", file.getOriginalFilename());
            result.setStatus(ImportResult.ImportStatus.IN_PROGRESS);
            
            User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            
            Workbook workbook = createWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Validate headers
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file must have a header row");
            }
            
            Map<String, Integer> columnMap = mapColumns(headerRow, getCategoryColumnMapping());
            validateRequiredColumns(columnMap, Arrays.asList("name"));
            
            int totalRows = sheet.getLastRowNum();
            result.setTotalRows(totalRows);
            
            AtomicInteger processedRows = new AtomicInteger(0);
            AtomicInteger successfulRows = new AtomicInteger(0);
            AtomicInteger failedRows = new AtomicInteger(0);
            
            // Process each row
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    result.setSkippedRows(result.getSkippedRows() + 1);
                    continue;
                }
                
                try {
                    processCategory(row, columnMap, employee, result, i);
                    successfulRows.incrementAndGet();
                } catch (Exception e) {
                    failedRows.incrementAndGet();
                    result.addError(i, "general", "", e.getMessage(), ImportError.ErrorType.VALIDATION_ERROR);
                    log.error("Error processing row {}: {}", i, e.getMessage());
                }
                
                processedRows.incrementAndGet();
                result.updateProgress(processedRows.get(), totalRows);
            }
            
            result.setSuccessfulRows(successfulRows.get());
            result.setFailedRows(failedRows.get());
            result.setStatus(ImportResult.ImportStatus.COMPLETED);
            result.setEndTime(LocalDateTime.now());
            
            log.info("✅ Category import completed. Success: {}, Failed: {}", successfulRows.get(), failedRows.get());
            workbook.close();
            
        } catch (Exception e) {
            log.error("❌ Category import failed: {}", e.getMessage(), e);
            result.setStatus(ImportResult.ImportStatus.FAILED);
            result.setEndTime(LocalDateTime.now());
            result.addError(0, "general", "", "Import failed: " + e.getMessage(), ImportError.ErrorType.VALIDATION_ERROR);
        }
        
        return result;
    }
    
    /**
     * Import cities from Excel file
     */
    @Transactional
    public ImportResult importCities(MultipartFile file, Long employeeId) {
        String importId = generateImportId();
        ImportResult result = initializeImportResult(importId, file, ImportResult.ImportType.CITIES, employeeId);
        importResults.put(importId, result);
        
        try {
            log.info("🔄 Starting city import from file: {}", file.getOriginalFilename());
            result.setStatus(ImportResult.ImportStatus.IN_PROGRESS);
            
            User employee = userRepository.findById(employeeId)
                    .orElseThrow(() -> new RuntimeException("Employee not found"));
            
            Workbook workbook = createWorkbook(file);
            Sheet sheet = workbook.getSheetAt(0);
            
            // Validate headers
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new RuntimeException("Excel file must have a header row");
            }
            
            Map<String, Integer> columnMap = mapColumns(headerRow, getCityColumnMapping());
            validateRequiredColumns(columnMap, Arrays.asList("name", "country"));
            
            int totalRows = sheet.getLastRowNum();
            result.setTotalRows(totalRows);
            
            AtomicInteger processedRows = new AtomicInteger(0);
            AtomicInteger successfulRows = new AtomicInteger(0);
            AtomicInteger failedRows = new AtomicInteger(0);
            
            // Process each row
            for (int i = 1; i <= totalRows; i++) {
                Row row = sheet.getRow(i);
                if (row == null || isEmptyRow(row)) {
                    result.setSkippedRows(result.getSkippedRows() + 1);
                    continue;
                }
                
                try {
                    processCity(row, columnMap, employee, result, i);
                    successfulRows.incrementAndGet();
                } catch (Exception e) {
                    failedRows.incrementAndGet();
                    result.addError(i, "general", "", e.getMessage(), ImportError.ErrorType.VALIDATION_ERROR);
                    log.error("Error processing row {}: {}", i, e.getMessage());
                }
                
                processedRows.incrementAndGet();
                result.updateProgress(processedRows.get(), totalRows);
            }
            
            result.setSuccessfulRows(successfulRows.get());
            result.setFailedRows(failedRows.get());
            result.setStatus(ImportResult.ImportStatus.COMPLETED);
            result.setEndTime(LocalDateTime.now());
            
            log.info("✅ City import completed. Success: {}, Failed: {}", successfulRows.get(), failedRows.get());
            workbook.close();
            
        } catch (Exception e) {
            log.error("❌ City import failed: {}", e.getMessage(), e);
            result.setStatus(ImportResult.ImportStatus.FAILED);
            result.setEndTime(LocalDateTime.now());
            result.addError(0, "general", "", "Import failed: " + e.getMessage(), ImportError.ErrorType.VALIDATION_ERROR);
        }
        
        return result;
    }
    
    /**
     * Get import result by ID
     */
    public ImportResult getImportResult(String importId) {
        return importResults.get(importId);
    }
    
    /**
     * Get all import results for a user
     */
    public List<ImportResult> getImportHistory(Long userId) {
        return importResults.values().stream()
                .filter(result -> Objects.equals(result.getImportedBy(), userId))
                .sorted((r1, r2) -> r2.getStartTime().compareTo(r1.getStartTime()))
                .toList();
    }
    
    // Private helper methods
    
    private void processCategory(Row row, Map<String, Integer> columnMap, User employee, ImportResult result, int rowNumber) {
        try {
            String name = getCellValueAsString(row, columnMap.get("name"));
            String description = getCellValueAsString(row, columnMap.get("description"));
            String parentCategoryName = getCellValueAsString(row, columnMap.get("parent_category"));
            Integer displayOrder = getCellValueAsInteger(row, columnMap.get("display_order"));
            Double commissionPercentage = getCellValueAsDouble(row, columnMap.get("commission_percentage"));
            Boolean visibleToVendors = getCellValueAsBoolean(row, columnMap.get("visible_to_vendors"), true);
            Boolean visibleToCustomers = getCellValueAsBoolean(row, columnMap.get("visible_to_customers"), true);
            String iconUrl = getCellValueAsString(row, columnMap.get("icon_url"));
            
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("Category name is required");
            }
            
            // Find parent category if specified
            Long parentCategoryId = null;
            if (parentCategoryName != null && !parentCategoryName.trim().isEmpty()) {
                try {
                    List<Category> categories = categoryService.searchCategories(parentCategoryName);
                    Category parentCategory = categories.stream()
                            .filter(cat -> cat.getName().equalsIgnoreCase(parentCategoryName.trim()))
                            .findFirst()
                            .orElse(null);
                    
                    if (parentCategory != null) {
                        parentCategoryId = parentCategory.getId();
                    } else {
                        result.addWarning(rowNumber, "parent_category", parentCategoryName, 
                                "Parent category not found, creating as root category", 
                                ImportWarning.WarningType.MISSING_OPTIONAL_FIELD);
                    }
                } catch (Exception e) {
                    result.addWarning(rowNumber, "parent_category", parentCategoryName, 
                            "Error finding parent category: " + e.getMessage(), 
                            ImportWarning.WarningType.MISSING_OPTIONAL_FIELD);
                }
            }
            
            // Create category
            categoryService.createCategory(
                    name.trim(),
                    description,
                    parentCategoryId,
                    employee.getId(),
                    iconUrl,
                    displayOrder != null ? displayOrder : 0,
                    commissionPercentage != null ? commissionPercentage : 0.0,
                    visibleToVendors,
                    visibleToCustomers
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process category: " + e.getMessage(), e);
        }
    }
    
    private void processCity(Row row, Map<String, Integer> columnMap, User employee, ImportResult result, int rowNumber) {
        try {
            String name = getCellValueAsString(row, columnMap.get("name"));
            String stateProvince = getCellValueAsString(row, columnMap.get("state_province"));
            String country = getCellValueAsString(row, columnMap.get("country"));
            String postalCode = getCellValueAsString(row, columnMap.get("postal_code"));
            Boolean isActive = getCellValueAsBoolean(row, columnMap.get("is_active"), true);
            Double latitude = getCellValueAsDouble(row, columnMap.get("latitude"));
            Double longitude = getCellValueAsDouble(row, columnMap.get("longitude"));
            String timeZone = getCellValueAsString(row, columnMap.get("time_zone"));
            String notes = getCellValueAsString(row, columnMap.get("notes"));
            Boolean isMajorCity = getCellValueAsBoolean(row, columnMap.get("is_major_city"), false);
            
            if (name == null || name.trim().isEmpty()) {
                throw new RuntimeException("City name is required");
            }
            
            if (country == null || country.trim().isEmpty()) {
                throw new RuntimeException("Country is required");
            }
            
            // Create city
            cityService.createCity(
                    name.trim(),
                    stateProvince,
                    country.trim(),
                    postalCode,
                    latitude,
                    longitude,
                    timeZone,
                    notes,
                    isMajorCity,
                    isActive,
                    employee.getId()
            );
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to process city: " + e.getMessage(), e);
        }
    }
    
    private ImportResult initializeImportResult(String importId, MultipartFile file, ImportResult.ImportType importType, Long employeeId) {
        User employee = userRepository.findById(employeeId).orElse(null);
        
        return ImportResult.builder()
                .importId(importId)
                .fileName(file.getOriginalFilename())
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .fileSize(file.getSize())
                .importType(importType)
                .status(ImportResult.ImportStatus.PENDING)
                .startTime(LocalDateTime.now())
                .importedBy(employeeId)
                .importedByName(employee != null ? employee.getName() : "Unknown")
                .totalRows(0)
                .successfulRows(0)
                .failedRows(0)
                .skippedRows(0)
                .errors(new ArrayList<>())
                .warnings(new ArrayList<>())
                .statistics(new HashMap<>())
                .build();
    }
    
    private String generateImportId() {
        return "IMP_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private Workbook createWorkbook(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.endsWith(".xlsx")) {
            return new XSSFWorkbook(file.getInputStream());
        } else if (fileName != null && fileName.endsWith(".xls")) {
            return new HSSFWorkbook(file.getInputStream());
        } else {
            throw new RuntimeException("Unsupported file format. Please use .xls or .xlsx files.");
        }
    }
    
    private Map<String, Integer> mapColumns(Row headerRow, Map<String, String> columnMapping) {
        Map<String, Integer> columnMap = new HashMap<>();
        
        for (int i = 0; i < headerRow.getLastCellNum(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null) {
                String headerValue = cell.getStringCellValue().toLowerCase().trim();
                
                // Find matching column mapping
                for (Map.Entry<String, String> entry : columnMapping.entrySet()) {
                    if (headerValue.equals(entry.getValue().toLowerCase()) || 
                        headerValue.replace(" ", "_").equals(entry.getKey())) {
                        columnMap.put(entry.getKey(), i);
                        break;
                    }
                }
            }
        }
        
        return columnMap;
    }
    
    private void validateRequiredColumns(Map<String, Integer> columnMap, List<String> requiredColumns) {
        List<String> missingColumns = new ArrayList<>();
        
        for (String requiredColumn : requiredColumns) {
            if (!columnMap.containsKey(requiredColumn)) {
                missingColumns.add(requiredColumn);
            }
        }
        
        if (!missingColumns.isEmpty()) {
            throw new RuntimeException("Missing required columns: " + String.join(", ", missingColumns));
        }
    }
    
    private boolean isEmptyRow(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValueAsString(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }
    
    private String getCellValueAsString(Row row, Integer columnIndex) {
        if (columnIndex == null || row == null) return null;
        Cell cell = row.getCell(columnIndex);
        return getCellValueAsString(cell);
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return null;
        }
    }
    
    private Integer getCellValueAsInteger(Row row, Integer columnIndex) {
        String value = getCellValueAsString(row, columnIndex);
        if (value == null || value.trim().isEmpty()) return null;
        
        try {
            return Integer.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Double getCellValueAsDouble(Row row, Integer columnIndex) {
        String value = getCellValueAsString(row, columnIndex);
        if (value == null || value.trim().isEmpty()) return null;
        
        try {
            return Double.valueOf(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    private Boolean getCellValueAsBoolean(Row row, Integer columnIndex, Boolean defaultValue) {
        String value = getCellValueAsString(row, columnIndex);
        if (value == null || value.trim().isEmpty()) return defaultValue;
        
        value = value.trim().toLowerCase();
        return "true".equals(value) || "yes".equals(value) || "1".equals(value) || "active".equals(value);
    }
    
    private Map<String, String> getCategoryColumnMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "Category Name");
        mapping.put("description", "Description");
        mapping.put("parent_category", "Parent Category");
        mapping.put("display_order", "Display Order");
        mapping.put("commission_percentage", "Commission %");
        mapping.put("visible_to_vendors", "Vendor Visible");
        mapping.put("visible_to_customers", "Customer Visible");
        mapping.put("icon_url", "Icon URL");
        return mapping;
    }
    
    private Map<String, String> getCityColumnMapping() {
        Map<String, String> mapping = new HashMap<>();
        mapping.put("name", "City Name");
        mapping.put("state_province", "State/Province");
        mapping.put("country", "Country");
        mapping.put("postal_code", "Postal Code");
        mapping.put("is_active", "Status");
        mapping.put("latitude", "Latitude");
        mapping.put("longitude", "Longitude");
        mapping.put("time_zone", "Time Zone");
        mapping.put("notes", "Notes");
        mapping.put("is_major_city", "Major City");
        return mapping;
    }
}
