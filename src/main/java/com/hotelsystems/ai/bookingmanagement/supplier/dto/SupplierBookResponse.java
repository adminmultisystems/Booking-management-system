package com.hotelsystems.ai.bookingmanagement.supplier.dto;

/**
 * DTO representing the response from a booking creation.
 */
public class SupplierBookResponse {
    public enum BookingStatus {
        CONFIRMED,
        FAILED
    }

    private String supplierBookingRef;
    private BookingStatus status;
    private String rawPayloadJson;

    public SupplierBookResponse() {
    }

    public SupplierBookResponse(String supplierBookingRef, BookingStatus status, String rawPayloadJson) {
        this.supplierBookingRef = supplierBookingRef;
        this.status = status;
        this.rawPayloadJson = rawPayloadJson;
    }

    public String getSupplierBookingRef() {
        return supplierBookingRef;
    }

    public void setSupplierBookingRef(String supplierBookingRef) {
        this.supplierBookingRef = supplierBookingRef;
    }

    /**
     * Backward compatibility method for existing code.
     * @return the supplier booking reference
     */
    public String getBookingRef() {
        return supplierBookingRef;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public String getRawPayloadJson() {
        return rawPayloadJson;
    }

    public void setRawPayloadJson(String rawPayloadJson) {
        this.rawPayloadJson = rawPayloadJson;
    }
}
