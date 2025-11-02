package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MicroCategoryDto {
    private Long id;
    private String name;
    private String description;
    private int displayOrder;
    private boolean isActive;
    private String metaTitle;
    private String metaDescription;
    private String slug;
    private Long subCategoryId;
    private String subCategoryName;
    private Long categoryId;
    private String categoryName;
    private int productCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

