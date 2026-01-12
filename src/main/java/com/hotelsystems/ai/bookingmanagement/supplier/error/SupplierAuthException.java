package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when supplier API authentication fails (401/403).
 */
public class SupplierAuthException extends SupplierBookingException {
    public SupplierAuthException(String message, String supplierId) {
        super(message, supplierId, "AUTH_ERROR");
    }

    public SupplierAuthException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "AUTH_ERROR");
    }
}

