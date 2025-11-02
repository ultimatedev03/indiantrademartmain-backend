package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Auto Response Suggestion DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoResponseSuggestionDto {
    private String suggestion;
    private Double confidenceScore;
}

