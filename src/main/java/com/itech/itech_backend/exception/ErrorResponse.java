package com.itech.itech_backend.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private int status;
    private String message;
    private long timestamp;
    private String path;
    private String errorCode;
    private String requestId;
    private Object additionalData;
}
