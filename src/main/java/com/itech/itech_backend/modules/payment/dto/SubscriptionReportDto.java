package com.itech.itech_backend.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionReportDto {
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private Long totalSubscriptions;
    private Long activeSubscriptions;
    private Long cancelledSubscriptions;
    private Long expiredSubscriptions;
    
    private BigDecimal totalRevenue;
    private BigDecimal recurringRevenue;
    
    private Map<String, Long> subscriptionsByPlan;
    private Map<String, BigDecimal> revenueByPlan;
    private Map<String, Long> subscriptionsByPeriod;
}

