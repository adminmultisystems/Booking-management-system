package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import jakarta.validation.constraints.Min;

/**
 * Request DTO for updating inventory allotment.
 * All fields are optional - only provided fields will be updated.
 */
public class InventoryUpdateRequest {
    
    @Min(value = 0, message = "allotmentQty must be >= 0")
    private Integer allotmentQty;
    
    private Boolean stopSell;
    
    public InventoryUpdateRequest() {
    }
    
    public InventoryUpdateRequest(Integer allotmentQty, Boolean stopSell) {
        this.allotmentQty = allotmentQty;
        this.stopSell = stopSell;
    }
    
    public Integer getAllotmentQty() {
        return allotmentQty;
    }
    
    public void setAllotmentQty(Integer allotmentQty) {
        this.allotmentQty = allotmentQty;
    }
    
    public Boolean getStopSell() {
        return stopSell;
    }
    
    public void setStopSell(Boolean stopSell) {
        this.stopSell = stopSell;
    }
}

