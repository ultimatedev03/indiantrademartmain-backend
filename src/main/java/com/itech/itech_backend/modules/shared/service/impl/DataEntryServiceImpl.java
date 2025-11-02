package com.itech.itech_backend.modules.shared.service.impl;

import com.itech.itech_backend.modules.shared.dto.*;
import com.itech.itech_backend.modules.buyer.model.Category;
import com.itech.itech_backend.modules.buyer.model.SubCategory;
import com.itech.itech_backend.modules.buyer.model.MicroCategory;
import com.itech.itech_backend.modules.buyer.model.Product;
import com.itech.itech_backend.modules.shared.dto.*;
import com.itech.itech_backend.modules.vendor.repository.VendorsRepository;
import com.itech.itech_backend.modules.vendor.service.DataEntryService;
import com.itech.itech_backend.modules.buyer.repository.BuyerCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.SubCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.MicroCategoryRepository;
import com.itech.itech_backend.modules.buyer.repository.BuyerProductRepository;
import com.itech.itech_backend.modules.vendor.model.Vendors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DataEntryServiceImpl implements DataEntryService {
    
    private final BuyerCategoryRepository categoryRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final MicroCategoryRepository microCategoryRepository;
    private final BuyerProductRepository productRepository;
    private final VendorsRepository vendorsRepository;

    @Override
    public Page<CategoryDto> getAllCategories(String search, Pageable pageable) {
        Page<Category> categories;
        if (search != null && !search.trim().isEmpty()) {
            categories = categoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search.trim(), search.trim(), pageable);
        } else {
            categories = categoryRepository.findAll(pageable);
        }
        
        List<CategoryDto> categoryDtos = categories.getContent().stream()
            .map(this::convertToCategoryDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(categoryDtos, pageable, categories.getTotalElements());
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return convertToCategoryDto(category);
    }

    @Override
    public CategoryDto createCategory(CreateCategoryDto createCategoryDto) {
        Category category = Category.builder()
            .name(createCategoryDto.getName())
            .description(createCategoryDto.getDescription())
            .displayOrder(createCategoryDto.getDisplayOrder())
            .isActive(createCategoryDto.isActive())
            .metaTitle(createCategoryDto.getMetaTitle())
            .metaDescription(createCategoryDto.getMetaDescription())
            .slug(generateSlug(createCategoryDto.getName()))
            .build();
            
        Category savedCategory = categoryRepository.save(category);
        return convertToCategoryDto(savedCategory);
    }

    @Override
    public CategoryDto updateCategory(Long id, UpdateCategoryDto updateCategoryDto) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
            
        category.setName(updateCategoryDto.getName());
        category.setDescription(updateCategoryDto.getDescription());
        category.setDisplayOrder(updateCategoryDto.getDisplayOrder());
        category.setActive(updateCategoryDto.isActive());
        category.setMetaTitle(updateCategoryDto.getMetaTitle());
        category.setMetaDescription(updateCategoryDto.getMetaDescription());
        category.setSlug(updateCategoryDto.getSlug() != null ? 
            updateCategoryDto.getSlug() : generateSlug(updateCategoryDto.getName()));
            
        Category savedCategory = categoryRepository.save(category);
        return convertToCategoryDto(savedCategory);
    }

    @Override
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        
        // Check if category has any products
        long productCount = productRepository.countByCategoryId(id);
        if (productCount > 0) {
            throw new RuntimeException("Cannot delete category. It contains " + productCount + " product(s). Please delete or move the products first.");
        }
        
        // Check if category has any subcategories
        long subCategoryCount = subCategoryRepository.countByCategoryId(id);
        if (subCategoryCount > 0) {
            throw new RuntimeException("Cannot delete category. It contains " + subCategoryCount + " subcategory(ies). Please delete the subcategories first.");
        }
        
        categoryRepository.deleteById(id);
    }

    // Implement remaining methods similarly...

    @Override
    public Page<SubCategoryDto> getAllSubCategories(String search, Long categoryId, Pageable pageable) {
        Page<SubCategory> subCategories;
        
        if (categoryId != null && search != null && !search.trim().isEmpty()) {
            subCategories = subCategoryRepository.findByCategoryIdAndNameContainingIgnoreCase(
                categoryId, search.trim(), pageable);
        } else if (categoryId != null) {
            subCategories = subCategoryRepository.findAll(pageable);
            // Filter by categoryId manually or create specific method
        } else if (search != null && !search.trim().isEmpty()) {
            subCategories = subCategoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search.trim(), search.trim(), pageable);
        } else {
            subCategories = subCategoryRepository.findAll(pageable);
        }
        
        List<SubCategoryDto> subCategoryDtos = subCategories.getContent().stream()
            .map(this::convertToSubCategoryDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(subCategoryDtos, pageable, subCategories.getTotalElements());
    }

    @Override
    public List<SubCategoryDto> getSubCategoriesByCategory(Long categoryId) {
        List<SubCategory> subCategories = subCategoryRepository.findByCategoryIdOrderByDisplayOrder(categoryId);
        return subCategories.stream()
            .map(this::convertToSubCategoryDto)
            .collect(Collectors.toList());
    }

    @Override
    public SubCategoryDto createSubCategory(CreateSubCategoryDto createSubCategoryDto) {
        Category category = categoryRepository.findById(createSubCategoryDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + createSubCategoryDto.getCategoryId()));
            
        SubCategory subCategory = SubCategory.builder()
            .name(createSubCategoryDto.getName())
            .description(createSubCategoryDto.getDescription())
            .displayOrder(createSubCategoryDto.getDisplayOrder())
            .isActive(createSubCategoryDto.isActive())
            .metaTitle(createSubCategoryDto.getMetaTitle())
            .metaDescription(createSubCategoryDto.getMetaDescription())
            .slug(generateSlug(createSubCategoryDto.getName()))
            .category(category)
            .build();
            
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return convertToSubCategoryDto(savedSubCategory);
    }

    @Override
    public SubCategoryDto updateSubCategory(Long id, UpdateSubCategoryDto updateSubCategoryDto) {
        SubCategory subCategory = subCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("SubCategory not found with id: " + id));
            
        Category category = categoryRepository.findById(updateSubCategoryDto.getCategoryId())
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + updateSubCategoryDto.getCategoryId()));
            
        subCategory.setName(updateSubCategoryDto.getName());
        subCategory.setDescription(updateSubCategoryDto.getDescription());
        subCategory.setDisplayOrder(updateSubCategoryDto.getDisplayOrder());
        subCategory.setActive(updateSubCategoryDto.isActive());
        subCategory.setMetaTitle(updateSubCategoryDto.getMetaTitle());
        subCategory.setMetaDescription(updateSubCategoryDto.getMetaDescription());
        subCategory.setSlug(updateSubCategoryDto.getSlug() != null ? 
            updateSubCategoryDto.getSlug() : generateSlug(updateSubCategoryDto.getName()));
        subCategory.setCategory(category);
            
        SubCategory savedSubCategory = subCategoryRepository.save(subCategory);
        return convertToSubCategoryDto(savedSubCategory);
    }

    @Override
    public void deleteSubCategory(Long id) {
        if (!subCategoryRepository.existsById(id)) {
            throw new RuntimeException("SubCategory not found with id: " + id);
        }
        subCategoryRepository.deleteById(id);
    }

    @Override
    public Page<MicroCategoryDto> getAllMicroCategories(String search, Long subCategoryId, Pageable pageable) {
        Page<MicroCategory> microCategories;
        
        if (subCategoryId != null && search != null && !search.trim().isEmpty()) {
            microCategories = microCategoryRepository.findBySubCategoryIdAndNameContainingIgnoreCase(
                subCategoryId, search.trim(), pageable);
        } else if (subCategoryId != null) {
            microCategories = microCategoryRepository.findAll(pageable);
            // Filter by subCategoryId manually or create specific method
        } else if (search != null && !search.trim().isEmpty()) {
            microCategories = microCategoryRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                search.trim(), search.trim(), pageable);
        } else {
            microCategories = microCategoryRepository.findAll(pageable);
        }
        
        List<MicroCategoryDto> microCategoryDtos = microCategories.getContent().stream()
            .map(this::convertToMicroCategoryDto)
            .collect(Collectors.toList());
            
        return new PageImpl<>(microCategoryDtos, pageable, microCategories.getTotalElements());
    }

    @Override
    public List<MicroCategoryDto> getMicroCategoriesBySubCategory(Long subCategoryId) {
        List<MicroCategory> microCategories = microCategoryRepository.findBySubCategoryIdOrderByDisplayOrder(subCategoryId);
        return microCategories.stream()
            .map(this::convertToMicroCategoryDto)
            .collect(Collectors.toList());
    }

    @Override
    public MicroCategoryDto createMicroCategory(CreateMicroCategoryDto createMicroCategoryDto) {
        SubCategory subCategory = subCategoryRepository.findById(createMicroCategoryDto.getSubCategoryId())
            .orElseThrow(() -> new RuntimeException("SubCategory not found with id: " + createMicroCategoryDto.getSubCategoryId()));
            
        MicroCategory microCategory = MicroCategory.builder()
            .name(createMicroCategoryDto.getName())
            .description(createMicroCategoryDto.getDescription())
            .displayOrder(createMicroCategoryDto.getDisplayOrder())
            .isActive(createMicroCategoryDto.isActive())
            .metaTitle(createMicroCategoryDto.getMetaTitle())
            .metaDescription(createMicroCategoryDto.getMetaDescription())
            .slug(generateSlug(createMicroCategoryDto.getName()))
            .subCategory(subCategory)
            .build();
            
        MicroCategory savedMicroCategory = microCategoryRepository.save(microCategory);
        return convertToMicroCategoryDto(savedMicroCategory);
    }

    @Override
    public MicroCategoryDto updateMicroCategory(Long id, UpdateMicroCategoryDto updateMicroCategoryDto) {
        MicroCategory microCategory = microCategoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("MicroCategory not found with id: " + id));
            
        SubCategory subCategory = subCategoryRepository.findById(updateMicroCategoryDto.getSubCategoryId())
            .orElseThrow(() -> new RuntimeException("SubCategory not found with id: " + updateMicroCategoryDto.getSubCategoryId()));
            
        microCategory.setName(updateMicroCategoryDto.getName());
        microCategory.setDescription(updateMicroCategoryDto.getDescription());
        microCategory.setDisplayOrder(updateMicroCategoryDto.getDisplayOrder());
        microCategory.setActive(updateMicroCategoryDto.isActive());
        microCategory.setMetaTitle(updateMicroCategoryDto.getMetaTitle());
        microCategory.setMetaDescription(updateMicroCategoryDto.getMetaDescription());
        microCategory.setSlug(updateMicroCategoryDto.getSlug() != null ? 
            updateMicroCategoryDto.getSlug() : generateSlug(updateMicroCategoryDto.getName()));
        microCategory.setSubCategory(subCategory);
            
        MicroCategory savedMicroCategory = microCategoryRepository.save(microCategory);
        return convertToMicroCategoryDto(savedMicroCategory);
    }

    @Override
    public void deleteMicroCategory(Long id) {
        if (!microCategoryRepository.existsById(id)) {
            throw new RuntimeException("MicroCategory not found with id: " + id);
        }
        microCategoryRepository.deleteById(id);
    }

    @Override
    public Page<ProductDataEntryDto> getAllProducts(String search, Long categoryId, Long subCategoryId, Long microCategoryId, Pageable pageable) {
        return null;
    }

    @Override
    public ProductDataEntryDto getProductById(Long id) {
        return null;
    }

    @Override
    public ProductDataEntryDto createProduct(CreateProductDto createProductDto) {
        return null;
    }

    @Override
    public ProductDataEntryDto updateProduct(Long id, UpdateProductDto updateProductDto) {
        return null;
    }

    @Override
    public void deleteProduct(Long id) {
        // Implement logic to delete product
    }

    @Override
    public BulkImportResponseDto bulkImportCategories(MultipartFile file) {
        return null;
    }

    @Override
    public BulkImportResponseDto bulkImportProducts(MultipartFile file) {
        return null;
    }

    @Override
    public ResponseEntity<byte[]> exportCategories() {
        return null;
    }

    @Override
    public ResponseEntity<byte[]> exportProducts() {
        return null;
    }

    @Override
    public DashboardAnalyticsDto getDashboardAnalytics() {
        long totalCategories = categoryRepository.count();
        long totalSubCategories = subCategoryRepository.count();
        long totalMicroCategories = microCategoryRepository.count();
        long totalProducts = productRepository.count();
        long activeProducts = productRepository.countByIsActiveTrue();
        long inactiveProducts = totalProducts - activeProducts;
        long productsInStock = productRepository.countByInStockTrue();
        long productsOutOfStock = totalProducts - productsInStock;
        long totalVendors = vendorsRepository.count();
        
        return DashboardAnalyticsDto.builder()
            .totalCategories(totalCategories)
            .totalSubCategories(totalSubCategories)
            .totalMicroCategories(totalMicroCategories)
            .totalProducts(totalProducts)
            .activeProducts(activeProducts)
            .inactiveProducts(inactiveProducts)
            .productsInStock(productsInStock)
            .productsOutOfStock(productsOutOfStock)
            .totalVendors(totalVendors)
            .lastUpdated(LocalDateTime.now())
            .topCategories(getCategoryStats())
            .recentProducts(getRecentProducts())
            .build();
    }

    @Override
    public List<CategoryStatsDto> getCategoryStats() {
        List<Category> categories = categoryRepository.findAllOrderByDisplayOrder();
        long totalProducts = productRepository.count();
        
        return categories.stream()
            .limit(10) // Top 10 categories
            .map(category -> {
                long subCategoryCount = subCategoryRepository.countByCategoryId(category.getId());
                long microCategoryCount = microCategoryRepository.countByCategoryId(category.getId());
                long productCount = productRepository.countByCategoryId(category.getId());
                long activeProductCount = productRepository.countByIsActiveTrueAndCategoryId(category.getId());
                double percentage = totalProducts > 0 ? (double) productCount / totalProducts * 100 : 0;
                
                return CategoryStatsDto.builder()
                    .categoryId(category.getId())
                    .categoryName(category.getName())
                    .subCategoryCount(subCategoryCount)
                    .microCategoryCount(microCategoryCount)
                    .productCount(productCount)
                    .activeProductCount(activeProductCount)
                    .percentageOfTotal(percentage)
                    .build();
            })
            .collect(Collectors.toList());
    }

    @Override
    public List<CategoryHierarchyDto> getFullCategoryHierarchy() {
        return null;
    }

    @Override
    public List<MegaMenuDataDto> getMegaMenuData() {
        return null;
    }

    @Override
    public SearchSuggestionsDto getSearchSuggestions(String query) {
        return null;
    }
    
    // Helper Methods
    private CategoryDto convertToCategoryDto(Category category) {
        int subCategoryCount = category.getSubCategories() != null ? category.getSubCategories().size() : 0;
        long totalProductCount = category.getSubCategories() != null ? 
            category.getSubCategories().stream()
                .mapToLong(sc -> sc.getMicroCategories() != null ? 
                    sc.getMicroCategories().stream().mapToLong(mc -> mc.getProducts() != null ? mc.getProducts().size() : 0).sum() : 0)
                .sum() : 0;
                
        return CategoryDto.builder()
            .id(category.getId())
            .name(category.getName())
            .description(category.getDescription())
            .displayOrder(category.getDisplayOrder())
            .isActive(category.isActive())
            .metaTitle(category.getMetaTitle())
            .metaDescription(category.getMetaDescription())
            .slug(category.getSlug())
            .subCategoryCount(subCategoryCount)
            .totalProductCount((int) totalProductCount)
            .createdAt(category.getCreatedAt())
            .updatedAt(category.getUpdatedAt())
            .build();
    }
    
    private SubCategoryDto convertToSubCategoryDto(SubCategory subCategory) {
        int microCategoryCount = subCategory.getMicroCategories() != null ? subCategory.getMicroCategories().size() : 0;
        long productCount = subCategory.getMicroCategories() != null ? 
            subCategory.getMicroCategories().stream()
                .mapToLong(mc -> mc.getProducts() != null ? mc.getProducts().size() : 0)
                .sum() : 0;
                
        return SubCategoryDto.builder()
            .id(subCategory.getId())
            .name(subCategory.getName())
            .description(subCategory.getDescription())
            .displayOrder(subCategory.getDisplayOrder())
            .isActive(subCategory.isActive())
            .metaTitle(subCategory.getMetaTitle())
            .metaDescription(subCategory.getMetaDescription())
            .slug(subCategory.getSlug())
            .categoryId(subCategory.getCategory().getId())
            .categoryName(subCategory.getCategory().getName())
            .microCategoryCount(microCategoryCount)
            .productCount((int) productCount)
            .createdAt(subCategory.getCreatedAt())
            .updatedAt(subCategory.getUpdatedAt())
            .build();
    }
    
    private MicroCategoryDto convertToMicroCategoryDto(MicroCategory microCategory) {
        int productCount = microCategory.getProducts() != null ? microCategory.getProducts().size() : 0;
        
        return MicroCategoryDto.builder()
            .id(microCategory.getId())
            .name(microCategory.getName())
            .description(microCategory.getDescription())
            .displayOrder(microCategory.getDisplayOrder())
            .isActive(microCategory.isActive())
            .metaTitle(microCategory.getMetaTitle())
            .metaDescription(microCategory.getMetaDescription())
            .slug(microCategory.getSlug())
            .subCategoryId(microCategory.getSubCategory().getId())
            .subCategoryName(microCategory.getSubCategory().getName())
            .categoryId(microCategory.getSubCategory().getCategory().getId())
            .categoryName(microCategory.getSubCategory().getCategory().getName())
            .productCount(productCount)
            .createdAt(microCategory.getCreatedAt())
            .updatedAt(microCategory.getUpdatedAt())
            .build();
    }
    
    private String generateSlug(String name) {
        if (name == null) return null;
        return name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .replaceAll("^-|-$", "");
    }
    
    private List<ProductStatsDto> getRecentProducts() {
        // Get recent products for dashboard
        Page<Product> recentProducts = productRepository.findRecentProducts(
            Pageable.ofSize(5)); // Get top 5 recent products
            
        return recentProducts.getContent().stream()
            .map(product -> ProductStatsDto.builder()
                .productId(product.getId())
                .productName(product.getName())
                .categoryName(product.getMicroCategory() != null && 
                    product.getMicroCategory().getSubCategory() != null &&
                    product.getMicroCategory().getSubCategory().getCategory() != null ?
                    product.getMicroCategory().getSubCategory().getCategory().getName() : "N/A")
                .subCategoryName(product.getMicroCategory() != null && 
                    product.getMicroCategory().getSubCategory() != null ?
                    product.getMicroCategory().getSubCategory().getName() : "N/A")
                .microCategoryName(product.getMicroCategory() != null ?
                    product.getMicroCategory().getName() : "N/A")
                .price(product.getPrice() != null ? 
                    BigDecimal.valueOf(product.getPrice()) : BigDecimal.ZERO)
                .isActive(product.isActive())
                .inStock(product.isInStock())
                .createdAt(product.getCreatedAt())
                .vendorName(product.getVendor() != null ? product.getVendor().getBusinessName() : "N/A")
                .build())
            .collect(Collectors.toList());
    }
}

