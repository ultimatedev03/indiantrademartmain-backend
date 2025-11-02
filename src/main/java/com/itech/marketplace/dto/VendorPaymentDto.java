package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.util.Map;

// Vendor Payment DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VendorPaymentDto {
    @NotNull
    private Long vendorId;
    
    @NotNull
    @DecimalMin("0.01")
    private BigDecimal amount;
    
    @NotBlank
    private String paymentMethod; // BANK_TRANSFER, UPI, WALLET
    
    @NotBlank
    private String description;
    
    private String bankAccountId;
    private String upiId;
    private Map<String, String> paymentDetails;
}

