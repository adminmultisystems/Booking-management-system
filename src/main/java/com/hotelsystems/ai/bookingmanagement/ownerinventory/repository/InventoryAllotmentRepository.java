package com.hotelsystems.ai.bookingmanagement.ownerinventory.repository;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryAllotmentRepository extends JpaRepository<InventoryAllotmentEntity, UUID> {
    
    /**
     * Finds a single allotment by hotel, room type, and date.
     */
    Optional<InventoryAllotmentEntity> findByHotelIdAndRoomTypeIdAndDate(
        String hotelId, String roomTypeId, LocalDate date);
    
    /**
     * Finds allotments for a date range.
     * Uses Spring Data JPA method naming convention.
     */
    List<InventoryAllotmentEntity> findByHotelIdAndRoomTypeIdAndDateBetween(
        String hotelId, String roomTypeId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds allotments for a date range with pessimistic write lock.
     * This ensures exclusive access during concurrent reservation operations.
     * Locks rows for hotelId + roomTypeId where date >= checkIn and date < checkOut.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT ia FROM InventoryAllotmentEntity ia " +
           "WHERE ia.hotelId = :hotelId " +
           "AND ia.roomTypeId = :roomTypeId " +
           "AND ia.date >= :checkIn " +
           "AND ia.date < :checkOut " +
           "ORDER BY ia.date")
    List<InventoryAllotmentEntity> findLockedAllotmentsForDateRange(
        @Param("hotelId") String hotelId,
        @Param("roomTypeId") String roomTypeId,
        @Param("checkIn") LocalDate checkIn,
        @Param("checkOut") LocalDate checkOut);
    
    /**
     * Finds allotments for a hotel and optional room type within a date range.
     */
    @Query("SELECT ia FROM InventoryAllotmentEntity ia " +
           "WHERE ia.hotelId = :hotelId " +
           "AND (:roomTypeId IS NULL OR ia.roomTypeId = :roomTypeId) " +
           "AND ia.date >= :startDate " +
           "AND ia.date < :endDate " +
           "ORDER BY ia.roomTypeId, ia.date")
    List<InventoryAllotmentEntity> findByHotelIdAndOptionalRoomTypeIdAndDateRange(
        @Param("hotelId") String hotelId,
        @Param("roomTypeId") String roomTypeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}

