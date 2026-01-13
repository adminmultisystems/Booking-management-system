package com.hotelsystems.ai.bookingmanagement.ownerinventory.service;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.RoomTypeUpdateRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.HotelEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.RoomTypeEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.DuplicateException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.HotelRepository;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.RoomTypeRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing room types.
 */
@Service
public class RoomTypeService {
    
    private final RoomTypeRepository roomTypeRepository;
    private final HotelRepository hotelRepository;
    private final EntityManager entityManager;
    
    public RoomTypeService(RoomTypeRepository roomTypeRepository, HotelRepository hotelRepository, EntityManager entityManager) {
        this.roomTypeRepository = roomTypeRepository;
        this.hotelRepository = hotelRepository;
        this.entityManager = entityManager;
    }
    
    /**
     * Creates a new room type for a hotel.
     * 
     * @param hotelId Hotel identifier
     * @param request Room type creation request
     * @return Created room type response
     * @throws NotFoundException if hotel not found
     * @throws DuplicateException if room type with same id or name already exists for the hotel
     */
    @Transactional
    public RoomTypeResponse createRoomType(String hotelId, RoomTypeRequest request) {
        // Check if hotel exists
        HotelEntity hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new NotFoundException("Hotel with id '" + hotelId + "' not found"));
        
        // Check if hotel is active (optional business rule - can be removed if not needed)
        if (!hotel.isActive()) {
            throw new IllegalArgumentException("Cannot create room type for inactive hotel");
        }
        
        // Check if room type with this id already exists for this hotel
        if (roomTypeRepository.existsById(request.getId())) {
            throw new DuplicateException("Room type with id '" + request.getId() + "' already exists");
        }
        
        // Check if room type with this name already exists for this hotel
        if (roomTypeRepository.existsByHotelIdAndName(hotelId, request.getName())) {
            throw new DuplicateException("Room type with name '" + request.getName() + "' already exists for hotel '" + hotelId + "'");
        }
        
        // Create new room type entity
        RoomTypeEntity roomType = new RoomTypeEntity(
            request.getId(),
            hotelId,
            request.getName(),
            request.getMaxGuests()
        );
        
        RoomTypeEntity savedRoomType = roomTypeRepository.save(roomType);
        
        // Convert to response DTO
        return toResponse(savedRoomType);
    }
    
    /**
     * Updates an existing room type.
     * 
     * @param roomTypeId Room type identifier
     * @param request Update request with optional fields
     * @return Updated room type response
     * @throws NotFoundException if room type not found
     * @throws DuplicateException if updating name would create a duplicate
     */
    @Transactional
    public RoomTypeResponse updateRoomType(String roomTypeId, RoomTypeUpdateRequest request) {
        // Find room type or throw NotFoundException
        RoomTypeEntity roomType = roomTypeRepository.findById(roomTypeId)
            .orElseThrow(() -> new NotFoundException("Room type with id '" + roomTypeId + "' not found"));
        
        // Update name if provided
        if (request.getName() != null) {
            // Check if new name would create a duplicate
            if (roomTypeRepository.existsByHotelIdAndName(roomType.getHotelId(), request.getName())) {
                // Check if it's a different room type (not just updating the same one)
                RoomTypeEntity existingWithName = roomTypeRepository.findByHotelIdAndName(
                    roomType.getHotelId(), request.getName()).orElse(null);
                if (existingWithName != null && !existingWithName.getId().equals(roomTypeId)) {
                    throw new DuplicateException("Room type with name '" + request.getName() + "' already exists for hotel '" + roomType.getHotelId() + "'");
                }
            }
            roomType.setName(request.getName());
        }
        
        // Update maxGuests if provided
        if (request.getMaxGuests() != null) {
            roomType.setMaxGuests(request.getMaxGuests());
        }
        
        // Update active if provided
        if (request.getActive() != null) {
            roomType.setActive(request.getActive());
        }
        
        // Save updated room type
        RoomTypeEntity updatedRoomType = roomTypeRepository.save(roomType);
        
        // Explicitly flush to ensure changes are written to database immediately
        roomTypeRepository.flush();
        
        // Refresh entity to get the latest state from database (including updated_at timestamp from @PreUpdate)
        entityManager.refresh(updatedRoomType);
        
        // Convert to response DTO
        return toResponse(updatedRoomType);
    }
    
    /**
     * Converts RoomTypeEntity to RoomTypeResponse DTO.
     */
    private RoomTypeResponse toResponse(RoomTypeEntity entity) {
        RoomTypeResponse response = new RoomTypeResponse();
        response.setId(entity.getId());
        response.setHotelId(entity.getHotelId());
        response.setName(entity.getName());
        response.setMaxGuests(entity.getMaxGuests());
        response.setActive(entity.isActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

