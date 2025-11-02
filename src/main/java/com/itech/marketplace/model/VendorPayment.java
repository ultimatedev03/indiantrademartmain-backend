package com.itech.marketplace.model;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

// VendorPayment Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPayment {
    private Long id;
    private Long vendorId;
    private BigDecimal amount;
    private String paymentMethod;
    private String status;
    private String description;
    private String paymentReference;
    private LocalDateTime processedAt;
    private LocalDateTime createdAt;
    private Map<String, String> paymentDetails;
}

