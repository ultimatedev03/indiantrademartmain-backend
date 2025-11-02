package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Payment Summary DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentSummaryDto {
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    
    private BigDecimal totalAmount;  // Total amount field
    private BigDecimal totalPayments;
    private BigDecimal successfulPayments;
    private BigDecimal failedPayments;
    private BigDecimal pendingPayments;
    
    private Integer totalTransactions;
    private Integer successfulTransactions;
    private Integer failedTransactions;
    private Integer pendingTransactions;
    
    private BigDecimal successRate;
    private BigDecimal averageTransactionValue;
    
    private Map<String, BigDecimal> paymentsByMethod;
    private Map<String, Integer> transactionsByStatus;
    private List<DailyPaymentSummary> dailySummary;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyPaymentSummary {
        private String date;
        private BigDecimal amount;
        private Integer transactions;
        private BigDecimal successRate;
    }
}

