package com.hotelsystems.ai.bookingmanagement.ownerinventory.repository;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.PricingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PricingRepository extends JpaRepository<PricingEntity, UUID> {
    
    Optional<PricingEntity> findByHotelIdAndRoomTypeIdAndDate(
        UUID hotelId, UUID roomTypeId, LocalDate date);
    
    List<PricingEntity> findByHotelIdAndRoomTypeIdAndDateBetween(
        UUID hotelId, UUID roomTypeId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT p FROM PricingEntity p WHERE p.hotelId = :hotelId " +
           "AND p.roomTypeId = :roomTypeId AND p.date >= :startDate AND p.date < :endDate " +
           "ORDER BY p.date")
    List<PricingEntity> findPricingForDateRange(
        @Param("hotelId") UUID hotelId,
        @Param("roomTypeId") UUID roomTypeId,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate);
}

