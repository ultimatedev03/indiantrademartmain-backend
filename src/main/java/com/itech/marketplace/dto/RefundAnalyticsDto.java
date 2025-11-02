package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Refund Analytics DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundAnalyticsDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private BigDecimal totalRefunds;
    private BigDecimal processedRefunds;
    private BigDecimal pendingRefunds;
    private BigDecimal rejectedRefunds;
    
    private Integer totalRefundRequests;
    private Integer processedRequests;
    private Integer pendingRequests;
    private Integer rejectedRequests;
    
    private BigDecimal refundRate;
    private BigDecimal averageRefundAmount;
    private Double averageProcessingTime;
    
    private Map<String, BigDecimal> refundsByReason;
    private Map<String, Integer> refundCountsByReason;
    private List<RefundTrend> trends;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefundTrend {
        private String period;
        private BigDecimal amount;
        private Integer count;
        private BigDecimal rate;
    }
}

