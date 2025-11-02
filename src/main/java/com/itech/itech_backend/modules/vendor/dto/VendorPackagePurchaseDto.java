package com.itech.itech_backend.modules.vendor.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VendorPackagePurchaseDto {
    
    @NotNull(message = "Plan ID is required")
    private Long planId;
    
    @NotBlank(message = "Payment method is required")
    private String paymentMethod; // RAZORPAY, BANK_TRANSFER, UPI, etc.
    
    private String transactionId;
    
    private String couponCode;
    
    private BigDecimal discountAmount;
    
    private String billingAddress;
    
    private String billingCity;
    
    private String billingState;
    
    private String billingPincode;
    
    private String gstNumber;
    
    @Builder.Default
    private boolean generateInvoice = true;
    
    // For installment payments
    private Integer installmentCount;
    
    private BigDecimal installmentAmount;
    
    private String notes;
}
