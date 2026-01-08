package com.hotelsystems.ai.bookingmanagement.enums;

/**
 * Recheck Result Enum
 * 
 * Result status when rechecking offer availability.
 */
public enum RecheckResult {
    /**
     * Offer is still available and valid
     */
    OK,
    
    /**
     * Price has changed from the original offer
     */
    PRICE_CHANGED,
    
    /**
     * Room is sold out or no longer available
     */
    SOLD_OUT
}

