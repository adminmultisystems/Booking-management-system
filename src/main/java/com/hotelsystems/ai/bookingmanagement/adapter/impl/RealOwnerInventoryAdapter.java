package com.hotelsystems.ai.bookingmanagement.adapter.impl;

import com.hotelsystems.ai.bookingmanagement.adapter.OwnerInventoryAdapter;
import com.hotelsystems.ai.bookingmanagement.adapter.RecheckStatus;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.reservation.ReservationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Real implementation of OwnerInventoryAdapter.
 * This adapter integrates with the owner inventory services to provide
 * inventory checking, reservation, and release functionality.
 */
@Service
@Primary
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
     * Rechecks inventory availability for a booking.
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return RecheckStatus indicating if the booking is still available
     */
    public RecheckStatus recheck(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // Validate hotelId, roomTypeId, checkIn, checkOut present
        if (hotelId == null || roomTypeId == null || checkIn == null || checkOut == null) {
            return RecheckStatus.SOLD_OUT;
        }
        
        // roomsCount = 1 for MVP
        int roomsCount = 1;
        
        // Convert UUID to String for service calls
        String hotelIdStr = hotelId.toString();
        String roomTypeIdStr = roomTypeId.toString();
        
        // Check if bookable
        boolean bookable = availabilityService.isBookable(
            hotelIdStr, roomTypeIdStr, checkIn, checkOut, roomsCount);
        
        // If not bookable -> return SOLD_OUT
        if (!bookable) {
            return RecheckStatus.SOLD_OUT;
        }
        
        // Else return OK
        return RecheckStatus.OK;
    }
    
    /**
     * Reserves inventory and confirms the reservation.
     * 
     * @param bookingId The booking identifier
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return Reservation confirmation ID in format "OWN-RES-{reservationId}"
     */
    public String reserveAndConfirm(UUID bookingId, UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // Convert UUID to String for service calls
        String hotelIdStr = hotelId.toString();
        String roomTypeIdStr = roomTypeId.toString();
        
        // Reserve inventory
        UUID reservationId = reservationService.reserve(
            bookingId,
            hotelIdStr,
            roomTypeIdStr,
            checkIn,
            checkOut,
            1 // roomsCount = 1 for MVP
        );
        
        // Return "OWN-RES-" + reservationId
        return "OWN-RES-" + reservationId;
    }
    
    /**
     * Releases inventory for a booking.
     * 
     * @param bookingId The booking identifier
     */
    public void release(UUID bookingId) {
        reservationService.releaseByBookingId(bookingId);
    }
    
    // Implementation of OwnerInventoryAdapter interface methods
    // These methods delegate to the appropriate services
    
    @Override
    public boolean isAvailable(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return availabilityService.isBookable(
            hotelId.toString(), roomTypeId.toString(), checkIn, checkOut, 1);
    }
    
    @Override
    public boolean reserveInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        try {
            // Generate a temporary booking ID for reservation
            UUID bookingId = UUID.randomUUID();
            String confirmationId = reserveAndConfirm(bookingId, hotelId, roomTypeId, checkIn, checkOut);
            return confirmationId != null && !confirmationId.isEmpty();
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public void releaseInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // Note: This method requires bookingId to release, but we don't have it here.
        // This is a limitation - in a real implementation, you'd need to track bookingId.
        // For now, this method cannot fully release without bookingId.
        // Consider using a different approach or storing bookingId mapping.
    }
    
    @Override
    public BigDecimal calculatePrice(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // TODO: Implement pricing calculation using PricingService
        // For now, return zero
        return BigDecimal.ZERO;
    }
    
    @Override
    public BigDecimal getPriceForDate(UUID hotelId, UUID roomTypeId, LocalDate date) {
        // TODO: Implement per-date pricing using PricingService
        // For now, return zero
        return BigDecimal.ZERO;
    }
}

