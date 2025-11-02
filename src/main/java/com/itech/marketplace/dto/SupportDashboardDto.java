package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Support Dashboard DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupportDashboardDto {
    private Integer totalTickets;
    private Integer openTickets;
    private Integer inProgressTickets;
    private Integer resolvedTickets;
    private Integer escalatedTickets;
    private Double averageResponseTime;
    private Double averageResolutionTime;
    private Double complianceRate;
}

