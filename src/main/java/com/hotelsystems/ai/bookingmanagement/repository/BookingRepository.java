package com.hotelsystems.ai.bookingmanagement.repository;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Booking Repository
 * 
 * JPA repository for BookingEntity data access operations.
 */
@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, UUID> {
    
    List<BookingEntity> findByUserId(String userId);
    
    List<BookingEntity> findByHotelId(String hotelId);
    
    List<BookingEntity> findByStatus(BookingStatus status);
    
    List<BookingEntity> findBySource(com.hotelsystems.ai.bookingmanagement.enums.BookingSource source);
    
    List<BookingEntity> findByUserIdAndStatus(String userId, BookingStatus status);
    
    /**
     * Find booking by userId and idempotencyKey
     * Used for idempotency checks
     */
    Optional<BookingEntity> findByUserIdAndIdempotencyKey(String userId, String idempotencyKey);
    
    @Query("SELECT b FROM BookingEntity b WHERE b.hotelId = :hotelId " +
           "AND b.status IN :statuses " +
           "AND ((b.checkIn <= :checkInDate AND b.checkOut > :checkInDate) OR " +
           "(b.checkIn < :checkOutDate AND b.checkOut >= :checkOutDate) OR " +
           "(b.checkIn >= :checkInDate AND b.checkOut <= :checkOutDate))")
    List<BookingEntity> findOverlappingBookings(
            @Param("hotelId") String hotelId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<BookingStatus> statuses);
    
    @Query("SELECT b FROM BookingEntity b WHERE b.hotelId = :hotelId " +
           "AND b.roomTypeId = :roomTypeId " +
           "AND b.status IN :statuses " +
           "AND ((b.checkIn <= :checkInDate AND b.checkOut > :checkInDate) OR " +
           "(b.checkIn < :checkOutDate AND b.checkOut >= :checkOutDate) OR " +
           "(b.checkIn >= :checkInDate AND b.checkOut <= :checkOutDate))")
    List<BookingEntity> findOverlappingRoomBookings(
            @Param("hotelId") String hotelId,
            @Param("roomTypeId") String roomTypeId,
            @Param("checkInDate") LocalDate checkInDate,
            @Param("checkOutDate") LocalDate checkOutDate,
            @Param("statuses") List<BookingStatus> statuses);
}
