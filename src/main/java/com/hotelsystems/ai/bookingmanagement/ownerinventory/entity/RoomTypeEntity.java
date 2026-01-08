package com.hotelsystems.ai.bookingmanagement.ownerinventory.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a room type for a hotel.
 * Uses String id (roomTypeId) as primary key.
 * Unique constraints: (hotel_id, id) and (hotel_id, name)
 */
@Entity
@Table(name = "room_types",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_hotel_roomtype_id", columnNames = {"hotel_id", "id"}),
           @UniqueConstraint(name = "uk_hotel_roomtype_name", columnNames = {"hotel_id", "name"})
       },
       indexes = {
           @Index(name = "idx_hotel_id", columnList = "hotel_id")
       })
public class RoomTypeEntity {
    
    @Id
    @Column(name = "id", nullable = false)
    private String id; // roomTypeId
    
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "max_guests")
    private Integer maxGuests;
    
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Constructors
    public RoomTypeEntity() {
    }
    
    public RoomTypeEntity(String id, String hotelId, String name) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.active = true;
    }
    
    public RoomTypeEntity(String id, String hotelId, String name, Integer maxGuests) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.maxGuests = maxGuests;
        this.active = true;
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
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getMaxGuests() {
        return maxGuests;
    }
    
    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
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

