package com.hotelsystems.ai.bookingmanagement.service.offer;

import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingEntity;
import com.hotelsystems.ai.bookingmanagement.supplier.service.SupplierMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Offer Routing Service
 * 
 * Determines whether to route offer requests to SUPPLIER or OWNER adapters.
 * Uses supplier_hotel_mapping table to check for ACTIVE supplier mappings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfferRoutingService {
    
    private final SupplierMappingService supplierMappingService;
    
    /**
     * Decide offer source for a hotel
     * 
     * @param hotelId Hotel identifier
     * @param slug Hotel slug (kept for backward compatibility, not used in routing logic)
     * @return OfferSource (SUPPLIER or OWNER)
     */
    public OfferSource decideSourceForHotel(String hotelId, String slug) {
        log.debug("Deciding offer source for hotelId: {}, slug: {}", hotelId, slug);
        
        // Check supplier_hotel_mapping table for ACTIVE status
        // Note: If mapping exists but status is not ACTIVE (e.g., DISABLED, NOT_FOUND),
        // findActiveMapping() returns empty, so we route to OWNER
        Optional<SupplierHotelMappingEntity> activeMapping = supplierMappingService.findActiveMapping(hotelId);
        
        if (activeMapping.isPresent()) {
            SupplierHotelMappingEntity mapping = activeMapping.get();
            log.debug("SUPPLIER route chosen because ACTIVE mapping exists - hotelId: {}, supplierCode: {}, supplierHotelId: {}", 
                    hotelId, mapping.getSupplierCode(), mapping.getSupplierHotelId());
            return OfferSource.SUPPLIER;
        }
        
        // Safe default: OWNER
        // This covers: no mapping exists, or mapping exists but status is not ACTIVE
        log.debug("OWNER route chosen because no ACTIVE mapping exists - hotelId: {}", hotelId);
        return OfferSource.OWNER;
    }
}

