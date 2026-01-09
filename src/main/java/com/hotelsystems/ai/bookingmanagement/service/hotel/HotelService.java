package com.hotelsystems.ai.bookingmanagement.service.hotel;

import com.hotelsystems.ai.bookingmanagement.dto.hotel.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Hotel Service
 * 
 * Service for retrieving hotel information.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {
    
    private final HotelCatalog hotelCatalog;
    
    /**
     * Get hotel by slug
     * 
     * @param slug Hotel slug
     * @return HotelResponse
     * @throws NotFoundException if hotel not found
     */
    public HotelResponse getHotelBySlug(String slug) {
        log.debug("Getting hotel by slug: {}", slug);
        
        HotelResponse hotel = hotelCatalog.findBySlug(slug);
        
        if (hotel == null) {
            log.warn("Hotel not found for slug: {}", slug);
            throw new NotFoundException("Hotel not found for slug: " + slug);
        }
        
        log.debug("Found hotel: {} ({})", hotel.getName(), hotel.getSlug());
        return hotel;
    }
}

