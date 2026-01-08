package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

/**
 * Internal Client for Pricing Intelligence service.
 * Delegates to PricingIntelligenceService (NO HTTP calls).
 */
@Service
public class PricingIntelligenceClient {
    
    private final PricingIntelligenceService pricingService;
    
    public PricingIntelligenceClient(PricingIntelligenceService pricingService) {
        this.pricingService = pricingService;
    }
    
    /**
     * Gets a pricing quote from the pricing intelligence service.
     * 
     * Delegates directly to PricingIntelligenceService (NO HTTP calls).
     * 
     * @param hotelId Hotel identifier
     * @param roomTypeId Room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @param guests Number of guests
     * @param currency Currency code
     * @return PricingQuote with pricing information
     */
    public PricingQuote getQuote(String hotelId, String roomTypeId, LocalDate checkIn, 
                                LocalDate checkOut, int guests, String currency) {
        // Delegate directly to internal service (NO HTTP)
        return pricingService.getQuote(hotelId, roomTypeId, checkIn, checkOut, guests, currency);
    }
}

