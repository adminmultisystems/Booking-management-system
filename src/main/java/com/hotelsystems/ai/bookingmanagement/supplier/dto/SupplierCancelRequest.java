package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Request DTO for supplier cancellation operation.
 * Minimal DTO - may evolve based on supplier specifications.
 */
public class SupplierCancelRequest {

    @JsonProperty("bookingRef")
    private String bookingRef;

    // Note: Additional fields can be added as supplier specifications evolve

    public SupplierCancelRequest() {
    }

    public SupplierCancelRequest(String bookingRef) {
        this.bookingRef = bookingRef;
    }

    public String getBookingRef() {
        return bookingRef;
    }

    public void setBookingRef(String bookingRef) {
        this.bookingRef = bookingRef;
    }
}

