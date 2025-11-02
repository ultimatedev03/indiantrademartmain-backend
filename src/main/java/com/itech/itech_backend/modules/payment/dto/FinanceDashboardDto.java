package com.itech.itech_backend.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinanceDashboardDto {
    
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal dailyRevenue;
    
    private Long totalInvoices;
    private Long paidInvoices;
    private Long pendingInvoices;
    
    private BigDecimal totalTax;
    private BigDecimal monthlyTax;
    
    private Map<String, BigDecimal> revenueByType;
    private Map<String, Long> invoicesByStatus;
    
    private Double revenueGrowthRate;
    private Double invoiceGrowthRate;
}

