package com.hotelsystems.ai.bookingmanagement.service.adapter;

/**
 * Recheck Status Enum
 * 
 * Status returned when rechecking booking availability.
 */
public enum RecheckStatus {
    /**
     * Booking is still available and valid
     */
    OK,
    
    /**
     * Room is sold out or no longer available
     */
    SOLD_OUT,
    
    /**
     * Price has changed from the original offer
     */
    PRICE_CHANGED
}


