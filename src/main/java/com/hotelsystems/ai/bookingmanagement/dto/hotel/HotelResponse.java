package com.hotelsystems.ai.bookingmanagement.dto.hotel;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Hotel Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {
    
    /**
     * Hotel slug (unique identifier for public API)
     */
    private String slug;
    
    /**
     * Internal hotel ID (optional)
     */
    private String hotelId;
    
    /**
     * Hotel name
     */
    private String name;
    
    /**
     * City name
     */
    private String city;
    
    /**
     * Country name
     */
    private String country;
    
    /**
     * Address line (optional)
     */
    private String addressLine;
    
    /**
     * List of image URLs
     */
    private List<String> images;
    
    /**
     * List of amenities
     */
    private List<String> amenities;
    
    /**
     * Policies summary (optional)
     */
    private String policiesSummary;
}

