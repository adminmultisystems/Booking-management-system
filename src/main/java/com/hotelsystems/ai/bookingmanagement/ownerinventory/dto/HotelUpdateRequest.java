package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

/**
 * Request DTO for updating hotel details.
 * All fields are optional - only provided fields will be updated.
 */
public class HotelUpdateRequest {
    
    private String name;
    
    private Boolean active;
    
    public HotelUpdateRequest() {
    }
    
    public HotelUpdateRequest(String name, Boolean active) {
        this.name = name;
        this.active = active;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
}

