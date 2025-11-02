package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Financial Report DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialReportDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String groupBy;
    
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private BigDecimal netRevenue;
    
    private List<RevenueDataPoint> revenueData;
    private Map<String, BigDecimal> revenueByCategory;
    private Map<String, Integer> transactionCounts;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueDataPoint {
        private String period;
        private BigDecimal amount;
        private BigDecimal tax;
        private Integer transactionCount;
    }
}

