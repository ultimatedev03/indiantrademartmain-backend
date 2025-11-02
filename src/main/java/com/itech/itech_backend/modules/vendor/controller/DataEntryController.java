package com.itech.itech_backend.modules.vendor.controller;

import com.itech.itech_backend.modules.shared.dto.*;
import com.itech.itech_backend.modules.shared.model.*;
import com.itech.itech_backend.modules.vendor.service.DataEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/dataentry")
@RequiredArgsConstructor
public class DataEntryController {

    private final DataEntryService dataEntryService;

    // ================ CATEGORIES ================
    
    @GetMapping("/categories")
    public ResponseEntity<Page<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "") String search,
            Pageable pageable) {
        return ResponseEntity.ok(dataEntryService.getAllCategories(search, pageable));
    }

    @GetMapping("/categories/{id}")
    public ResponseEntity<CategoryDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(dataEntryService.getCategoryById(id));
    }

    @PostMapping("/categories")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CreateCategoryDto createCategoryDto) {
        return ResponseEntity.ok(dataEntryService.createCategory(createCategoryDto));
    }

    @PutMapping("/categories/{id}")
    // @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable Long id, @Valid @RequestBody UpdateCategoryDto updateCategoryDto) {
        return ResponseEntity.ok(dataEntryService.updateCategory(id, updateCategoryDto));
    }

    @DeleteMapping("/categories/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        dataEntryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ================ SUBCATEGORIES ================
    
    @GetMapping("/subcategories")
    public ResponseEntity<Page<SubCategoryDto>> getAllSubCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        return ResponseEntity.ok(dataEntryService.getAllSubCategories(search, categoryId, pageable));
    }

    @GetMapping("/categories/{categoryId}/subcategories")
    public ResponseEntity<List<SubCategoryDto>> getSubCategoriesByCategory(@PathVariable Long categoryId) {
        return ResponseEntity.ok(dataEntryService.getSubCategoriesByCategory(categoryId));
    }

    @PostMapping("/subcategories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<SubCategoryDto> createSubCategory(@Valid @RequestBody CreateSubCategoryDto createSubCategoryDto) {
        return ResponseEntity.ok(dataEntryService.createSubCategory(createSubCategoryDto));
    }

    @PutMapping("/subcategories/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<SubCategoryDto> updateSubCategory(@PathVariable Long id, @Valid @RequestBody UpdateSubCategoryDto updateSubCategoryDto) {
        return ResponseEntity.ok(dataEntryService.updateSubCategory(id, updateSubCategoryDto));
    }

    @DeleteMapping("/subcategories/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        dataEntryService.deleteSubCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ================ MICROCATEGORIES ================
    
    @GetMapping("/microcategories")
    public ResponseEntity<Page<MicroCategoryDto>> getAllMicroCategories(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long subCategoryId,
            Pageable pageable) {
        return ResponseEntity.ok(dataEntryService.getAllMicroCategories(search, subCategoryId, pageable));
    }

    @GetMapping("/subcategories/{subCategoryId}/microcategories")
    public ResponseEntity<List<MicroCategoryDto>> getMicroCategoriesBySubCategory(@PathVariable Long subCategoryId) {
        return ResponseEntity.ok(dataEntryService.getMicroCategoriesBySubCategory(subCategoryId));
    }

    @PostMapping("/microcategories")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<MicroCategoryDto> createMicroCategory(@Valid @RequestBody CreateMicroCategoryDto createMicroCategoryDto) {
        return ResponseEntity.ok(dataEntryService.createMicroCategory(createMicroCategoryDto));
    }

    @PutMapping("/microcategories/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<MicroCategoryDto> updateMicroCategory(@PathVariable Long id, @Valid @RequestBody UpdateMicroCategoryDto updateMicroCategoryDto) {
        return ResponseEntity.ok(dataEntryService.updateMicroCategory(id, updateMicroCategoryDto));
    }

    @DeleteMapping("/microcategories/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMicroCategory(@PathVariable Long id) {
        dataEntryService.deleteMicroCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ================ PRODUCTS ================
    
    @GetMapping("/products")
    public ResponseEntity<Page<ProductDataEntryDto>> getAllProducts(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long subCategoryId,
            @RequestParam(required = false) Long microCategoryId,
            Pageable pageable) {
        return ResponseEntity.ok(dataEntryService.getAllProducts(search, categoryId, subCategoryId, microCategoryId, pageable));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductDataEntryDto> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(dataEntryService.getProductById(id));
    }

    @PostMapping("/products")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<ProductDataEntryDto> createProduct(@Valid @RequestBody CreateProductDto createProductDto) {
        return ResponseEntity.ok(dataEntryService.createProduct(createProductDto));
    }

    @PutMapping("/products/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<ProductDataEntryDto> updateProduct(@PathVariable Long id, @Valid @RequestBody UpdateProductDto updateProductDto) {
        return ResponseEntity.ok(dataEntryService.updateProduct(id, updateProductDto));
    }

    @DeleteMapping("/products/{id}")
    // @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        dataEntryService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // ================ BULK OPERATIONS ================
    
    @PostMapping("/categories/bulk-import")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<BulkImportResponseDto> bulkImportCategories(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataEntryService.bulkImportCategories(file));
    }

    @PostMapping("/products/bulk-import")
    @PreAuthorize("hasRole('ADMIN') or hasRole('DATA_ENTRY')")
    public ResponseEntity<BulkImportResponseDto> bulkImportProducts(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(dataEntryService.bulkImportProducts(file));
    }

    @GetMapping("/export/categories")
    public ResponseEntity<byte[]> exportCategories() {
        return dataEntryService.exportCategories();
    }

    @GetMapping("/export/products")
    public ResponseEntity<byte[]> exportProducts() {
        return dataEntryService.exportProducts();
    }

    // ================ ANALYTICS ================
    
    @GetMapping("/analytics/dashboard")
    public ResponseEntity<DashboardAnalyticsDto> getDashboardAnalytics() {
        return ResponseEntity.ok(dataEntryService.getDashboardAnalytics());
    }

    @GetMapping("/analytics/category-stats")
    public ResponseEntity<List<CategoryStatsDto>> getCategoryStats() {
        return ResponseEntity.ok(dataEntryService.getCategoryStats());
    }

    // ================ CATEGORY HIERARCHY ================
    
    @GetMapping("/hierarchy/full")
    public ResponseEntity<List<CategoryHierarchyDto>> getFullCategoryHierarchy() {
        return ResponseEntity.ok(dataEntryService.getFullCategoryHierarchy());
    }

    @GetMapping("/hierarchy/megamenu")
    public ResponseEntity<List<MegaMenuDataDto>> getMegaMenuData() {
        return ResponseEntity.ok(dataEntryService.getMegaMenuData());
    }

    // ================ SEARCH & SUGGESTIONS ================
    
    @GetMapping("/search/suggestions")
    public ResponseEntity<SearchSuggestionsDto> getSearchSuggestions(@RequestParam String query) {
        return ResponseEntity.ok(dataEntryService.getSearchSuggestions(query));
    }
}

