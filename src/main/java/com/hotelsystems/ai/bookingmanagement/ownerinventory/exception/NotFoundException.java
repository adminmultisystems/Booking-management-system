package com.hotelsystems.ai.bookingmanagement.ownerinventory.exception;

/**
 * Exception thrown when a requested resource is not found.
 * Maps to HTTP 404 Not Found.
 */
public class NotFoundException extends RuntimeException {
    
    public NotFoundException(String message) {
        super(message);
    }
    
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

