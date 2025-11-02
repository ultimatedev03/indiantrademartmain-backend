package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryStatsDto {
    private Long categoryId;
    private String categoryName;
    private long subCategoryCount;
    private long microCategoryCount;
    private long productCount;
    private long activeProductCount;
    private double percentageOfTotal;
}

