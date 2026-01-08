package com.hotelsystems.ai.bookingmanagement.ownerinventory.controller;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.InventoryAllotmentResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.InventoryUpdateRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing.PricingIntelligenceClient;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryAllotmentRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Admin controller for updating individual inventory allotment rows.
 */
@RestController
@RequestMapping("/v1/admin/inventory")
public class AdminInventoryUpdateController {
    
    private final InventoryAllotmentRepository allotmentRepository;
    private final PricingIntelligenceClient pricingClient;
    
    public AdminInventoryUpdateController(
            InventoryAllotmentRepository allotmentRepository,
            PricingIntelligenceClient pricingClient) {
        this.allotmentRepository = allotmentRepository;
        this.pricingClient = pricingClient;
    }
    
    /**
     * Updates a specific inventory allotment row.
     * 
     * PATCH /v1/admin/inventory/{inventoryRowId}
     * 
     * @param inventoryRowId Inventory row identifier (UUID) from path
     * @param request Update request with optional allotmentQty and stopSell
     * @return Updated inventory allotment response (200 OK)
     * @throws NotFoundException if inventory row not found
     */
    @PatchMapping("/{inventoryRowId}")
    @Transactional
    public ResponseEntity<InventoryAllotmentResponse> updateInventory(
            @PathVariable UUID inventoryRowId,
            @Valid @RequestBody InventoryUpdateRequest request) {
        
        // Find inventory row or throw NotFoundException
        InventoryAllotmentEntity entity = allotmentRepository.findById(inventoryRowId)
            .orElseThrow(() -> new NotFoundException("Inventory row with id '" + inventoryRowId + "' not found"));
        
        // Update only provided fields
        if (request.getAllotmentQty() != null) {
            // Validate allotmentQty >= 0
            if (request.getAllotmentQty() < 0) {
                throw new IllegalArgumentException("allotmentQty must be >= 0");
            }
            entity.setAllotmentQty(request.getAllotmentQty());
        }
        
        if (request.getStopSell() != null) {
            entity.setStopSell(request.getStopSell());
        }
        
        // Save updated entity (updatedAt will be auto-set by @PreUpdate)
        InventoryAllotmentEntity updatedEntity = allotmentRepository.save(entity);
        
        // Convert to response DTO
        InventoryAllotmentResponse response = toResponse(updatedEntity);
        
        return ResponseEntity.ok(response);
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

