package com.hotelsystems.ai.bookingmanagement.supplier.entity;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key for SupplierHotelMappingEntity.
 */
public class SupplierHotelMappingId implements Serializable {

    private String hotelId;
    private SupplierCode supplierCode;

    public SupplierHotelMappingId() {
    }

    public SupplierHotelMappingId(String hotelId, SupplierCode supplierCode) {
        this.hotelId = hotelId;
        this.supplierCode = supplierCode;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierHotelMappingId that = (SupplierHotelMappingId) o;
        return Objects.equals(hotelId, that.hotelId) && supplierCode == that.supplierCode;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hotelId, supplierCode);
    }
}

