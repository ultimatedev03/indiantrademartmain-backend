package com.itech.itech_backend.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GSTReportDto {
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private BigDecimal totalTaxableAmount;
    private BigDecimal totalCGST;
    private BigDecimal totalSGST;
    private BigDecimal totalIGST;
    private BigDecimal totalGST;
    
    private List<GstInvoiceSummary> invoices;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class GstInvoiceSummary {
        private String invoiceNumber;
        private LocalDateTime invoiceDate;
        private String vendorName;
        private String vendorGstNumber;
        private BigDecimal taxableAmount;
        private BigDecimal cgst;
        private BigDecimal sgst;
        private BigDecimal igst;
        private BigDecimal totalAmount;
    }
}

