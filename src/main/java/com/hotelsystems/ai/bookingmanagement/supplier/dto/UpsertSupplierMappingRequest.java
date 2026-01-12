package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import jakarta.validation.constraints.NotNull;

/**
 * Request DTO for upserting a supplier mapping.
 */
public class UpsertSupplierMappingRequest {

    @NotNull(message = "supplierCode is required")
    private SupplierCode supplierCode;

    private String supplierHotelId;

    @NotNull(message = "status is required")
    private SupplierMappingStatus status;

    public UpsertSupplierMappingRequest() {
    }

    public UpsertSupplierMappingRequest(SupplierCode supplierCode, String supplierHotelId, SupplierMappingStatus status) {
        this.supplierCode = supplierCode;
        this.supplierHotelId = supplierHotelId;
        this.status = status;
    }

    public SupplierCode getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(SupplierCode supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierHotelId() {
        return supplierHotelId;
    }

    public void setSupplierHotelId(String supplierHotelId) {
        this.supplierHotelId = supplierHotelId;
    }

    public SupplierMappingStatus getStatus() {
        return status;
    }

    public void setStatus(SupplierMappingStatus status) {
        this.status = status;
    }
}

