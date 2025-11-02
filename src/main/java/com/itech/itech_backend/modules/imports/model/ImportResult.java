package com.itech.itech_backend.modules.imports.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResult {
    
    private String importId;
    private String fileName;
    private ImportType importType;
    private ImportStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    
    // Statistics
    private int totalRows;
    private int successfulRows;
    private int failedRows;
    private int skippedRows;
    
    // Results
    private List<ImportError> errors;
    private List<ImportWarning> warnings;
    private Map<String, Object> statistics;
    
    // Progress tracking
    private int processedRows;
    private double progressPercentage;
    
    // User info
    private Long importedBy;
    private String importedByName;
    
    // File info
    private long fileSize;
    private String originalFileName;
    private String contentType;
    
    public enum ImportType {
        CATEGORIES,
        CITIES,
        PRODUCTS,
        USERS
    }
    
    public enum ImportStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        FAILED,
        CANCELLED
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportError {
        private int rowNumber;
        private String column;
        private String value;
        private String errorMessage;
        private ErrorType errorType;
        
        public enum ErrorType {
            VALIDATION_ERROR,
            DUPLICATE_ERROR,
            REFERENCE_ERROR,
            DATA_TYPE_ERROR,
            REQUIRED_FIELD_ERROR
        }
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImportWarning {
        private int rowNumber;
        private String column;
        private String value;
        private String warningMessage;
        private WarningType warningType;
        
        public enum WarningType {
            DATA_TRUNCATED,
            DEFAULT_VALUE_USED,
            MISSING_OPTIONAL_FIELD,
            FORMAT_ADJUSTED
        }
    }
    
    // Utility methods
    public double calculateSuccessRate() {
        if (totalRows == 0) return 0.0;
        return (double) successfulRows / totalRows * 100.0;
    }
    
    public boolean isCompleted() {
        return status == ImportStatus.COMPLETED;
    }
    
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }
    
    public boolean hasWarnings() {
        return warnings != null && !warnings.isEmpty();
    }
    
    public long getDurationInMillis() {
        if (startTime == null || endTime == null) return 0;
        return java.time.Duration.between(startTime, endTime).toMillis();
    }
    
    public String getFormattedDuration() {
        long millis = getDurationInMillis();
        if (millis < 1000) {
            return millis + "ms";
        } else if (millis < 60000) {
            return String.format("%.1fs", millis / 1000.0);
        } else {
            long minutes = millis / 60000;
            long seconds = (millis % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    public void addError(int rowNumber, String column, String value, String errorMessage, ImportError.ErrorType errorType) {
        if (this.errors == null) {
            this.errors = new java.util.ArrayList<>();
        }
        this.errors.add(ImportError.builder()
                .rowNumber(rowNumber)
                .column(column)
                .value(value)
                .errorMessage(errorMessage)
                .errorType(errorType)
                .build());
    }
    
    public void addWarning(int rowNumber, String column, String value, String warningMessage, ImportWarning.WarningType warningType) {
        if (this.warnings == null) {
            this.warnings = new java.util.ArrayList<>();
        }
        this.warnings.add(ImportWarning.builder()
                .rowNumber(rowNumber)
                .column(column)
                .value(value)
                .warningMessage(warningMessage)
                .warningType(warningType)
                .build());
    }
    
    public void updateProgress(int processedRows, int totalRows) {
        this.processedRows = processedRows;
        this.totalRows = totalRows;
        this.progressPercentage = totalRows > 0 ? (double) processedRows / totalRows * 100.0 : 0.0;
    }
}
