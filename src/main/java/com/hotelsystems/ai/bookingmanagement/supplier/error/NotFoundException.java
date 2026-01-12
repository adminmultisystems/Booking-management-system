package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when a resource is not found.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }

    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}

