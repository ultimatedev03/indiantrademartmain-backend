package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// SLA Configuration DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAConfigurationDto {
    private Long id;
    private String category;
    private String priority;
    private Integer responseTimeMinutes;
    private Integer resolutionTimeMinutes;
    private Integer escalationTimeMinutes;
}

