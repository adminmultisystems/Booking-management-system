package com.hotelsystems.ai.bookingmanagement.ownerinventory.reservation;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryReservationEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.ReservationStatus;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.ConflictException;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryAllotmentRepository;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Service for managing inventory reservations.
 * Handles reservation and release operations with proper locking and validation.
 */
@Service
public class ReservationService {
    
    private final InventoryAllotmentRepository allotmentRepository;
    private final InventoryReservationRepository reservationRepository;
    private final AvailabilityService availabilityService;
    
    public ReservationService(
            InventoryAllotmentRepository allotmentRepository,
            InventoryReservationRepository reservationRepository,
            AvailabilityService availabilityService) {
        this.allotmentRepository = allotmentRepository;
        this.reservationRepository = reservationRepository;
        this.availabilityService = availabilityService;
    }
    
    /**
     * Reserves inventory for a booking.
     * 
     * Flow:
     * 1) Lock allotment rows for date range using PESSIMISTIC_WRITE query
     * 2) Compute isBookable(...) after lock
     * 3) If not bookable -> throw ConflictException("Insufficient owner inventory")
     * 4) Insert InventoryReservationEntity(status=RESERVED)
     * 5) Return reservationId
     * 
     * Note: If allotments are missing for any night, it's treated as not bookable.
     * 
     * @param bookingId Booking identifier
     * @param hotelId Hotel identifier
     * @param roomTypeId Room type identifier
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     * @param roomsCount Number of rooms to reserve
     * @return UUID of the created reservation
     * @throws ConflictException if insufficient inventory is available
     */
    @Transactional
    public UUID reserve(UUID bookingId, String hotelId, String roomTypeId, 
                       LocalDate checkIn, LocalDate checkOut, int roomsCount) {
        // Step 1: Lock allotment rows for date range using PESSIMISTIC_WRITE
        // This ensures exclusive access during concurrent reservation operations
        allotmentRepository.findLockedAllotmentsForDateRange(hotelId, roomTypeId, checkIn, checkOut);
        
        // Step 2: Compute isBookable(...) after lock
        // This checks availability including missing allotments and stopSell flags
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, roomsCount);
        
        // Step 3: If not bookable -> throw ConflictException
        if (!bookable) {
            throw new ConflictException("Insufficient owner inventory");
        }
        
        // Step 4: Insert InventoryReservationEntity(status=RESERVED)
        InventoryReservationEntity reservation = new InventoryReservationEntity(
            bookingId, hotelId, roomTypeId, checkIn, checkOut, roomsCount);
        reservation.setStatus(ReservationStatus.RESERVED);
        
        InventoryReservationEntity savedReservation = reservationRepository.save(reservation);
        
        // Step 5: Return reservationId
        return savedReservation.getId();
    }
    
    /**
     * Releases inventory reservations for a booking.
     * 
     * Flow:
     * - Find by bookingId
     * - If none -> no-op
     * - If already RELEASED -> no-op (idempotent)
     * - Else set RELEASED and save
     * 
     * @param bookingId Booking identifier
     */
    @Transactional
    public void releaseByBookingId(UUID bookingId) {
        // Find all reservations for this booking
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        
        // If none -> no-op
        if (reservations.isEmpty()) {
            return;
        }
        
        // Process each reservation
        for (InventoryReservationEntity reservation : reservations) {
            // If already RELEASED -> no-op (idempotent)
            if (reservation.getStatus() == ReservationStatus.RELEASED) {
                continue;
            }
            
            // Else set RELEASED and save
            reservation.setStatus(ReservationStatus.RELEASED);
            reservationRepository.save(reservation);
        }
    }
    
    /**
     * Reserves inventory for UUID-based hotel and room type.
     * 
     * @param hotelId Hotel identifier (UUID)
     * @param roomTypeId Room type identifier (UUID)
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     * @return true if reservation was successful, false otherwise
     */
    @Transactional
    public boolean reserveInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        try {
            UUID bookingId = UUID.randomUUID(); // Temporary booking ID
            reserve(bookingId, hotelId.toString(), roomTypeId.toString(), checkIn, checkOut, 1);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Releases inventory for UUID-based hotel and room type.
     * 
     * @param hotelId Hotel identifier (UUID)
     * @param roomTypeId Room type identifier (UUID)
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     */
    @Transactional
    public void releaseInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // This method signature is for compatibility with OwnerInventoryAdapter
        // Actual release should be done by bookingId, which is handled in releaseByBookingId
        // This is a placeholder implementation
    }
}

