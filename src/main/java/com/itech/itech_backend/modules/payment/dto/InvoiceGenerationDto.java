package com.itech.itech_backend.modules.payment.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceGenerationDto {
    
    @NotNull
    private Long vendorId;
    
    @NotNull
    private BigDecimal subtotal;
    
    private String description;
    private String billingAddress;
    private String shippingAddress;
    private List<InvoiceItemDto> items;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InvoiceItemDto {
        private String description;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal amount;
    }
}

