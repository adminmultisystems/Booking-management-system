package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for creating a new hotel.
 */
public class HotelRequest {
    
    @NotBlank(message = "id is required")
    private String id; // hotelId
    
    @NotBlank(message = "name is required")
    private String name;
    
    public HotelRequest() {
    }
    
    public HotelRequest(String id, String name) {
        this.id = id;
        this.name = name;
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
}

