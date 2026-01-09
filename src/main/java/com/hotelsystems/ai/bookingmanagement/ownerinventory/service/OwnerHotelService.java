package com.hotelsystems.ai.bookingmanagement.ownerinventory.service;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.HotelUpdateRequest;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.HotelEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.DuplicateException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.HotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing hotels.
 */
@Service
public class OwnerHotelService {
    
    private final HotelRepository hotelRepository;
    
    public OwnerHotelService(HotelRepository hotelRepository) {
        this.hotelRepository = hotelRepository;
    }
    
    /**
     * Creates a new hotel.
     * 
     * @param request Hotel creation request
     * @return Created hotel response
     * @throws DuplicateException if hotel with the same id already exists
     */
    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        // Check if hotel with this id already exists
        if (hotelRepository.existsById(request.getId())) {
            throw new DuplicateException("Hotel with id '" + request.getId() + "' already exists");
        }
        
        // Create new hotel entity
        HotelEntity hotel = new HotelEntity(request.getId(), request.getName());
        HotelEntity savedHotel = hotelRepository.save(hotel);
        
        // Convert to response DTO
        return toResponse(savedHotel);
    }
    
    /**
     * Updates an existing hotel.
     * 
     * @param hotelId Hotel identifier
     * @param request Update request with optional fields
     * @return Updated hotel response
     * @throws NotFoundException if hotel not found
     */
    @Transactional
    public HotelResponse updateHotel(String hotelId, HotelUpdateRequest request) {
        // Find hotel or throw NotFoundException
        HotelEntity hotel = hotelRepository.findById(hotelId)
            .orElseThrow(() -> new NotFoundException("Hotel with id '" + hotelId + "' not found"));
        
        // Update only provided fields
        if (request.getName() != null) {
            hotel.setName(request.getName());
        }
        if (request.getActive() != null) {
            hotel.setActive(request.getActive());
        }
        
        // Save updated hotel
        HotelEntity updatedHotel = hotelRepository.save(hotel);
        
        // Convert to response DTO
        return toResponse(updatedHotel);
    }
    
    /**
     * Converts HotelEntity to HotelResponse DTO.
     */
    private HotelResponse toResponse(HotelEntity entity) {
        HotelResponse response = new HotelResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setActive(entity.isActive());
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        return response;
    }
}

