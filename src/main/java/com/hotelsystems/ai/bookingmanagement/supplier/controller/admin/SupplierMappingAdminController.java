package com.hotelsystems.ai.bookingmanagement.supplier.controller.admin;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierMappingResponse;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.UpsertSupplierMappingRequest;
import com.hotelsystems.ai.bookingmanagement.supplier.service.SupplierMappingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Admin controller for managing supplier hotel mappings.
 */
@RestController
@RequestMapping("/v1/admin/hotels")
public class SupplierMappingAdminController {

    private final SupplierMappingService service;

    public SupplierMappingAdminController(SupplierMappingService service) {
        this.service = service;
    }

    /**
     * GET /v1/admin/hotels/{hotelId}/supplier-mapping
     * Get all supplier mappings for a hotel.
     */
    @GetMapping("/{hotelId}/supplier-mapping")
    public ResponseEntity<List<SupplierMappingResponse>> getMappings(@PathVariable String hotelId) {
        List<SupplierMappingResponse> mappings = service.getMappings(hotelId);
        return ResponseEntity.ok(mappings);
    }

    /**
     * POST /v1/admin/hotels/{hotelId}/supplier-mapping
     * Create or update a supplier mapping for a hotel.
     */
    @PostMapping("/{hotelId}/supplier-mapping")
    public ResponseEntity<SupplierMappingResponse> upsertMapping(
            @PathVariable String hotelId,
            @Valid @RequestBody UpsertSupplierMappingRequest request) {
        SupplierMappingResponse response = service.upsert(hotelId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

