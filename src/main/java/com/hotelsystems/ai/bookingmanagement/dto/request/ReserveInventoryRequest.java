package com.hotelsystems.ai.bookingmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Reserve Inventory Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReserveInventoryRequest {
    
    private String bookingReference;
    
    private String hotelId;
    
    private String roomId;
    
    private String roomType;
    
    private LocalDate checkInDate;
    
    private LocalDate checkOutDate;
    
    private Integer numberOfRooms;
    
    private Integer numberOfGuests;
    
    private BigDecimal totalAmount;
    
    private String currency;
    
    private String metadata; // JSON string for additional data
}

