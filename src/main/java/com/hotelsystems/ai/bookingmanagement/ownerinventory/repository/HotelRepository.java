package com.hotelsystems.ai.bookingmanagement.ownerinventory.repository;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.HotelEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for Hotel entities.
 * Uses String id (hotelId) as primary key.
 */
@Repository
public interface HotelRepository extends JpaRepository<HotelEntity, String> {
    
    /**
     * Finds a hotel by its id.
     * 
     * @param id Hotel identifier
     * @return Optional containing HotelEntity if found
     */
    Optional<HotelEntity> findById(String id);
    
    /**
     * Checks if a hotel exists with the given id.
     * 
     * @param id Hotel identifier
     * @return true if hotel exists, false otherwise
     */
    boolean existsById(String id);
}

