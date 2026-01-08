package com.hotelsystems.ai.bookingmanagement.service.hotel;

import com.hotelsystems.ai.bookingmanagement.dto.hotel.HotelResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stub Hotel Catalog
 * 
 * In-memory hotel catalog implementation.
 * This will be replaced by a database-backed repository in the future.
 */
@Component
@Slf4j
public class StubHotelCatalog {
    
    private final Map<String, HotelResponse> hotelsBySlug;
    
    public StubHotelCatalog() {
        this.hotelsBySlug = new HashMap<>();
        initializeStubData();
    }
    
    /**
     * Get hotel by slug
     * 
     * @param slug Hotel slug
     * @return HotelResponse or null if not found
     */
    public HotelResponse findBySlug(String slug) {
        return hotelsBySlug.get(slug);
    }
    
    /**
     * Check if hotel exists by slug
     * 
     * @param slug Hotel slug
     * @return true if exists, false otherwise
     */
    public boolean existsBySlug(String slug) {
        return hotelsBySlug.containsKey(slug);
    }
    
    /**
     * Initialize stub hotel data
     */
    private void initializeStubData() {
        // Demo hotel 1
        HotelResponse hotel1 = HotelResponse.builder()
                .slug("hotel-demo")
                .hotelId("HOTEL-001")
                .name("Grand Plaza Hotel")
                .city("New York")
                .country("United States")
                .addressLine("123 Main Street, Manhattan")
                .images(Arrays.asList(
                        "https://example.com/images/hotel-demo-1.jpg",
                        "https://example.com/images/hotel-demo-2.jpg"
                ))
                .amenities(Arrays.asList(
                        "Free WiFi",
                        "Swimming Pool",
                        "Fitness Center",
                        "Restaurant",
                        "Room Service",
                        "Parking"
                ))
                .policiesSummary("Free cancellation up to 24 hours before check-in. Check-in: 3:00 PM, Check-out: 11:00 AM.")
                .build();
        
        hotelsBySlug.put("hotel-demo", hotel1);
        
        // Demo hotel 2
        HotelResponse hotel2 = HotelResponse.builder()
                .slug("hotel-123")
                .hotelId("HOTEL-002")
                .name("Oceanview Resort")
                .city("Miami")
                .country("United States")
                .addressLine("456 Beach Boulevard")
                .images(Arrays.asList(
                        "https://example.com/images/hotel-123-1.jpg",
                        "https://example.com/images/hotel-123-2.jpg",
                        "https://example.com/images/hotel-123-3.jpg"
                ))
                .amenities(Arrays.asList(
                        "Free WiFi",
                        "Beach Access",
                        "Spa",
                        "Pool",
                        "Restaurant",
                        "Bar",
                        "Concierge"
                ))
                .policiesSummary("Free cancellation up to 48 hours before check-in. Check-in: 4:00 PM, Check-out: 12:00 PM.")
                .build();
        
        hotelsBySlug.put("hotel-123", hotel2);
        
        // Demo hotel 3
        HotelResponse hotel3 = HotelResponse.builder()
                .slug("hotel-paris")
                .hotelId("HOTEL-003")
                .name("Champs-Élysées Boutique")
                .city("Paris")
                .country("France")
                .addressLine("789 Avenue des Champs-Élysées")
                .images(Arrays.asList(
                        "https://example.com/images/hotel-paris-1.jpg"
                ))
                .amenities(Arrays.asList(
                        "Free WiFi",
                        "Breakfast Included",
                        "Concierge",
                        "Laundry Service"
                ))
                .policiesSummary("Non-refundable. Check-in: 2:00 PM, Check-out: 11:00 AM.")
                .build();
        
        hotelsBySlug.put("hotel-paris", hotel3);
        
        log.info("Initialized stub hotel catalog with {} hotels", hotelsBySlug.size());
    }
}

