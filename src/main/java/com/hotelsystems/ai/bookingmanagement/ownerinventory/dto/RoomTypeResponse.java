package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import java.time.Instant;

/**
 * Response DTO for room type information.
 */
public class RoomTypeResponse {
    
    private String id; // roomTypeId
    
    private String hotelId;
    
    private String name;
    
    private Integer maxGuests;
    
    private boolean active;
    
    private Instant createdAt;
    
    private Instant updatedAt;
    
    public RoomTypeResponse() {
    }
    
    public RoomTypeResponse(String id, String hotelId, String name, Integer maxGuests, 
                           boolean active, Instant createdAt, Instant updatedAt) {
        this.id = id;
        this.hotelId = hotelId;
        this.name = name;
        this.maxGuests = maxGuests;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
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

