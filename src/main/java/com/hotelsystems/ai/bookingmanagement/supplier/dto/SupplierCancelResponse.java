package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for supplier cancellation operation.
 * Minimal DTO - may evolve based on supplier specifications.
 */
public class SupplierCancelResponse {

    @JsonProperty("status")
    private CancelStatus status;

    // Note: Additional fields can be added as supplier specifications evolve

    public SupplierCancelResponse() {
    }

    public SupplierCancelResponse(CancelStatus status) {
        this.status = status;
    }

    public CancelStatus getStatus() {
        return status;
    }

    public void setStatus(CancelStatus status) {
        this.status = status;
    }

    /**
     * Enum representing cancellation status values.
     */
    public enum CancelStatus {
        CANCELLED,
        FAILED,
        NOT_FOUND
    }
}

