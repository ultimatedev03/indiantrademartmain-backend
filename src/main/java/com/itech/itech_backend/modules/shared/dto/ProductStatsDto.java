package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductStatsDto {
    private Long productId;
    private String productName;
    private String categoryName;
    private String subCategoryName;
    private String microCategoryName;
    private BigDecimal price;
    private boolean isActive;
    private boolean inStock;
    private LocalDateTime createdAt;
    private String vendorName;
}

