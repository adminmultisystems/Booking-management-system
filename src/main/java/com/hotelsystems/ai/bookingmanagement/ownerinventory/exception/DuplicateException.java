package com.hotelsystems.ai.bookingmanagement.ownerinventory.exception;

/**
 * Exception thrown when attempting to create a duplicate resource.
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateException extends RuntimeException {
    
    public DuplicateException(String message) {
        super(message);
    }
    
    public DuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}

