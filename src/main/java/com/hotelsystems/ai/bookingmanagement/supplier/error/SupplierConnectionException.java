package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when there's a connection issue with a supplier API.
 */
public class SupplierConnectionException extends SupplierBookingException {
    public SupplierConnectionException(String message, String supplierId) {
        super(message, supplierId, "CONNECTION_ERROR");
    }

    public SupplierConnectionException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "CONNECTION_ERROR");
    }
}

