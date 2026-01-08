package com.hotelsystems.ai.bookingmanagement.ownerinventory.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Entity representing a hotel.
 * Uses String id (hotelId) as primary key.
 */
@Entity
@Table(name = "hotels")
public class HotelEntity {
    
    @Id
    @Column(name = "id", nullable = false, unique = true)
    private String id; // hotelId
    
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "active", nullable = false)
    private boolean active = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Constructors
    public HotelEntity() {
    }
    
    public HotelEntity(String id, String name) {
        this.id = id;
        this.name = name;
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
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
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

