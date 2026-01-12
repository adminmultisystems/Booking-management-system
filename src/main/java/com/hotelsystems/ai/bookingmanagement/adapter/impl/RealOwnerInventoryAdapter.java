package com.hotelsystems.ai.bookingmanagement.adapter.impl;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.reservation.ReservationService;
import com.hotelsystems.ai.bookingmanagement.service.adapter.OwnerInventoryAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Real implementation of OwnerInventoryAdapter.
 * This adapter integrates with the owner inventory services to provide
 * real inventory checking, reservation, and release functionality.
 * 
 * This replaces the StubOwnerInventoryAdapter and provides actual integration
 * with the owner inventory system (AvailabilityService and ReservationService).
 */
@Component
@Primary
@Slf4j
public class RealOwnerInventoryAdapter implements OwnerInventoryAdapter {
    
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;
    
    public RealOwnerInventoryAdapter(
            AvailabilityService availabilityService,
            ReservationService reservationService) {
        this.availabilityService = availabilityService;
        this.reservationService = reservationService;
    }
    
    /**
     * Recheck booking availability and validity with owner inventory.
     * 
     * @param booking Booking entity to recheck
     * @return RecheckResult with status and details
     */
    @Override
    public RecheckResult recheck(BookingEntity booking) {
        log.info("Rechecking owner inventory - bookingId: {}, hotelId: {}, roomTypeId: {}",
                booking.getId(), booking.getHotelId(), booking.getRoomTypeId());
        
        // Extract booking details
        String hotelId = booking.getHotelId();
        String roomTypeId = booking.getRoomTypeId();
        java.time.LocalDate checkIn = booking.getCheckIn();
        java.time.LocalDate checkOut = booking.getCheckOut();
        
        // Validate required fields
        if (hotelId == null || hotelId.trim().isEmpty() ||
            roomTypeId == null || roomTypeId.trim().isEmpty() ||
            checkIn == null || checkOut == null) {
            log.warn("Invalid booking data for recheck - bookingId: {}", booking.getId());
            return RecheckResult.builder()
                    .status(RecheckStatus.SOLD_OUT)
                    .message("Invalid booking data: missing hotelId, roomTypeId, or dates")
                    .build();
        }
        
        // Get roomsCount from booking (default to 1 if not specified)
        int roomsCount = (booking.getRoomsCount() != null && booking.getRoomsCount() > 0) 
                ? booking.getRoomsCount() 
                : 1;
        
        // Check if bookable using AvailabilityService
        boolean bookable = availabilityService.isBookable(
                hotelId, roomTypeId, checkIn, checkOut, roomsCount);
        
        if (!bookable) {
            log.warn("Owner inventory not available - bookingId: {}, hotelId: {}, roomTypeId: {}, checkIn: {}, checkOut: {}",
                    booking.getId(), hotelId, roomTypeId, checkIn, checkOut);
            return RecheckResult.builder()
                    .status(RecheckStatus.SOLD_OUT)
                    .message("Room is sold out or no longer available for the requested dates")
                    .build();
        }
        
        log.info("Owner inventory available - bookingId: {}", booking.getId());
        return RecheckResult.builder()
                .status(RecheckStatus.OK)
                .message("Owner inventory recheck successful - room is available")
                .build();
    }
    
    /**
     * Reserve and confirm booking with owner inventory.
     * 
     * @param booking Booking entity to reserve and confirm
     * @return Internal confirmation reference in format "OWN-RES-{reservationId}"
     */
    @Override
    public String reserveAndConfirm(BookingEntity booking) {
        log.info("Reserving and confirming owner inventory - bookingId: {}, hotelId: {}, roomTypeId: {}",
                booking.getId(), booking.getHotelId(), booking.getRoomTypeId());
        
        // Extract booking details
        UUID bookingId = booking.getId();
        String hotelId = booking.getHotelId();
        String roomTypeId = booking.getRoomTypeId();
        java.time.LocalDate checkIn = booking.getCheckIn();
        java.time.LocalDate checkOut = booking.getCheckOut();
        
        // Validate required fields
        if (hotelId == null || hotelId.trim().isEmpty() ||
            roomTypeId == null || roomTypeId.trim().isEmpty() ||
            checkIn == null || checkOut == null) {
            throw new IllegalArgumentException("Invalid booking data: missing hotelId, roomTypeId, or dates");
        }
        
        // Get roomsCount from booking (default to 1 if not specified)
        int roomsCount = (booking.getRoomsCount() != null && booking.getRoomsCount() > 0) 
                ? booking.getRoomsCount() 
                : 1;
        
        // Reserve inventory using ReservationService
        UUID reservationId = reservationService.reserve(
                bookingId,
                hotelId,
                roomTypeId,
                checkIn,
                checkOut,
                roomsCount
        );
        
        // Return confirmation reference in format "OWN-RES-{reservationId}"
        String confirmationRef = "OWN-RES-" + reservationId;
        log.info("Owner inventory reserved and confirmed - bookingId: {}, confirmationRef: {}", 
                bookingId, confirmationRef);
        
        return confirmationRef;
    }
    
    /**
     * Release booking from owner inventory.
     * 
     * @param booking Booking entity to release
     */
    @Override
    public void release(BookingEntity booking) {
        log.info("Releasing owner inventory - bookingId: {}, confirmationRef: {}",
                booking.getId(), booking.getInternalConfirmationRef());
        
        UUID bookingId = booking.getId();
        
        if (bookingId == null) {
            log.warn("Cannot release inventory - bookingId is null");
            return;
        }
        
        // Release inventory using ReservationService
        reservationService.releaseByBookingId(bookingId);
        
        log.info("Owner inventory released - bookingId: {}", bookingId);
    }
}

