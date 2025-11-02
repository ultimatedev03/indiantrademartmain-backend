package com.itech.itech_backend.modules.shared.dto;

import com.itech.itech_backend.modules.buyer.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchResponseDto {
    private List<Product> products;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean hasPrevious;
    
    // Available filters based on current search
    private Map<String, Object> availableFilters;
    
    // Search statistics
    private SearchStats searchStats;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SearchStats {
        private long totalProductCount;
        private long activeProductCount;
        private long vendorCount;
        private PriceRange priceRange;
        private List<String> availableBrands;
        private List<String> availableLocations;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceRange {
        private Double minPrice;
        private Double maxPrice;
        private Double avgPrice;
    }
}

