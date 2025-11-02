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
public class MegaMenuDataDto {
    private String id;
    private String title;
    private String icon;
    private List<MegaMenuSubCategoryDto> subcategories;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MegaMenuSubCategoryDto {
        private String id;
        private String title;
        private List<MegaMenuItemDto> microCategories;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MegaMenuItemDto {
        private String id;
        private String name;
        private String href;
    }
}

