package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Response DTO for supplier recheck operation.
 * Minimal DTO - may evolve based on supplier specifications.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierRecheckResponse {

    @JsonProperty("status")
    private RecheckStatus status;

    @JsonProperty("newTotalPriceMinor")
    private Long newTotalPriceMinor;

    // TODO: Add additional fields as supplier specifications evolve

    public SupplierRecheckResponse() {
    }

    public SupplierRecheckResponse(RecheckStatus status, Long newTotalPriceMinor) {
        this.status = status;
        this.newTotalPriceMinor = newTotalPriceMinor;
    }

    public RecheckStatus getStatus() {
        return status;
    }

    public void setStatus(RecheckStatus status) {
        this.status = status;
    }

    public Long getNewTotalPriceMinor() {
        return newTotalPriceMinor;
    }

    public void setNewTotalPriceMinor(Long newTotalPriceMinor) {
        this.newTotalPriceMinor = newTotalPriceMinor;
    }

    /**
     * Enum representing recheck status values.
     */
    public enum RecheckStatus {
        OK,
        SOLD_OUT,
        PRICE_CHANGED
    }
}

