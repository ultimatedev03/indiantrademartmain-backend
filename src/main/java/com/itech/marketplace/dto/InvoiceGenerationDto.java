package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

// Invoice Generation DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceGenerationDto {
    @NotNull
    private Long vendorId;
    
    @NotNull
    private Long subscriptionId;
    
    @NotBlank
    private String invoiceType; // SUBSCRIPTION, SERVICE_FEE, PENALTY
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    @NotNull
    @DecimalMin("0.00")
    private BigDecimal taxAmount;
    
    private String description;
    private LocalDateTime dueDate;
    private Map<String, Object> metadata;
}

