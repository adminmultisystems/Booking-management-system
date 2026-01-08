package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuoteRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Internal REST Controller for Pricing Intelligence.
 * Exposes internal endpoint for pricing quotes.
 */
@RestController
@RequestMapping("/v1/internal/pricing")
public class PricingIntelligenceInternalController {
    
    private final PricingIntelligenceService pricingService;
    
    public PricingIntelligenceInternalController(PricingIntelligenceService pricingService) {
        this.pricingService = pricingService;
    }
    
    /**
     * POST /v1/internal/pricing/quote
     * 
     * Gets a pricing quote for a stay.
     * 
     * @param request Pricing quote request with hotelId, roomTypeId, checkIn, checkOut, guests, currency
     * @return PricingQuote with currency and total price in minor units
     */
    @PostMapping("/quote")
    public ResponseEntity<PricingQuote> getQuote(@RequestBody PricingQuoteRequest request) {
        try {
            PricingQuote quote = pricingService.getQuote(
                request.getHotelId(),
                request.getRoomTypeId(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                request.getCurrency()
            );
            return ResponseEntity.ok(quote);
        } catch (org.springframework.web.server.ResponseStatusException e) {
            // Re-throw to return proper HTTP status
            throw e;
        } catch (Exception e) {
            // Handle any other exceptions
            throw new org.springframework.web.server.ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR, "Error calculating pricing quote: " + e.getMessage());
        }
    }
}

