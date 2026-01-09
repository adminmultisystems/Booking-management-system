package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Exception thrown when supplier rate is sold out (409).
 */
public class SupplierSoldOutException extends SupplierBookingException {
    public SupplierSoldOutException(String message, String supplierId) {
        super(message, supplierId, "SOLD_OUT");
    }

    public SupplierSoldOutException(String message, Throwable cause, String supplierId) {
        super(message, cause, supplierId, "SOLD_OUT");
    }
}

