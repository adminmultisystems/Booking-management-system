package com.hotelsystems.ai.bookingmanagement.service.hotel;

import com.hotelsystems.ai.bookingmanagement.dto.hotel.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.HotelEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Hotel Catalog
 * 
 * Database-backed hotel catalog implementation.
 * Fetches hotels from database and generates slugs from hotel names.
 */
@Component
@Slf4j
public class HotelCatalog {
    
    private final HotelRepository hotelRepository;
    
    public HotelCatalog(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
        log.info("Hotel catalog initialized with database repository");
    }
    
    /**
     * Get hotel by slug
     * 
     * Fetches all active hotels from database and matches by generated slug.
     * Slug is generated from hotel name: lowercase, spaces replaced with hyphens.
     * 
     * @param slug Hotel slug (generated from hotel name)
     * @return HotelResponse or null if not found
     */
    public HotelResponse findBySlug(String slug) {
        log.debug("Finding hotel by slug: {}", slug);
        
        // Fetch all active hotels from database
        List<HotelEntity> hotels = hotelRepository.findAll().stream()
                .filter(HotelEntity::isActive)
                .collect(Collectors.toList());
        
        // Find hotel by matching generated slug
        for (HotelEntity hotel : hotels) {
            String generatedSlug = generateSlug(hotel.getName());
            if (generatedSlug.equals(slug)) {
                log.debug("Found hotel: {} with slug: {}", hotel.getName(), slug);
                return toHotelResponse(hotel, generatedSlug);
            }
        }
        
        log.debug("Hotel not found for slug: {}", slug);
        return null;
    }
    
    /**
     * Check if hotel exists by slug
     * 
     * @param slug Hotel slug
     * @return true if exists, false otherwise
     */
    public boolean existsBySlug(String slug) {
        return findBySlug(slug) != null;
    }
    
    /**
     * Generate slug from hotel name
     * 
     * Converts to lowercase and replaces spaces with hyphens.
     * Example: "Grand Hotel" -> "grand-hotel"
     * 
     * @param hotelName Hotel name
     * @return Generated slug
     */
    private String generateSlug(String hotelName) {
        if (hotelName == null || hotelName.trim().isEmpty()) {
            return "";
        }
        return hotelName.toLowerCase()
                .trim()
                .replaceAll("\\s+", "-")  // Replace one or more spaces with single hyphen
                .replaceAll("[^a-z0-9\\-]", "")  // Remove special characters except hyphens
                .replaceAll("-+", "-")  // Replace multiple hyphens with single hyphen
                .replaceAll("^-|-$", "");  // Remove leading/trailing hyphens
    }
    
    /**
     * Convert HotelEntity to HotelResponse
     * 
     * @param entity Hotel entity from database
     * @param slug Generated slug
     * @return HotelResponse DTO
     */
    private HotelResponse toHotelResponse(HotelEntity entity, String slug) {
        return HotelResponse.builder()
                .slug(slug)
                .hotelId(entity.getId())
                .name(entity.getName())
                .build();
    }
}

