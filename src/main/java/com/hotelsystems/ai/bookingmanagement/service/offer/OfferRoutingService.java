package com.hotelsystems.ai.bookingmanagement.service.offer;

import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Offer Routing Service
 * 
 * Determines whether to route offer requests to SUPPLIER or OWNER adapters.
 * 
 * Note: This is a stub implementation. Proper supplier mapping logic will be implemented
 * via admin endpoints. For now, uses simple heuristics:
 * - If slug contains "supplier" OR hotelId ends with "S" => SUPPLIER
 * - Otherwise => OWNER (safe default)
 */
@Service
@Slf4j
public class OfferRoutingService {
    
    /**
     * Decide offer source for a hotel
     * 
     * @param hotelId Hotel identifier
     * @param slug Hotel slug
     * @return OfferSource (SUPPLIER or OWNER)
     */
    public OfferSource decideSourceForHotel(String hotelId, String slug) {
        log.debug("Deciding offer source for hotelId: {}, slug: {}", hotelId, slug);
        
        // Note: Will be replaced with proper supplier mapping lookup in future implementation
        // For now, use simple stub heuristics:
        
        // Check if slug contains "supplier"
        if (slug != null && slug.toLowerCase().contains("supplier")) {
            log.debug("Routing to SUPPLIER (slug contains 'supplier')");
            return OfferSource.SUPPLIER;
        }
        
        // Check if hotelId ends with "S" (stub pattern)
        if (hotelId != null && hotelId.endsWith("S")) {
            log.debug("Routing to SUPPLIER (hotelId ends with 'S')");
            return OfferSource.SUPPLIER;
        }
        
        // Safe default: OWNER
        log.debug("Routing to OWNER (default)");
        return OfferSource.OWNER;
    }
}

