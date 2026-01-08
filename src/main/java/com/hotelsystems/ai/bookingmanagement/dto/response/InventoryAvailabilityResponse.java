package com.hotelsystems.ai.bookingmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Inventory Availability Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAvailabilityResponse {
    
    private boolean available;
    
    private String roomId;
    
    private String roomType;
    
    private BigDecimal price;
    
    private String currency;
    
    private String message;
    
    private String inventoryReference; // Reference from inventory system
}

