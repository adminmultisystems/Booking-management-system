package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

/**
 * Request DTO for updating room type details.
 * All fields are optional - only provided fields will be updated.
 */
public class RoomTypeUpdateRequest {
    
    private String name;
    
    private Integer maxGuests;
    
    private Boolean active;
    
    public RoomTypeUpdateRequest() {
    }
    
    public RoomTypeUpdateRequest(String name, Integer maxGuests, Boolean active) {
        this.name = name;
        this.maxGuests = maxGuests;
        this.active = active;
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
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
}

