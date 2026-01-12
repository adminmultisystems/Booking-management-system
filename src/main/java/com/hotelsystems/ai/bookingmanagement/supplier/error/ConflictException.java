package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when a conflict occurs, typically due to supplier unavailability.
 */
public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

