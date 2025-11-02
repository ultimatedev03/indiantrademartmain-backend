package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

// Refund Request DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequestDto {
    @NotNull
    private Long orderId;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    @NotBlank
    private String reason;
    
    @NotBlank
    private String refundType; // FULL, PARTIAL
    
    private String notes;
    @Builder.Default
    private Boolean autoProcess = false;
}

