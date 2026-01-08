package com.hotelsystems.ai.bookingmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Cancel Inventory Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelInventoryRequest {
    
    private String bookingReference;
    
    private String inventoryReservationId; // From reserve step
    
    private String supplierBookingReference; // If already confirmed
    
    private String hotelId;
    
    private String roomId;
    
    private String reason;
}

