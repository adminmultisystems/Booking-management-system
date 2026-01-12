package com.hotelsystems.ai.bookingmanagement.supplier.dto;

/**
 * Base DTO for supplier-specific booking responses.
 * This can be extended by supplier-specific implementations.
 * 
 * NOTE: This class is for future use with real supplier integration.
 * In Phase-1, booking operations use SupplierBookRequest/SupplierBookResponse.
 */
public class SupplierBookingResponse {
    // NOTE: bookingResponse field removed as BookingResponse is not available in Phase-1
    // This will be restored when TL-owned orchestration logic is integrated
    private Object bookingResponse;
    private String supplierId;
    private Object rawSupplierResponse;

    public SupplierBookingResponse() {
    }

    public SupplierBookingResponse(Object bookingResponse, String supplierId, Object rawSupplierResponse) {
        this.bookingResponse = bookingResponse;
        this.supplierId = supplierId;
        this.rawSupplierResponse = rawSupplierResponse;
    }

    public Object getBookingResponse() {
        return bookingResponse;
    }

    public void setBookingResponse(Object bookingResponse) {
        this.bookingResponse = bookingResponse;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public Object getRawSupplierResponse() {
        return rawSupplierResponse;
    }

    public void setRawSupplierResponse(Object rawSupplierResponse) {
        this.rawSupplierResponse = rawSupplierResponse;
    }
}

