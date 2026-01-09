package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when supplier rate has changed (409).
 */
public class SupplierRateChangedException extends SupplierBookingException {
    public SupplierRateChangedException(String message, String supplierId) {
        super(message, supplierId, "RATE_CHANGED");
    }

    public SupplierRateChangedException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "RATE_CHANGED");
    }
}

