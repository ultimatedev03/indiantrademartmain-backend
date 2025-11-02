package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Subscription Report DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionReportDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Integer totalActiveSubscriptions;
    private Integer newSubscriptions;
    private Integer cancelledSubscriptions;
    private Integer expiredSubscriptions;
    
    private BigDecimal totalSubscriptionRevenue;
    private BigDecimal averageSubscriptionValue;
    
    private Map<String, Integer> subscriptionsByPlan;
    private Map<String, BigDecimal> revenueByPlan;
    private List<SubscriptionTrend> trends;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SubscriptionTrend {
        private String period;
        private Integer active;
        private Integer newSubs;
        private Integer cancelled;
        private BigDecimal revenue;
    }
}

