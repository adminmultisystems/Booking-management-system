package com.hotelsystems.ai.bookingmanagement.dto.request;

import com.hotelsystems.ai.bookingmanagement.dto.offer.MoneyDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Price Snapshot DTO
 * 
 * Represents a snapshot of pricing information at the time of booking creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceSnapshotDto {
    
    /**
     * Total price for the booking
     */
    private MoneyDto totalPrice;
    
    /**
     * Base room price (before taxes/fees)
     */
    private MoneyDto basePrice;
    
    /**
     * Taxes amount
     */
    private MoneyDto taxes;
    
    /**
     * Fees amount (service fees, etc.)
     */
    private MoneyDto fees;
    
    /**
     * Price per night (optional)
     */
    private MoneyDto pricePerNight;
    
    /**
     * Number of nights
     */
    private Integer nights;
}

