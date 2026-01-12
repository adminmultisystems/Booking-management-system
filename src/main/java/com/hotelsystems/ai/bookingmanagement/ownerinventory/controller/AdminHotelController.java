package com.hotelsystems.ai.bookingmanagement.ownerinventory.controller;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelUpdateRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.service.OwnerInventoryHotelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for managing hotels.
 * 
 * Handles hotel creation and updates for owner inventory management.
 */
@RestController
@RequestMapping("/v1/admin/hotels")
public class AdminHotelController {
    
    private final OwnerInventoryHotelService hotelService;
    
    public AdminHotelController(OwnerInventoryHotelService hotelService) {
        this.hotelService = hotelService;
    }
    
    /**
     * Creates a new hotel.
     * 
     * POST /v1/admin/hotels
     * 
     * @param request Hotel creation request
     * @return Created hotel response (201 Created)
     */
    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(@Valid @RequestBody HotelRequest request) {
        HotelResponse response = hotelService.createHotel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Updates an existing hotel.
     * 
     * PATCH /v1/admin/hotels/{hotelId}
     * 
     * @param hotelId Hotel identifier from path
     * @param request Update request with optional fields
     * @return Updated hotel response (200 OK)
     */
    @PatchMapping("/{hotelId}")
    public ResponseEntity<HotelResponse> updateHotel(
            @PathVariable String hotelId,
            @Valid @RequestBody HotelUpdateRequest request) {
        HotelResponse response = hotelService.updateHotel(hotelId, request);
        return ResponseEntity.ok(response);
    }
}

