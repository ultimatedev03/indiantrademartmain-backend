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
public class FinancialReportDto {
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String groupBy;
    
    private BigDecimal totalRevenue;
    private BigDecimal totalTax;
    private Long totalInvoices;
    
    private Map<String, BigDecimal> revenueByPeriod;
    private Map<String, Long> invoicesByPeriod;
    private Map<String, BigDecimal> taxByPeriod;
    
    private Map<String, BigDecimal> revenueByType;
    private Map<String, BigDecimal> revenueByVendor;
}

