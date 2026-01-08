package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import java.time.LocalDate;
import java.util.UUID;

public class AvailabilityRequest {
    
    private UUID hotelId;
    private UUID roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    
    public AvailabilityRequest() {
    }
    
    public AvailabilityRequest(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
    }
    
    public UUID getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }
    
    public UUID getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(UUID roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public LocalDate getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    
    public LocalDate getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}

