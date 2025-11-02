package com.itech.itech_backend.modules.shared.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateQuoteDto {
    private Long vendorId;
    private Long inquiryId;
    private String response;
    private BigDecimal price;
    private String currency;
    private Integer quantity;
    private String deliveryTime;
    private String paymentTerms;
    private String validityPeriod;
    private String additionalNotes;
}

