package com.hotelsystems.ai.bookingmanagement.ownerinventory.availability;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryReservationEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.ReservationStatus;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryAllotmentRepository;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service for calculating availability based on allotments and reservations.
 * Nights are calculated as [checkIn, checkOut) - checkIn inclusive, checkOut exclusive.
 */
@Service
public class AvailabilityService {
    
    private final InventoryAllotmentRepository allotmentRepository;
    private final InventoryReservationRepository reservationRepository;
    
    public AvailabilityService(
            InventoryAllotmentRepository allotmentRepository,
            InventoryReservationRepository reservationRepository) {
        this.allotmentRepository = allotmentRepository;
        this.reservationRepository = reservationRepository;
    }
    
    /**
     * Returns the minimum available rooms across all nights in the date range.
     * 
     * @param hotelId Hotel identifier
     * @param roomTypeId Room type identifier
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     * @return Minimum available rooms across all nights, or 0 if any night is unavailable
     */
    @Transactional(readOnly = true)
    public int minAvailable(String hotelId, String roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        // Load allotments for the date range [checkIn, checkOut)
        List<InventoryAllotmentEntity> allotments = allotmentRepository
            .findByHotelIdAndRoomTypeIdAndDateBetween(hotelId, roomTypeId, checkIn, checkOut.minusDays(1));
        
        // Create a map for quick lookup: date -> allotment
        Map<LocalDate, InventoryAllotmentEntity> allotmentMap = allotments.stream()
            .collect(Collectors.toMap(InventoryAllotmentEntity::getDate, a -> a));
        
        // Load all overlapping reservations with RESERVED status
        List<InventoryReservationEntity> reservations = reservationRepository
            .findByHotelIdAndRoomTypeIdAndStatusAndCheckInLessThanAndCheckOutGreaterThan(
                hotelId, roomTypeId, ReservationStatus.RESERVED, checkOut, checkIn);
        
        int minAvailable = Integer.MAX_VALUE;
        
        // Iterate through each night [checkIn, checkOut)
        LocalDate currentDate = checkIn;
        while (currentDate.isBefore(checkOut)) {
            final LocalDate night = currentDate; // Make effectively final for lambda
            InventoryAllotmentEntity allotment = allotmentMap.get(night);
            
            // Rule: If allotment row is missing -> availability = 0
            if (allotment == null) {
                return 0;
            }
            
            // Rule: If stopSell = true -> availability = 0
            if (allotment.isStopSell()) {
                return 0;
            }
            
            // Calculate reservedCount: sum of roomsCount from RESERVED reservations that overlap this night
            int reservedCount = reservations.stream()
                .filter(reservation -> overlapsNight(reservation, night))
                .mapToInt(InventoryReservationEntity::getRoomsCount)
                .sum();
            
            // Calculate availableNight = max(0, allotmentQty - reservedCount)
            int availableNight = Math.max(0, allotment.getAllotmentQty() - reservedCount);
            
            minAvailable = Math.min(minAvailable, availableNight);
            
            currentDate = currentDate.plusDays(1);
        }
        
        return minAvailable == Integer.MAX_VALUE ? 0 : minAvailable;
    }
    
    /**
     * Checks if the requested number of rooms can be booked for the given date range.
     * 
     * @param hotelId Hotel identifier
     * @param roomTypeId Room type identifier
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     * @param roomsCount Number of rooms requested
     * @return true if the requested number of rooms is available for all nights, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isBookable(String hotelId, String roomTypeId, LocalDate checkIn, LocalDate checkOut, int roomsCount) {
        int minAvailable = minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        return minAvailable >= roomsCount;
    }
    
    /**
     * Checks if rooms are available for UUID-based hotel and room type.
     * 
     * @param hotelId Hotel identifier (UUID)
     * @param roomTypeId Room type identifier (UUID)
     * @param checkIn Check-in date (inclusive)
     * @param checkOut Check-out date (exclusive)
     * @return true if rooms are available, false otherwise
     */
    @Transactional(readOnly = true)
    public boolean isAvailable(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return isBookable(hotelId.toString(), roomTypeId.toString(), checkIn, checkOut, 1);
    }
    
    /**
     * Checks if a reservation overlaps a specific night.
     * A reservation overlaps a night if: reservation.checkIn <= night < reservation.checkOut
     * 
     * @param reservation The reservation to check
     * @param night The night date to check
     * @return true if the reservation overlaps the night, false otherwise
     */
    private boolean overlapsNight(InventoryReservationEntity reservation, LocalDate night) {
        return !night.isBefore(reservation.getCheckIn()) && night.isBefore(reservation.getCheckOut());
    }
}

