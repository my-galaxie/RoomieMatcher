package com.roomiematcher.common.exception;

import org.springframework.http.HttpStatus;

public class BaseException extends RuntimeException {
    private final HttpStatus status;
    private final String errorCode;
    
    public BaseException(String message) {
        this(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    public BaseException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.errorCode = null;
    }
    
    public BaseException(String message, Throwable cause, HttpStatus status) {
        super(message, cause);
        this.status = status;
        this.errorCode = null;
    }
    
    public BaseException(HttpStatus status, String errorCode, String message) {
        super(message);
        this.status = status;
        this.errorCode = errorCode;
    }
    
    public HttpStatus getStatus() {
        return status;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
} 