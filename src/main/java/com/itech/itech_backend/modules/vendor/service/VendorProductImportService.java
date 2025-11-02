package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.shared.dto.ExcelImportDto;
import com.itech.itech_backend.modules.shared.dto.ExcelImportResponseDto;
import com.itech.itech_backend.modules.shared.dto.ProductDto;
import com.itech.itech_backend.modules.buyer.model.Category;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.shared.dto.ProductDto;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.core.repository.UserRepository;
import com.itech.itech_backend.modules.buyer.service.ProductService;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import com.itech.itech_backend.modules.core.model.User;
import com.itech.itech_backend.modules.core.service.UserService;
import com.itech.itech_backend.modules.vendor.service.VendorsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class VendorProductImportService {

    private final ProductService productService;
    private final BuyerCategoryRepository categoryRepository;
    private final UserService userService;
    private final VendorsService vendorsService;

    @Transactional
    public ExcelImportResponseDto importProductsFromExcel(MultipartFile file, Long vendorId) {
        try {
            log.info("🔍 Looking for vendor with ID: {} in Vendors table", vendorId);
            
            // Find vendor in Vendors table
            Vendors vendor = null;
            try {
                Optional<Vendors> vendorOpt = vendorsService.getVendorById(vendorId);
                if (vendorOpt.isPresent()) {
                    vendor = vendorOpt.get();
                    log.info("✅ Found vendor in Vendors table: {} (ID: {})", vendor.getName(), vendor.getId());
                } else {
                    log.error("❌ Vendor not found in Vendors table with ID: {}", vendorId);
                    // Let's list available vendors for debugging
                    try {
                        log.info("🔍 Attempting to get all vendors for debugging...");
                        List<Vendors> allVendors = vendorsService.getAllVendors();
                        log.info("📋 Available vendors: {}", allVendors.stream()
                            .map(v -> "ID: " + v.getId() + ", Name: " + v.getName())
                            .toList());
                    } catch (Exception listEx) {
                        log.error("Could not list vendors: {}", listEx.getMessage());
                    }
                    throw new RuntimeException("Vendor not found with ID: " + vendorId + ". Please check if the vendor exists in the Vendors table.");
                }
            } catch (Exception e) {
                log.error("⚠️ Error querying Vendors table: {}", e.getMessage());
                throw new RuntimeException("Error accessing Vendors table: " + e.getMessage());
            }

            List<ExcelImportDto> excelData = parseFile(file);
            
            // Process import for vendor
            return processImportDataForVendor(excelData, vendor);

        } catch (Exception e) {
            log.error("Error importing file", e);
            return ExcelImportResponseDto.builder()
                    .success(false)
                    .message("Failed to import file: " + e.getMessage())
                    .totalRows(0)
                    .successfulImports(0)
                    .failedImports(0)
                    .errors(List.of(e.getMessage()))
                    .build();
        }
    }

    /**
     * Unified file parsing method that handles both CSV and Excel files
     */
    private List<ExcelImportDto> parseFile(MultipartFile file) throws IOException {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new IOException("File name is null");
        }

        fileName = fileName.toLowerCase();
        if (fileName.endsWith(".csv")) {
            return parseCsvFile(file);
        } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
            return parseExcelFile(file);
        } else {
            throw new IOException("Unsupported file format. Please use CSV (.csv) or Excel (.xlsx/.xls) files.");
        }
    }

    /**
     * Parse CSV file
     */
    private List<ExcelImportDto> parseCsvFile(MultipartFile file) throws IOException {
        List<ExcelImportDto> csvData = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int rowNumber = 0;
            
            // Skip header row
            reader.readLine();
            rowNumber++;
            
            while ((line = reader.readLine()) != null) {
                rowNumber++;
                if (line.trim().isEmpty()) continue;
                
                ExcelImportDto dto = parseCsvRow(line, rowNumber);
                csvData.add(dto);
            }
        }
        
        return csvData;
    }

    /**
     * Parse Excel file
     */
    private List<ExcelImportDto> parseExcelFile(MultipartFile file) throws IOException {
        List<ExcelImportDto> excelData = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            
            // Skip header row (row 0)
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                ExcelImportDto dto = parseRow(row, i + 1);
                excelData.add(dto);
            }
        }

        return excelData;
    }

    private ExcelImportDto parseRow(Row row, int rowNumber) {
        ExcelImportDto dto = ExcelImportDto.builder()
                .rowNumber(rowNumber)
                .isValid(true)
                .build();

        try {
            // Expected columns (Your 5-Column Format):
            // A: Category (Required)
            // B: Subcategory (Optional) 
            // C: Product Name (Required)
            // D: Description (Optional)
            // E: Price (Required)

            dto.setCategory(getCellValueAsString(row.getCell(0)));
            dto.setSubcategory(getCellValueAsString(row.getCell(1)));
            dto.setProductName(getCellValueAsString(row.getCell(2)));
            dto.setDescription(getCellValueAsString(row.getCell(3)));
            
            // Price (required) - Column E (index 4)
            String priceStr = getCellValueAsString(row.getCell(4));
            if (priceStr != null && !priceStr.trim().isEmpty()) {
                dto.setPrice(new BigDecimal(priceStr.trim()));
            }
            
            // Set default values for optional fields
            if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
                dto.setDescription(dto.getProductName()); // Use product name as description if empty
            }
            dto.setMinOrderQuantity(1); // Default minimum order quantity
            dto.setUnit("piece"); // Default unit
            dto.setGstRate(18.0); // Default GST rate for India
            dto.setFreeShipping(false); // Default no free shipping


            // Validate required fields
            validateRequiredFields(dto);

        } catch (Exception e) {
            dto.setIsValid(false);
            dto.setErrorMessage("Error parsing row " + rowNumber + ": " + e.getMessage());
            log.error("Error parsing row {}: {}", rowNumber, e.getMessage());
        }

        return dto;
    }

    /**
     * Parse CSV row
     */
    private ExcelImportDto parseCsvRow(String line, int rowNumber) {
        ExcelImportDto dto = ExcelImportDto.builder()
                .rowNumber(rowNumber)
                .isValid(true)
                .build();

        try {
            // Simple CSV parsing - split by comma and handle quoted values
            String[] columns = parseCsvLine(line);
            
            // Expected columns (Your 6-Column Format):
            // 0: Category (Required)
            // 1: Subcategory (Optional) 
            // 2: Minor Category (Optional)
            // 3: Product/Services Name (Required)
            // 4: Description (Optional)
            // 5: Price (Required)

            dto.setCategory(columns.length > 0 ? columns[0].trim() : null);
            dto.setSubcategory(columns.length > 1 ? columns[1].trim() : null);
            // Minor category can be stored in specifications field for now
            String minorCategory = columns.length > 2 ? columns[2].trim() : null;
            dto.setProductName(columns.length > 3 ? columns[3].trim() : null);
            dto.setDescription(columns.length > 4 ? columns[4].trim() : null);
            
            // Price (required) - Column 5
            if (columns.length > 5 && !columns[5].trim().isEmpty()) {
                String priceStr = columns[5].trim();
                // Remove currency symbols and commas
                priceStr = priceStr.replaceAll("[₹$,]", "");
                dto.setPrice(new BigDecimal(priceStr));
            }
            
            // Store minor category in specifications if available
            if (minorCategory != null && !minorCategory.trim().isEmpty()) {
                dto.setSpecifications("Minor Category: " + minorCategory);
            }
            
            // Set default values for optional fields
            if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
                dto.setDescription(dto.getProductName()); // Use product name as description if empty
            }
            dto.setMinOrderQuantity(1); // Default minimum order quantity
            dto.setUnit("piece"); // Default unit
            dto.setGstRate(18.0); // Default GST rate for India
            dto.setFreeShipping(false); // Default no free shipping

            // Validate required fields
            validateRequiredFields(dto);

        } catch (Exception e) {
            dto.setIsValid(false);
            dto.setErrorMessage("Error parsing CSV row " + rowNumber + ": " + e.getMessage());
            log.error("Error parsing CSV row {}: {}", rowNumber, e.getMessage());
        }

        return dto;
    }

    /**
     * Parse CSV line handling quoted values
     */
    private String[] parseCsvLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                result.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        
        result.add(currentField.toString());
        return result.toArray(new String[0]);
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
                return "";
        }
    }

    private void validateRequiredFields(ExcelImportDto dto) {
        List<String> errors = new ArrayList<>();

        if (dto.getCategory() == null || dto.getCategory().trim().isEmpty()) {
            errors.add("Category is required");
        }
        if (dto.getProductName() == null || dto.getProductName().trim().isEmpty()) {
            errors.add("Product name is required");
        }
        if (dto.getPrice() == null || dto.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            errors.add("Valid price is required");
        }

        if (!errors.isEmpty()) {
            dto.setIsValid(false);
            dto.setErrorMessage(String.join(", ", errors));
        }
    }

    private ExcelImportResponseDto processImportDataForVendor(List<ExcelImportDto> excelData, Vendors vendor) {
        List<String> errors = new ArrayList<>();
        List<ExcelImportDto> failedRecords = new ArrayList<>();
        List<Long> createdProductIds = new ArrayList<>();
        
        int successfulImports = 0;
        int failedImports = 0;
        int skippedRows = 0;

        for (ExcelImportDto dto : excelData) {
            if (!dto.getIsValid()) {
                failedRecords.add(dto);
                errors.add("Row " + dto.getRowNumber() + ": " + dto.getErrorMessage());
                failedImports++;
                continue;
            }

            try {
                // Process each product in its own transaction to avoid rollback of entire batch
                Long productId = processProductImport(dto, vendor);
                createdProductIds.add(productId);
                successfulImports++;

                log.info("Successfully imported product: {} (Row {}) for vendor: {}", dto.getProductName(), dto.getRowNumber(), vendor.getName());

            } catch (Exception e) {
                dto.setErrorMessage("Failed to create product: " + e.getMessage());
                failedRecords.add(dto);
                errors.add("Row " + dto.getRowNumber() + ": " + e.getMessage());
                failedImports++;
                log.error("Failed to import product at row {}: {}", dto.getRowNumber(), e.getMessage());
            }
        }

        return ExcelImportResponseDto.builder()
                .success(failedImports == 0)
                .totalRows(excelData.size())
                .successfulImports(successfulImports)
                .failedImports(failedImports)
                .skippedRows(skippedRows)
                .errors(errors)
                .failedRecords(failedRecords)
                .createdProductIds(createdProductIds)
                .message(String.format("Import completed: %d successful, %d failed out of %d total rows", 
                        successfulImports, failedImports, excelData.size()))
                .build();
    }

    private ExcelImportResponseDto processImportData(List<ExcelImportDto> excelData, User vendor) {
        List<String> errors = new ArrayList<>();
        List<ExcelImportDto> failedRecords = new ArrayList<>();
        List<Long> createdProductIds = new ArrayList<>();
        
        int successfulImports = 0;
        int failedImports = 0;
        int skippedRows = 0;

        for (ExcelImportDto dto : excelData) {
            if (!dto.getIsValid()) {
                failedRecords.add(dto);
                errors.add("Row " + dto.getRowNumber() + ": " + dto.getErrorMessage());
                failedImports++;
                continue;
            }

            try {
                // Find or create category
                Category category = findOrCreateCategory(dto.getCategory(), dto.getSubcategory());

                // Create ProductDto
                ProductDto productDto = convertToProductDto(dto, category.getId());

                // Create product
                productDto.setVendorId(vendor.getId());
                Product product = productService.addProduct(productDto);
                createdProductIds.add(product.getId());
                successfulImports++;

                log.info("Successfully imported product: {} (Row {})", dto.getProductName(), dto.getRowNumber());

            } catch (Exception e) {
                dto.setErrorMessage("Failed to create product: " + e.getMessage());
                failedRecords.add(dto);
                errors.add("Row " + dto.getRowNumber() + ": " + e.getMessage());
                failedImports++;
                log.error("Failed to import product at row {}: {}", dto.getRowNumber(), e.getMessage());
            }
        }

        return ExcelImportResponseDto.builder()
                .success(failedImports == 0)
                .totalRows(excelData.size())
                .successfulImports(successfulImports)
                .failedImports(failedImports)
                .skippedRows(skippedRows)
                .errors(errors)
                .failedRecords(failedRecords)
                .createdProductIds(createdProductIds)
                .message(String.format("Import completed: %d successful, %d failed out of %d total rows", 
                        successfulImports, failedImports, excelData.size()))
                .build();
    }

    private Category findOrCreateCategory(String categoryName, String subcategoryName) {
        // Try to find existing category
        Optional<Category> existingCategory = categoryRepository.findByName(categoryName);
        
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        }

        // Create new category
        Category category = Category.builder()
                .name(categoryName)
                .description("Auto-created category during product import")
                .displayOrder(0)
                .isActive(true)
                .slug(categoryName.toLowerCase().replaceAll("[^a-zA-Z0-9]", "-"))
                .build();
        
        return categoryRepository.save(category);
    }

    /**
     * Process individual product import in its own transaction
     * This ensures that failure of one product doesn't rollback others
     */
    @Transactional
    private Long processProductImport(ExcelImportDto dto, Vendors vendor) {
        // Find or create category
        Category category = findOrCreateCategory(dto.getCategory(), dto.getSubcategory());

        // Create ProductDto
        ProductDto productDto = convertToProductDto(dto, category.getId());

        // Use vendor ID directly - now database schema references vendors table correctly
        productDto.setVendorId(vendor.getId());
        Product product = productService.addProduct(productDto);
        
        return product.getId();
    }

    private ProductDto convertToProductDto(ExcelImportDto dto, Long categoryId) {
        return ProductDto.builder()
                .name(dto.getProductName())
                .description(dto.getDescription())
                .price(dto.getPrice() != null ? dto.getPrice().doubleValue() : null)
                .originalPrice(dto.getOriginalPrice() != null ? dto.getOriginalPrice().doubleValue() : null)
                .categoryId(categoryId)
                .stock(100) // Default stock value since it's not in the import template
                .brand(dto.getBrand())
                .model(dto.getModel())
                .sku(dto.getSku())
                .minOrderQuantity(dto.getMinOrderQuantity())
                .unit(dto.getUnit())
                .specifications(dto.getSpecifications())
                .tags(dto.getTags())
                .gstRate(dto.getGstRate())
                .weight(dto.getWeight())
                .length(dto.getLength())
                .width(dto.getWidth())
                .height(dto.getHeight())
                .freeShipping(dto.getFreeShipping())
                .shippingCharge(dto.getShippingCharge())
                .isActive(true)
                .build();
    }

}

