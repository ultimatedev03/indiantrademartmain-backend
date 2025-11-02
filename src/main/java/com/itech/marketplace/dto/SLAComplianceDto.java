package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

// SLA Compliance DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SLAComplianceDto {
    private Double overallCompliance;
    private List<SLAComplianceDetail> details;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SLAComplianceDetail {
        private String category;
        private String priority;
        private Double responseCompliance;
        private Double resolutionCompliance;
    }
}

