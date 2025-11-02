package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardAnalyticsDto {
    private long totalCategories;
    private long totalSubCategories;
    private long totalMicroCategories;
    private long totalProducts;
    private long activeProducts;
    private long inactiveProducts;
    private long productsInStock;
    private long productsOutOfStock;
    private long totalVendors;
    private LocalDateTime lastUpdated;
    private List<CategoryStatsDto> topCategories;
    private List<ProductStatsDto> recentProducts;
}

