package com.itech.itech_backend.modules.payment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkInvoiceGenerationDto {
    
    private List<InvoiceGenerationDto> invoices;
    private String batchDescription;
}

