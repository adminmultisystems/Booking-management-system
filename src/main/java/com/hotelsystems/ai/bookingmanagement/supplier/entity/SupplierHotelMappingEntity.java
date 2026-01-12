package com.hotelsystems.ai.bookingmanagement.supplier.entity;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a mapping between a hotel ID and a supplier hotel ID.
 * Uses composite primary key (hotelId + supplierCode).
 */
@Entity
@Table(name = "supplier_hotel_mapping")
@IdClass(SupplierHotelMappingId.class)
public class SupplierHotelMappingEntity {

    @Id
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;

    @Id
    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_code", nullable = false)
    private SupplierCode supplierCode;

    @Column(name = "supplier_hotel_id")
    private String supplierHotelId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SupplierMappingStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public SupplierHotelMappingEntity() {
    }

    public SupplierHotelMappingEntity(String hotelId, SupplierCode supplierCode, 
                                     String supplierHotelId, SupplierMappingStatus status) {
        this.hotelId = hotelId;
        this.supplierCode = supplierCode;
        this.supplierHotelId = supplierHotelId;
        this.status = status;
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (updatedAt == null) {
            updatedAt = Instant.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
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

