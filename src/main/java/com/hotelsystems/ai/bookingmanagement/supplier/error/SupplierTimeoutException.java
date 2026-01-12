package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when supplier API times out or returns 5xx errors.
 */
public class SupplierTimeoutException extends SupplierBookingException {
    public SupplierTimeoutException(String message, String supplierId) {
        super(message, supplierId, "TIMEOUT_ERROR");
    }

    public SupplierTimeoutException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "TIMEOUT_ERROR");
    }
}

