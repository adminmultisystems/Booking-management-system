package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import java.time.Instant;

/**
 * Response DTO for supplier mapping.
 */
public class SupplierMappingResponse {

    private String hotelId;
    private SupplierCode supplierCode;
    private String supplierHotelId;
    private SupplierMappingStatus status;
    private Instant createdAt;
    private Instant updatedAt;

    public SupplierMappingResponse() {
    }

    public SupplierMappingResponse(String hotelId, SupplierCode supplierCode, String supplierHotelId,
                                  SupplierMappingStatus status, Instant createdAt, Instant updatedAt) {
        this.hotelId = hotelId;
        this.supplierCode = supplierCode;
        this.supplierHotelId = supplierHotelId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getHotelId() {
        return hotelId;
    }

    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

