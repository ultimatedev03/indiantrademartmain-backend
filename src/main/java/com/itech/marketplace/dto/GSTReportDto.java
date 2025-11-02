package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// GST Report DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GSTReportDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private BigDecimal totalTaxableAmount;
    private BigDecimal totalCGST;
    private BigDecimal totalSGST;
    private BigDecimal totalIGST;
    private BigDecimal totalTax;
    
    private List<GSTEntry> entries;
    private Map<String, GSTSummary> taxRateSummary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GSTEntry {
        private String invoiceNumber;
        private LocalDateTime invoiceDate;
        private String vendorGST;
        private String vendorName;
        private BigDecimal taxableAmount;
        private BigDecimal cgst;
        private BigDecimal sgst;
        private BigDecimal igst;
        private BigDecimal totalTax;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GSTSummary {
        private BigDecimal taxableAmount;
        private BigDecimal taxAmount;
        private Integer invoiceCount;
    }
}

