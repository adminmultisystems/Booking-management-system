package com.hotelsystems.ai.bookingmanagement.dto.offer;

import com.fasterxml.jackson.databind.JsonNode;
import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Offer DTO
 * 
 * Normalized offer representation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OfferDto {
    
    /**
     * Offer identifier
     */
    private String offerId;
    
    /**
     * Source of the offer (SUPPLIER or OWNER)
     */
    private OfferSource source;
    
    /**
     * Hotel identifier
     */
    private String hotelId;
    
    /**
     * Room type identifier
     */
    private String roomTypeId;
    
    /**
     * Check-in date
     */
    private LocalDate checkIn;
    
    /**
     * Check-out date
     */
    private LocalDate checkOut;
    
    /**
     * Total price with currency
     */
    private MoneyDto totalPrice;
    
    /**
     * Cancellation policy summary (optional)
     */
    private String cancellationPolicySummary;
    
    /**
     * Raw payload (JsonNode or String) - holds supplier raw payload when needed
     */
    private JsonNode payload;
}

