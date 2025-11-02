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
public class SearchSuggestionsDto {
    private List<String> categories;
    private List<String> subCategories;
    private List<String> microCategories;
    private List<String> products;
    private List<String> brands;
}

