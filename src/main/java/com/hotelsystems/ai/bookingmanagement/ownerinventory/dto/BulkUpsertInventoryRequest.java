package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * Request DTO for bulk upserting inventory allotments.
 */
public class BulkUpsertInventoryRequest {
    
    @NotBlank(message = "roomTypeId is required")
    private String roomTypeId;
    
    @NotNull(message = "startDate is required")
    private LocalDate startDate;
    
    @NotNull(message = "endDate is required")
    private LocalDate endDate;
    
    @Min(value = 0, message = "allotmentQty must be >= 0")
    private int allotmentQty;
    
    private boolean stopSell = false;
    
    public BulkUpsertInventoryRequest() {
    }
    
    public BulkUpsertInventoryRequest(String roomTypeId, LocalDate startDate, LocalDate endDate, 
                                     int allotmentQty, boolean stopSell) {
        this.roomTypeId = roomTypeId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.allotmentQty = allotmentQty;
        this.stopSell = stopSell;
    }
    
    public String getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    
    public LocalDate getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
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
}

