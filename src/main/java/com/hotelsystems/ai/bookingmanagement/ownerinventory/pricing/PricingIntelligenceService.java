package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Internal Pricing Intelligence Service.
 * Calculates pricing quotes without external API calls.
 */
@Service
public class PricingIntelligenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(PricingIntelligenceService.class);
    
    /**
     * Gets a pricing quote for a stay.
     * 
     * @param hotelId Hotel identifier (required)
     * @param roomTypeId Room type identifier (required)
     * @param checkIn Check-in date (required, inclusive)
     * @param checkOut Check-out date (required, exclusive)
     * @param guests Number of guests
     * @param currency Currency code (optional, defaults to "INR")
     * @return PricingQuote with currency and total price in minor units
     * @throws org.springframework.web.server.ResponseStatusException if validation fails
     */
    public PricingQuote getQuote(String hotelId, String roomTypeId, LocalDate checkIn, 
                                 LocalDate checkOut, int guests, String currency) {
        
        // Validation
        if (hotelId == null || hotelId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "hotelId is required");
        }
        
        if (roomTypeId == null || roomTypeId.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "roomTypeId is required");
        }
        
        if (checkIn == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "checkIn is required");
        }
        
        if (checkOut == null) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "checkOut is required");
        }
        
        if (!checkIn.isBefore(checkOut)) {
            throw new org.springframework.web.server.ResponseStatusException(
                org.springframework.http.HttpStatus.BAD_REQUEST, "checkIn must be before checkOut");
        }
        
        // Calculate nights (checkIn inclusive, checkOut exclusive)
        long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
        
        // Resolve currency (default to INR if null or blank)
        String resolvedCurrency = (currency == null || currency.trim().isEmpty()) ? "INR" : currency;
        
        // Determine base rate based on room type
        long baseRate = getBaseRateForRoomType(roomTypeId);
        
        // Calculate total price
        long totalPriceMinor = nights * baseRate;
        
        // Log the quote calculation
        logger.info("PRICING_INTERNAL quote - hotelId: {}, roomTypeId: {}, checkIn: {}, checkOut: {}, guests: {}, nights: {}, currency: {}, totalPriceMinor: {}",
            hotelId, roomTypeId, checkIn, checkOut, guests, nights, resolvedCurrency, totalPriceMinor);
        
        return new PricingQuote(resolvedCurrency, totalPriceMinor);
    }
    
    /**
     * Gets the base rate per night based on room type.
     * 
     * @param roomTypeId Room type identifier
     * @return Base rate per night in minor units
     */
    private long getBaseRateForRoomType(String roomTypeId) {
        if (roomTypeId == null || roomTypeId.trim().isEmpty()) {
            return 10000L; // Default rate
        }
        
        // Convert to uppercase for case-insensitive matching
        String roomTypeUpper = roomTypeId.trim().toUpperCase();
        
        long baseRate;
        switch (roomTypeUpper) {
            case "DELUXE" -> baseRate = 12000L;
            case "SUITE" -> baseRate = 20000L;
            case "STANDARD" -> baseRate = 8000L;
            default -> baseRate = 10000L;
        }
        
        return baseRate;
    }
}

