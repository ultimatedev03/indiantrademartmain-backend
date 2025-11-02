package com.itech.marketplace.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

// Bulk Invoice Generation DTO
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkInvoiceGenerationDto {
    @NotNull
    private List<Long> vendorIds;
    
    @NotBlank
    private String invoiceType;
    
    private LocalDateTime dueDate;
    private String description;
    private Map<String, Object> commonMetadata;
}

