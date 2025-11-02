package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDto {
    private String query;
    private Long categoryId;
    private Long subCategoryId;
    private Long microCategoryId;
    private String brand;
    private String city;
    private String state;
    private Double minPrice;
    private Double maxPrice;
    private Boolean inStock;
    private Boolean freeShipping;
    private String vendorType;
    private Double minRating;
    private String sortBy;
    private String sortDirection;
    
    // Search pagination and sorting
    private int page;
    private int size;
    private String sortDir;
    
    // Advanced filters
    private String location;
    private Long vendorId;
    private Boolean isActive;
    private Boolean isApproved;
    private Boolean isFeatured;
    
    // Legacy support
    private Long subcategoryId;
    private Long microcategoryId;
    
    // Advanced attribute filters
    private Map<String, String> attributeFilters; // attribute name -> value
    private Map<String, String> attributeMinValues; // for numeric ranges
    private Map<String, String> attributeMaxValues; // for numeric ranges
    
    // Product variant filters
    private String color;
    private String material;
    private String style;
    private String weight;
    private String dimensions;
    
    // Advanced search options
    private boolean includeVariants;
    private boolean searchInAttributes;
    private boolean fuzzySearch;
    private String searchMode; // EXACT, CONTAINS, FUZZY
}

