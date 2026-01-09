package com.hotelsystems.ai.bookingmanagement.ownerinventory.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventory_allotments",
       uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "room_type_id", "date"}),
       indexes = {
           @Index(name = "idx_hotel_date", columnList = "hotel_id,date"),
           @Index(name = "idx_hotel_roomtype_date", columnList = "hotel_id,room_type_id,date")
       })
public class InventoryAllotmentEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;
    
    @Column(name = "room_type_id", nullable = false)
    private String roomTypeId;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(name = "allotment_qty", nullable = false)
    private int allotmentQty;
    
    @Column(name = "stop_sell", nullable = false)
    private boolean stopSell = false;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Constructors
    public InventoryAllotmentEntity() {
    }
    
    public InventoryAllotmentEntity(String hotelId, String roomTypeId, LocalDate date, int allotmentQty) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.allotmentQty = allotmentQty;
        this.stopSell = false;
    }
    
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
    
    public String getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getAllotmentQty() {
        return allotmentQty;
    }
    
    public void setAllotmentQty(int allotmentQty) {
        this.allotmentQty = allotmentQty;
    }
    
    public boolean isStopSell() {
        return stopSell;
    }
    
    public void setStopSell(boolean stopSell) {
        this.stopSell = stopSell;
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

