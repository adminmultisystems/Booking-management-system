package com.hotelsystems.ai.bookingmanagement.ownerinventory.controller;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.BulkUpsertInventoryRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.InventoryAllotmentResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.RoomTypeEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing.PricingIntelligenceClient;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryAllotmentRepository;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.RoomTypeRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Admin controller for managing inventory allotments.
 */
@RestController
@RequestMapping("/v1/admin/hotels/{hotelId}/inventory")
public class AdminInventoryController {
    
    private final InventoryAllotmentRepository allotmentRepository;
    private final PricingIntelligenceClient pricingClient;
    private final RoomTypeRepository roomTypeRepository;
    
    public AdminInventoryController(
            InventoryAllotmentRepository allotmentRepository,
            PricingIntelligenceClient pricingClient,
            RoomTypeRepository roomTypeRepository) {
        this.allotmentRepository = allotmentRepository;
        this.pricingClient = pricingClient;
        this.roomTypeRepository = roomTypeRepository;
    }
    
    /**
     * Bulk upsert inventory allotments for a date range.
     * 
     * POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert
     * 
     * @param hotelId Hotel identifier from path
     * @param request Bulk upsert request with roomTypeId, date range, allotmentQty, and stopSell
     * @return ResponseEntity with success message
     */
    @PostMapping("/bulk-upsert")
    @Transactional
    public ResponseEntity<String> bulkUpsertInventory(
            @PathVariable String hotelId,
            @Valid @RequestBody BulkUpsertInventoryRequest request) {
        
        // Validate allotmentQty >= 0 (already validated by @Min annotation, but double-check)
        if (request.getAllotmentQty() < 0) {
            return ResponseEntity.badRequest()
                .body("allotmentQty must be >= 0");
        }
        
        // Get today's date for validation
        LocalDate today = LocalDate.now();
        
        // Validate dates are not in the past (MUST be first check)
        if (request.getStartDate() == null || request.getEndDate() == null) {
            return ResponseEntity.badRequest()
                .body("startDate and endDate are required");
        }
        
        // Check if startDate is in the past
        if (request.getStartDate().isBefore(today)) {
            return ResponseEntity.badRequest()
                .body(String.format("startDate (%s) cannot be in the past. Today's date is %s. Please use today or a future date.", 
                    request.getStartDate(), today));
        }
        
        // Check if endDate is in the past
        if (request.getEndDate().isBefore(today)) {
            return ResponseEntity.badRequest()
                .body(String.format("endDate (%s) cannot be in the past. Today's date is %s. Please use today or a future date.", 
                    request.getEndDate(), today));
        }
        
        // Validate date range (startDate must be before endDate)
        if (request.getStartDate().isAfter(request.getEndDate()) || 
            request.getStartDate().equals(request.getEndDate())) {
            return ResponseEntity.badRequest()
                .body("startDate must be before endDate");
        }
        
        // Validate room type exists and belongs to this hotel
        Optional<RoomTypeEntity> roomTypeOpt = roomTypeRepository.findById(request.getRoomTypeId());
        if (roomTypeOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                .body(String.format("Room type with id '%s' does not exist. Please create the room type first before setting inventory.", 
                    request.getRoomTypeId()));
        }
        
        RoomTypeEntity roomType = roomTypeOpt.get();
        if (!roomType.getHotelId().equals(hotelId)) {
            return ResponseEntity.badRequest()
                .body(String.format("Room type '%s' does not belong to hotel '%s'. It belongs to hotel '%s'.", 
                    request.getRoomTypeId(), hotelId, roomType.getHotelId()));
        }
        
        // Check if room type is active
        if (!roomType.isActive()) {
            return ResponseEntity.badRequest()
                .body(String.format("Room type '%s' is inactive. Cannot set inventory for inactive room types.", 
                    request.getRoomTypeId()));
        }
        
        // Upsert one row per date from startDate (inclusive) to endDate (exclusive)
        LocalDate currentDate = request.getStartDate();
        int upsertedCount = 0;
        
        while (currentDate.isBefore(request.getEndDate())) {
            // Check if allotment already exists
            InventoryAllotmentEntity existing = allotmentRepository
                .findByHotelIdAndRoomTypeIdAndDate(hotelId, request.getRoomTypeId(), currentDate)
                .orElse(null);
            
            if (existing != null) {
                // Update existing
                existing.setAllotmentQty(request.getAllotmentQty());
                existing.setStopSell(request.isStopSell());
                allotmentRepository.save(existing);
            } else {
                // Create new
                InventoryAllotmentEntity newAllotment = new InventoryAllotmentEntity(
                    hotelId, request.getRoomTypeId(), currentDate, request.getAllotmentQty());
                newAllotment.setStopSell(request.isStopSell());
                allotmentRepository.save(newAllotment);
            }
            
            upsertedCount++;
            currentDate = currentDate.plusDays(1);
        }
        
        return ResponseEntity.ok(
            String.format("Successfully upserted %d inventory allotment(s)", upsertedCount));
    }
    
    /**
     * Get inventory allotments for a hotel and optional filters.
     * 
     * GET /v1/admin/hotels/{hotelId}/inventory?roomTypeId=...&start=...&end=...
     * 
     * @param hotelId Hotel identifier from path
     * @param roomTypeId Optional room type filter (query parameter)
     * @param start Optional start date filter (query parameter)
     * @param end Optional end date filter (query parameter)
     * @return List of inventory allotments
     */
    @GetMapping
    public ResponseEntity<List<InventoryAllotmentResponse>> getInventory(
            @PathVariable String hotelId,
            @RequestParam(required = false) String roomTypeId,
            @RequestParam(required = false) LocalDate start,
            @RequestParam(required = false) LocalDate end) {
        
        List<InventoryAllotmentEntity> allotments;
        
        if (start != null && end != null) {
            // Use date range query
            if (roomTypeId != null && !roomTypeId.isEmpty()) {
                // Filter by roomTypeId and date range
                allotments = allotmentRepository.findByHotelIdAndRoomTypeIdAndDateBetween(
                    hotelId, roomTypeId, start, end.minusDays(1));
            } else {
                // Filter by date range only
                allotments = allotmentRepository.findByHotelIdAndOptionalRoomTypeIdAndDateRange(
                    hotelId, null, start, end);
            }
        } else if (roomTypeId != null && !roomTypeId.isEmpty()) {
            // Filter by roomTypeId only (no date range)
            // For simplicity, return all for this hotel and roomTypeId
            // In production, you might want to add pagination or limit
            allotments = allotmentRepository.findAll().stream()
                .filter(a -> a.getHotelId().equals(hotelId) && a.getRoomTypeId().equals(roomTypeId))
                .collect(Collectors.toList());
        } else {
            // No filters - return all for hotel
            allotments = allotmentRepository.findAll().stream()
                .filter(a -> a.getHotelId().equals(hotelId))
                .collect(Collectors.toList());
        }
        
        // Convert entities to response DTOs
        List<InventoryAllotmentResponse> responses = allotments.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }
    
    /**
     * Converts InventoryAllotmentEntity to InventoryAllotmentResponse.
     * Includes dummy pricing for local/testing.
     */
    private InventoryAllotmentResponse toResponse(InventoryAllotmentEntity entity) {
        InventoryAllotmentResponse response = new InventoryAllotmentResponse();
        response.setId(entity.getId());
        response.setHotelId(entity.getHotelId());
        response.setRoomTypeId(entity.getRoomTypeId());
        response.setDate(entity.getDate());
        response.setAllotmentQty(entity.getAllotmentQty());
        response.setStopSell(entity.isStopSell());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        
        // Add dummy pricing for local/testing
        // For single date, calculate price for one night (checkIn = date, checkOut = date + 1 day)
        try {
            LocalDate checkIn = entity.getDate();
            LocalDate checkOut = entity.getDate().plusDays(1);
            PricingQuote quote = pricingClient.getQuote(
                entity.getHotelId(), 
                entity.getRoomTypeId(), 
                checkIn, 
                checkOut, 
                1, // default 1 guest
                "INR" // default currency
            );
            if (quote != null) {
                response.setCurrency(quote.getCurrency());
                response.setTotalPriceMinor(quote.getTotalPriceMinor());
            } else {
                // Fallback if quote is null
                response.setCurrency("INR");
                response.setTotalPriceMinor(10000L); // 1 night = 10000
            }
        } catch (Exception e) {
            // Ensure pricing is always set even if pricing client fails
            response.setCurrency("INR");
            response.setTotalPriceMinor(10000L); // 1 night = 10000
        }
        
        return response;
    }
}

