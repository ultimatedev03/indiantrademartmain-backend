package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryHierarchyDto {
    private Long id;
    private String name;
    private String description;
    private boolean isActive;
    private int displayOrder;
    private int productCount;
    private List<SubCategoryHierarchyDto> subCategories;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubCategoryHierarchyDto {
        private Long id;
        private String name;
        private String description;
        private boolean isActive;
        private int displayOrder;
        private int productCount;
        private List<MicroCategoryHierarchyDto> microCategories;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MicroCategoryHierarchyDto {
        private Long id;
        private String name;
        private String description;
        private boolean isActive;
        private int displayOrder;
        private int productCount;
    }
}

