package com.itech.itech_backend.exception;

import lombok.Builder;
import lombok.Data;
import java.util.Map;

@Data
@Builder
public class ValidationErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private Map<String, String> errors;
}
