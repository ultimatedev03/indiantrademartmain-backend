package com.itech.itech_backend.modules.vendor.service;

import com.itech.itech_backend.modules.shared.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DataEntryService {
    // Category Operations
    Page<CategoryDto> getAllCategories(String search, Pageable pageable);
    CategoryDto getCategoryById(Long id);
    CategoryDto createCategory(CreateCategoryDto createCategoryDto);
    CategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto);
    void deleteCategory(Long id);

    // SubCategory Operations
    Page<SubCategoryDto> getAllSubCategories(String search, Long categoryId, Pageable pageable);
    List<SubCategoryDto> getSubCategoriesByCategory(Long categoryId);
    SubCategoryDto createSubCategory(CreateSubCategoryDto createSubCategoryDto);
    SubCategoryDto updateSubCategory(Long id, UpdateSubCategoryDto updateSubCategoryDto);
    void deleteSubCategory(Long id);

    // MicroCategory Operations
    Page<MicroCategoryDto> getAllMicroCategories(String search, Long subCategoryId, Pageable pageable);
    List<MicroCategoryDto> getMicroCategoriesBySubCategory(Long subCategoryId);
    MicroCategoryDto createMicroCategory(CreateMicroCategoryDto createMicroCategoryDto);
    MicroCategoryDto updateMicroCategory(Long id, UpdateMicroCategoryDto updateMicroCategoryDto);
    void deleteMicroCategory(Long id);

    // Product Operations
    Page<ProductDataEntryDto> getAllProducts(String search, Long categoryId, Long subCategoryId, Long microCategoryId, Pageable pageable);
    ProductDataEntryDto getProductById(Long id);
    ProductDataEntryDto createProduct(CreateProductDto createProductDto);
    ProductDataEntryDto updateProduct(Long id, UpdateProductDto updateProductDto);
    void deleteProduct(Long id);

    // Bulk Operations
    BulkImportResponseDto bulkImportCategories(MultipartFile file);
    BulkImportResponseDto bulkImportProducts(MultipartFile file);
    ResponseEntity<byte[]> exportCategories();
    ResponseEntity<byte[]> exportProducts();

    // Analytics
    DashboardAnalyticsDto getDashboardAnalytics();
    List<CategoryStatsDto> getCategoryStats();

    // Hierarchy Data
    List<CategoryHierarchyDto> getFullCategoryHierarchy();
    List<MegaMenuDataDto> getMegaMenuData();

    // Search & Suggestions
    SearchSuggestionsDto getSearchSuggestions(String query);
}

