package com.roomiematcher.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BaseException extends RuntimeException {
    
    private final HttpStatus status;
    private final String errorCode;
    
    public BaseException(String message) {
        this(HttpStatus.INTERNAL_SERVER_ERROR, message);
    }
    
    public BaseException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }
    
    public BaseException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
} 