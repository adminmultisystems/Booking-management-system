package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a new room type.
 */
public class RoomTypeRequest {
    
    @NotBlank(message = "id is required")
    private String id; // roomTypeId
    
    @NotBlank(message = "name is required")
    private String name;
    
    private Integer maxGuests;
    
    public RoomTypeRequest() {
    }
    
    public RoomTypeRequest(String id, String name) {
        this.id = id;
        this.name = name;
    }
    
    public RoomTypeRequest(String id, String name, Integer maxGuests) {
        this.id = id;
        this.name = name;
        this.maxGuests = maxGuests;
    }
    
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
    
    public Integer getMaxGuests() {
        return maxGuests;
    }
    
    public void setMaxGuests(Integer maxGuests) {
        this.maxGuests = maxGuests;
    }
}

