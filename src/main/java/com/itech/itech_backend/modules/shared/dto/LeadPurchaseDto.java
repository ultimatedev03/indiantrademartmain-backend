package com.itech.itech_backend.modules.shared.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeadPurchaseDto {
    private Long leadId;
    private Long vendorId;
    private Double price;
}

