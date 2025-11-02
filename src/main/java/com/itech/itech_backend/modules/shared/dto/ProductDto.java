package com.itech.itech_backend.modules.shared.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductDto {
    private String name;
    private String description;
    private Double price;
    private Double originalPrice;
    private int stock;
    private Long categoryId;
    private Long vendorId;
    private Long subCategoryId;
    private Long microCategoryId;
    
    // Additional fields for Excel import and extended functionality
    private String brand;
    private String model;
    private String sku;
    private Integer minOrderQuantity;
    private String unit;
    private String specifications;
    private String tags;
    private Double gstRate;
    private BigDecimal weight;
    private BigDecimal length;
    private BigDecimal width;
    private BigDecimal height;
    private Boolean freeShipping;
    private BigDecimal shippingCharge;
    private Boolean isActive;
}

