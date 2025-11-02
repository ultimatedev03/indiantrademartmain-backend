package com.itech.itech_backend.modules.shared.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductDto {
    
    @NotBlank(message = "Product name is required")
    @Size(max = 255, message = "Product name must not exceed 255 characters")
    private String name;
    
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;
    
    @Size(max = 500, message = "Short description must not exceed 500 characters")
    private String shortDescription;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @DecimalMin(value = "0.0", inclusive = false, message = "Original price must be greater than 0")
    private BigDecimal originalPrice;
    
    @Size(max = 100, message = "SKU must not exceed 100 characters")
    private String sku;
    
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private int stockQuantity;
    
    @Builder.Default
    private boolean inStock = true;
    
    @Builder.Default
    private boolean isActive = true;
    
    @Size(max = 100, message = "Brand must not exceed 100 characters")
    private String brand;
    
    @Size(max = 100, message = "Model must not exceed 100 characters")
    private String model;
    
    @Size(max = 1000, message = "Specification must not exceed 1000 characters")
    private String specification;
    
    @Size(max = 255, message = "Meta title must not exceed 255 characters")
    private String metaTitle;
    
    @Size(max = 500, message = "Meta description must not exceed 500 characters")
    private String metaDescription;
    
    @Size(max = 255, message = "Slug must not exceed 255 characters")
    private String slug;
    
    private int displayOrder;
    
    @NotNull(message = "Category ID is required")
    private Long categoryId;
    
    @NotNull(message = "SubCategory ID is required")
    private Long subCategoryId;
    
    @NotNull(message = "MicroCategory ID is required")
    private Long microCategoryId;
    
    private Long vendorId;
    
    private List<String> imageUrls;
}

