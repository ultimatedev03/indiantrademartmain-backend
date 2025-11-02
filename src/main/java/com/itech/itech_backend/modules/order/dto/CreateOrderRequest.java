package com.itech.itech_backend.modules.order.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateOrderRequest {
    private Long buyerId;
    private Long companyId;
    private String referenceNumber;
    private String poNumber;
    private List<OrderItemRequest> items;
    private AddressRequest shippingAddress;
    private AddressRequest billingAddress;
    private String specialInstructions;
    private String customerNotes;
    private String currency;
    private String paymentMethod;
    private String paymentTerms;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class OrderItemRequest {
        private Long productId;
        private Long vendorId;
        private Integer quantity;
        private BigDecimal unitPrice;
        private String specialInstructions;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AddressRequest {
        private String addressLine1;
        private String addressLine2;
        private String city;
        private String state;
        private String postalCode;
        private String country;
        private String contactName;
        private String contactPhone;
    }
}
