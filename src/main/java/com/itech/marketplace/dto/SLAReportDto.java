package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

// SLA Report DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAReportDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Double averageResponseTime;
    private Double averageResolutionTime;
    private Double responseCompliance;
    private Double resolutionCompliance;
    private Integer totalTickets;
    private Integer overdueTickets;
}

