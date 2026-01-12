package com.hotelsystems.ai.bookingmanagement.supplier.dto;

/**
 * Base DTO for supplier-specific booking requests.
 * This can be extended by supplier-specific implementations.
 * 
 * NOTE: This class is for future use with real supplier integration.
 * In Phase-1, booking operations use SupplierBookRequest/SupplierBookResponse.
 */
public class SupplierBookingRequest {
    // NOTE: originalRequest field removed as BookingRequest is not available in Phase-1
    // This will be restored when TL-owned orchestration logic is integrated
    private Object originalRequest;
    private String supplierId;

    public SupplierBookingRequest() {
    }

    public SupplierBookingRequest(Object originalRequest, String supplierId) {
        this.originalRequest = originalRequest;
        this.supplierId = supplierId;
    }

    public Object getOriginalRequest() {
        return originalRequest;
    }

    public void setOriginalRequest(Object originalRequest) {
        this.originalRequest = originalRequest;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}

