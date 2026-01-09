package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when a bad request is made, typically due to client-side errors.
 */
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }

    public BadRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}

