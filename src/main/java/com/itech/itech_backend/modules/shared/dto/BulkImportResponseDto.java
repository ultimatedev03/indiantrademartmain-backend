package com.itech.itech_backend.modules.shared.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkImportResponseDto {
    private boolean success;
    private String message;
    private int totalRows;
    private int successfulRows;
    private int failedRows;
    private List<String> errors;
    private List<String> warnings;
}

