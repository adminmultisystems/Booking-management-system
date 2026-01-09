package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when supplier API validation fails.
 */
public class SupplierValidationException extends SupplierBookingException {
    public SupplierValidationException(String message, String supplierId) {
        super(message, supplierId, "VALIDATION_ERROR");
    }

    public SupplierValidationException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "VALIDATION_ERROR");
    }
}

