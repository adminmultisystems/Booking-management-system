package com.hotelsystems.ai.bookingmanagement.ownerinventory.controller;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeUpdateRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.service.RoomTypeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Admin controller for managing room types.
 */
@RestController
@RequestMapping("/v1/admin")
public class RoomTypeController {
    
    private final RoomTypeService roomTypeService;
    
    public RoomTypeController(RoomTypeService roomTypeService) {
        this.roomTypeService = roomTypeService;
    }
    
    /**
     * Creates a new room type for a hotel.
     * 
     * POST /v1/admin/hotels/{hotelId}/room-types
     * 
     * @param hotelId Hotel identifier from path
     * @param request Room type creation request
     * @return Created room type response (201 Created)
     */
    @PostMapping("/hotels/{hotelId}/room-types")
    public ResponseEntity<RoomTypeResponse> createRoomType(
            @PathVariable String hotelId,
            @Valid @RequestBody RoomTypeRequest request) {
        RoomTypeResponse response = roomTypeService.createRoomType(hotelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Updates an existing room type.
     * 
     * PATCH /v1/admin/room-types/{roomTypeId}
     * 
     * @param roomTypeId Room type identifier from path
     * @param request Update request with optional fields
     * @return Updated room type response (200 OK)
     */
    @PatchMapping("/room-types/{roomTypeId}")
    public ResponseEntity<RoomTypeResponse> updateRoomType(
            @PathVariable String roomTypeId,
            @Valid @RequestBody RoomTypeUpdateRequest request) {
        RoomTypeResponse response = roomTypeService.updateRoomType(roomTypeId, request);
        return ResponseEntity.ok(response);
    }
}

