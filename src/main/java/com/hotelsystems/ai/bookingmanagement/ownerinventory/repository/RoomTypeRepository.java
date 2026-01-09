package com.hotelsystems.ai.bookingmanagement.ownerinventory.repository;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.RoomTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for RoomType entities.
 * Uses String id (roomTypeId) as primary key.
 */
@Repository
public interface RoomTypeRepository extends JpaRepository<RoomTypeEntity, String> {
    
    /**
     * Finds a room type by its id.
     * 
     * @param id Room type identifier
     * @return Optional containing RoomTypeEntity if found
     */
    Optional<RoomTypeEntity> findById(String id);
    
    /**
     * Finds all room types for a specific hotel.
     * 
     * @param hotelId Hotel identifier
     * @return List of room types for the hotel
     */
    List<RoomTypeEntity> findByHotelId(String hotelId);
    
    /**
     * Finds a room type by hotel id and name.
     * Used to check for duplicate room type names within the same hotel.
     * 
     * @param hotelId Hotel identifier
     * @param name Room type name
     * @return Optional containing RoomTypeEntity if found
     */
    Optional<RoomTypeEntity> findByHotelIdAndName(String hotelId, String name);
    
    /**
     * Checks if a room type exists with the given id.
     * 
     * @param id Room type identifier
     * @return true if room type exists, false otherwise
     */
    boolean existsById(String id);
    
    /**
     * Checks if a room type with the given name exists for a hotel.
     * Used to validate duplicate room type names within the same hotel.
     * 
     * @param hotelId Hotel identifier
     * @param name Room type name
     * @return true if room type with this name exists for the hotel, false otherwise
     */
    boolean existsByHotelIdAndName(String hotelId, String name);
}

