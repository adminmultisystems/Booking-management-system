package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class AvailabilityResponse {
    
    private UUID hotelId;
    private UUID roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean available;
    private BigDecimal totalPrice;
    private String currency;
    
    public AvailabilityResponse() {
    }
    
    public AvailabilityResponse(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut, 
                              boolean available, BigDecimal totalPrice, String currency) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.available = available;
        this.totalPrice = totalPrice;
        this.currency = currency;
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
    
    public boolean isAvailable() {
        return available;
    }
    
    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    public BigDecimal getTotalPrice() {
        return totalPrice;
    }
    
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

