package com.itech.itech_backend.exception;

public class InvalidOperationException extends RuntimeException {
    
    public InvalidOperationException(String message) {
        super(message);
    }
    
    public InvalidOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
