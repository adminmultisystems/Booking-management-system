package com.hotelsystems.ai.bookingmanagement.supplier.service;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierMappingResponse;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.UpsertSupplierMappingRequest;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingEntity;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import com.hotelsystems.ai.bookingmanagement.supplier.error.ConflictException;
import com.hotelsystems.ai.bookingmanagement.supplier.error.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.supplier.repo.SupplierHotelMappingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service for managing supplier hotel mappings.
 */
@Service
public class SupplierMappingService {

    private final SupplierHotelMappingRepository repository;

    public SupplierMappingService(SupplierHotelMappingRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all mappings for a hotel.
     * @param hotelId the hotel ID
     * @return list of mappings
     * @throws NotFoundException if no mappings exist
     */
    public List<SupplierMappingResponse> getMappings(String hotelId) {
        List<SupplierHotelMappingEntity> entities = repository.findByHotelId(hotelId);
        if (entities.isEmpty()) {
            throw new NotFoundException("No supplier mappings found for hotel: " + hotelId);
        }
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Find the first ACTIVE supplier mapping for a hotel.
     * Used by routing services to determine if hotel should route to supplier path.
     * 
     * @param hotelId the hotel ID
     * @return Optional containing the active mapping if found, empty otherwise
     */
    public Optional<SupplierHotelMappingEntity> findActiveMapping(String hotelId) {
        return repository.findFirstByHotelIdAndStatus(hotelId, SupplierMappingStatus.ACTIVE);
    }

    /**
     * Upsert a supplier mapping.
     * @param hotelId the hotel ID
     * @param request the upsert request
     * @return the saved mapping
     * @throws ConflictException if validation fails or conflict occurs
     */
    @Transactional
    public SupplierMappingResponse upsert(String hotelId, UpsertSupplierMappingRequest request) {
        // Validate: ACTIVE status requires supplierHotelId
        if (request.getStatus() == SupplierMappingStatus.ACTIVE) {
            if (!StringUtils.hasText(request.getSupplierHotelId())) {
                throw new ConflictException("supplierHotelId is required when status is ACTIVE");
            }
        }

        // Enforce single ACTIVE supplier per hotel
        if (request.getStatus() == SupplierMappingStatus.ACTIVE) {
            SupplierHotelMappingEntity existingActive = repository
                    .findFirstByHotelIdAndStatus(hotelId, SupplierMappingStatus.ACTIVE)
                    .orElse(null);
            
            if (existingActive != null && 
                !existingActive.getSupplierCode().equals(request.getSupplierCode())) {
                throw new ConflictException(
                    "Cannot set supplier " + request.getSupplierCode() + 
                    " as ACTIVE: hotel already has ACTIVE supplier " + existingActive.getSupplierCode()
                );
            }
        }

        // Find existing mapping or create new
        SupplierHotelMappingEntity entity = repository
                .findByHotelIdAndSupplierCode(hotelId, request.getSupplierCode())
                .orElse(new SupplierHotelMappingEntity(hotelId, request.getSupplierCode(), 
                        request.getSupplierHotelId(), request.getStatus()));

        // Update fields
        entity.setSupplierHotelId(request.getSupplierHotelId());
        entity.setStatus(request.getStatus());

        // Save
        SupplierHotelMappingEntity saved = repository.save(entity);
        return toResponse(saved);
    }

    private SupplierMappingResponse toResponse(SupplierHotelMappingEntity entity) {
        return new SupplierMappingResponse(
                entity.getHotelId(),
                entity.getSupplierCode(),
                entity.getSupplierHotelId(),
                entity.getStatus(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}

