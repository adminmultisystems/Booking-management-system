package com.hotelsystems.ai.bookingmanagement.ownerinventory.exception;

/**
 * Exception thrown when there is a conflict in inventory operations,
 * such as insufficient inventory for a reservation request.
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String message, Throwable cause) {
        super(message, cause);
    }
}

