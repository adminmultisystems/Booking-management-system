package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Response DTO for inventory allotment.
 */
public class InventoryAllotmentResponse {
    
    private UUID id;
    private String hotelId;
    private String roomTypeId;
    private LocalDate date;
    private int allotmentQty;
    private boolean stopSell;
    private Instant createdAt;
    private Instant updatedAt;
    
    // Pricing information (dummy pricing for local/testing)
    private String currency;
    private Long totalPriceMinor;
    
    public InventoryAllotmentResponse() {
    }
    
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
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
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getAllotmentQty() {
        return allotmentQty;
    }
    
    public void setAllotmentQty(int allotmentQty) {
        this.allotmentQty = allotmentQty;
    }
    
    public boolean isStopSell() {
        return stopSell;
    }
    
    public void setStopSell(boolean stopSell) {
        this.stopSell = stopSell;
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
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public Long getTotalPriceMinor() {
        return totalPriceMinor;
    }
    
    public void setTotalPriceMinor(Long totalPriceMinor) {
        this.totalPriceMinor = totalPriceMinor;
    }
}

