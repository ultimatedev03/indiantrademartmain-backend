package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Suggest Response DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SuggestResponseDto {
    private Long ticketId;
    private String query;
    private Integer suggestionsLimit;
}

