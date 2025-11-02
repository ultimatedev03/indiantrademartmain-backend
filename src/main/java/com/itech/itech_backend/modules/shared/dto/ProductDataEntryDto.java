package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDataEntryDto {
    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private BigDecimal price;
    private BigDecimal originalPrice;
    private String sku;
    private int stockQuantity;
    private boolean inStock;
    private boolean isActive;
    private String brand;
    private String model;
    private String specification;
    private String metaTitle;
    private String metaDescription;
    private String slug;
    private int displayOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Category Information
    private Long categoryId;
    private String categoryName;
    private Long subCategoryId;
    private String subCategoryName;
    private Long microCategoryId;
    private String microCategoryName;
    
    // Vendor Information
    private Long vendorId;
    private String vendorName;
    private String vendorCompanyName;
    
    // Product Images
    private List<String> imageUrls;
    private String primaryImageUrl;
}

