package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Category Analysis DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryAnalysisDto {
    private String category;
    private Integer totalTickets;
    private Integer resolvedTickets;
    private Double resolutionRate;
    private Double averageResponseTime;
    private Double averageResolutionTime;
}

