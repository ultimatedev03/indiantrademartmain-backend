package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// Knowledge Base Article DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBaseArticleDto {
    private Long id;
    private String title;
    private String content;
    private String category;
    private List<String> tags;
    private Boolean isPublished;
    private Long authorId;
    private String metaDescription;
    private Boolean isFeatured;
}

