package com.hotelsystems.ai.bookingmanagement.controller.hotel;

import com.hotelsystems.ai.bookingmanagement.dto.hotel.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.service.hotel.BookingHotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Hotel Controller
 * 
 * REST endpoints for hotel information operations.
 */
@RestController
@RequestMapping("/v1/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {
    
    private final BookingHotelService hotelService;
    
    /**
     * Get hotel by slug
     * 
     * GET /v1/hotels/{slug}
     */
    @GetMapping("/{slug}")
    public ResponseEntity<HotelResponse> getHotel(@PathVariable String slug) {
        
        log.info("GET /v1/hotels/{}", slug);
        
        HotelResponse response = hotelService.getHotelBySlug(slug);
        
        return ResponseEntity.ok(response);
    }
}

