package com.hotelsystems.ai.bookingmanagement.ownerinventory.repository;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryReservationEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface InventoryReservationRepository extends JpaRepository<InventoryReservationEntity, UUID> {
    
    /**
     * Finds all reservations for a specific booking.
     */
    List<InventoryReservationEntity> findByBookingId(UUID bookingId);
    
    /**
     * Finds overlapping reservations for a hotel and room type.
     * This query finds reservations where:
     * - Status matches the provided status
     * - checkIn < provided checkOut (reservation starts before the query period ends)
     * - checkOut > provided checkIn (reservation ends after the query period starts)
     * 
     * This effectively finds all reservations that overlap with the given date range.
     */
    List<InventoryReservationEntity> findByHotelIdAndRoomTypeIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
        String hotelId,
        String roomTypeId,
        ReservationStatus status,
        LocalDate checkOut,
        LocalDate checkIn);
}

