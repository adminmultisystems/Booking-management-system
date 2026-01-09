package com.hotelsystems.ai.bookingmanagement.supplier.error;

/**
 * Base exception for supplier booking operations.
 */
public class SupplierBookingException extends RuntimeException {
    private final String supplierId;
    private final String errorCode;

    public SupplierBookingException(String message, String supplierId, String errorCode) {
        super(message);
        this.supplierId = supplierId;
        this.errorCode = errorCode;
    }

    public SupplierBookingException(String message, Throwable cause, String supplierId, String errorCode) {
        super(message, cause);
        this.supplierId = supplierId;
        this.errorCode = errorCode;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}

