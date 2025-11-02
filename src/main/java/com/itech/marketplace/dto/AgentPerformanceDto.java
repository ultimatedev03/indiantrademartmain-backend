package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

// Agent Performance DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentPerformanceDto {
    private Long agentId;
    private String agentName;
    private Integer ticketsHandled;
    private Double averageResponseTime;
    private Double averageResolutionTime;
    private Double complianceRate;
}

