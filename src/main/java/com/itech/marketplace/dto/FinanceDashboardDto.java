package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Finance Dashboard DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinanceDashboardDto {
    // Current period metrics
    private BigDecimal totalRevenue;
    private BigDecimal monthlyRevenue;
    private BigDecimal dailyRevenue;
    
    private Integer totalTransactions;
    private Integer monthlyTransactions;
    private Integer dailyTransactions;
    
    private BigDecimal totalRefunds;
    private BigDecimal monthlyRefunds;
    
    private Integer activeSubscriptions;
    private Integer pendingInvoices;
    private Integer overdueInvoices;
    
    // Growth metrics
    private BigDecimal revenueGrowth;
    private BigDecimal transactionGrowth;
    private BigDecimal subscriptionGrowth;
    
    // Charts data
    private List<RevenueChartData> revenueChart;
    private List<TransactionChartData> transactionChart;
    private Map<String, BigDecimal> revenueDistribution;
    
    // Recent activities
    private List<RecentTransaction> recentTransactions;
    private List<PendingRefund> pendingRefunds;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RevenueChartData {
        private String date;
        private BigDecimal revenue;
        private BigDecimal tax;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TransactionChartData {
        private String date;
        private Integer successful;
        private Integer failed;
        private Integer pending;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentTransaction {
        private Long id;
        private String type;
        private BigDecimal amount;
        private String status;
        private LocalDateTime createdAt;
        private String vendorName;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingRefund {
        private Long id;
        private BigDecimal amount;
        private String reason;
        private LocalDateTime requestedAt;
        private String customerName;
    }
}

