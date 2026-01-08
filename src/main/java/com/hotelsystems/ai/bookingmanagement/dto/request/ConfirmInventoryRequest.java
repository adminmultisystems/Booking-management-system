package com.hotelsystems.ai.bookingmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Confirm Inventory Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmInventoryRequest {
    
    private String bookingReference;
    
    private String inventoryReservationId; // From reserve step
    
    private String hotelId;
    
    private String roomId;
    
    private String metadata; // JSON string for additional data
}

