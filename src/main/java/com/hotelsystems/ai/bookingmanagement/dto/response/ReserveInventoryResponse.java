package com.hotelsystems.ai.bookingmanagement.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Reserve Inventory Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveInventoryResponse {
    
    private boolean success;
    
    private String inventoryReservationId; // Temporary reservation ID from inventory system
    
    private String message;
    
    private Long expiresInSeconds; // How long the reservation is held
}

