package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

/**
 * Request DTO for pricing quote from Pricing Intelligence service.
 */
public class PricingQuoteRequest {
    
    private String hotelId;
    private String roomTypeId;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkIn;
    
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate checkOut;
    
    private int guests;
    private String currency;
    
    public PricingQuoteRequest() {
    }
    
    public PricingQuoteRequest(String hotelId, String roomTypeId, LocalDate checkIn, 
                               LocalDate checkOut, int guests, String currency) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guests = guests;
        this.currency = currency;
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
    
    public int getGuests() {
        return guests;
    }
    
    public void setGuests(int guests) {
        this.guests = guests;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
}

