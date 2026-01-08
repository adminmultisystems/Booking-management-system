package com.hotelsystems.ai.bookingmanagement.ownerinventory.availability;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryReservationEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.ReservationStatus;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryAllotmentRepository;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.InventoryReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AvailabilityService.
 */
@DataJpaTest
@Import(AvailabilityService.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class AvailabilityServiceTest {
    
    @Autowired
    private InventoryAllotmentRepository allotmentRepository;
    
    @Autowired
    private InventoryReservationRepository reservationRepository;
    
    @Autowired
    private AvailabilityService availabilityService;
    
    private String hotelId;
    private String roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    
    @BeforeEach
    void setUp() {
        hotelId = "hotel-123";
        roomTypeId = "room-type-456";
        checkIn = LocalDate.of(2024, 1, 1);
        checkOut = LocalDate.of(2024, 1, 5); // 4 nights: Jan 1, 2, 3, 4
    }
    
    @Test
    void minAvailable_ReturnsCorrectValue_WhenAllotmentsExist() {
        // Given: Allotments exist for all nights with qty = 5
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 5, false);
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 5 (no reservations)
        assertEquals(5, minAvailable);
    }
    
    @Test
    void minAvailable_ReturnsZero_WhenAnyNightMissingAllotment() {
        // Given: Allotments exist for only some nights
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, false);
        // Missing checkIn.plusDays(2) and checkIn.plusDays(3)
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 0 (missing allotment)
        assertEquals(0, minAvailable);
    }
    
    @Test
    void minAvailable_ReturnsZero_WhenAnyNightStopSellTrue() {
        // Given: Allotments exist but one night has stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, true); // stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(2), 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(3), 5, false);
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 0 (stopSell = true)
        assertEquals(0, minAvailable);
    }
    
    @Test
    void minAvailable_AccountsForReservations() {
        // Given: Allotments exist with qty = 5, and one reservation for 2 rooms
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 5, false);
        
        UUID bookingId = UUID.randomUUID();
        createReservation(bookingId, hotelId, roomTypeId, checkIn, checkOut, 2);
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 3 (5 - 2 = 3)
        assertEquals(3, minAvailable);
    }
    
    @Test
    void minAvailable_ReturnsZero_WhenReservationsExceedCapacity() {
        // Given: Allotments exist with qty = 2, and one reservation for 3 rooms
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 2, false);
        
        UUID bookingId = UUID.randomUUID();
        createReservation(bookingId, hotelId, roomTypeId, checkIn, checkOut, 3);
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 0 (2 - 3 = -1, max(0, -1) = 0)
        assertEquals(0, minAvailable);
    }
    
    @Test
    void minAvailable_ReturnsMinimumAcrossAllNights() {
        // Given: Allotments with different quantities per night
        createAllotment(hotelId, roomTypeId, checkIn, 10, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(2), 8, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(3), 3, false);
        
        // When: Check min available
        int minAvailable = availabilityService.minAvailable(hotelId, roomTypeId, checkIn, checkOut);
        
        // Then: Returns 3 (minimum across all nights)
        assertEquals(3, minAvailable);
    }
    
    @Test
    void isBookable_ReturnsTrue_WhenSufficientAvailability() {
        // Given: Allotments exist with qty = 5
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 5, false);
        
        // When: Check if 2 rooms are bookable
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, 2);
        
        // Then: Returns true
        assertTrue(bookable);
    }
    
    @Test
    void isBookable_ReturnsFalse_WhenInsufficientAvailability() {
        // Given: Allotments exist with qty = 2, and one reservation for 1 room
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 2, false);
        
        UUID bookingId = UUID.randomUUID();
        createReservation(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // When: Check if 2 rooms are bookable
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, 2);
        
        // Then: Returns false (only 1 available, need 2)
        assertFalse(bookable);
    }
    
    @Test
    void isBookable_ReturnsFalse_WhenAnyNightMissingAllotment() {
        // Given: Allotments exist for only some nights
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        // Missing other nights
        
        // When: Check if 1 room is bookable
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // Then: Returns false (missing allotments)
        assertFalse(bookable);
    }
    
    @Test
    void isBookable_ReturnsFalse_WhenAnyNightStopSellTrue() {
        // Given: Allotments exist but one night has stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, true); // stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(2), 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(3), 5, false);
        
        // When: Check if 1 room is bookable
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // Then: Returns false (stopSell = true)
        assertFalse(bookable);
    }
    
    @Test
    void isBookable_ReturnsTrue_WhenExactCapacityAvailable() {
        // Given: Allotments exist with qty = 3
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 3, false);
        
        // When: Check if 3 rooms are bookable
        boolean bookable = availabilityService.isBookable(hotelId, roomTypeId, checkIn, checkOut, 3);
        
        // Then: Returns true (exact capacity)
        assertTrue(bookable);
    }
    
    // Helper methods
    
    private void createAllotments(String hotelId, String roomTypeId, LocalDate start, LocalDate end, int qty, boolean stopSell) {
        LocalDate current = start;
        while (current.isBefore(end)) {
            createAllotment(hotelId, roomTypeId, current, qty, stopSell);
            current = current.plusDays(1);
        }
    }
    
    private void createAllotment(String hotelId, String roomTypeId, LocalDate date, int qty, boolean stopSell) {
        InventoryAllotmentEntity allotment = new InventoryAllotmentEntity(hotelId, roomTypeId, date, qty);
        allotment.setStopSell(stopSell);
        allotmentRepository.save(allotment);
    }
    
    private void createReservation(UUID bookingId, String hotelId, String roomTypeId, 
                                  LocalDate checkIn, LocalDate checkOut, int roomsCount) {
        InventoryReservationEntity reservation = new InventoryReservationEntity(
            bookingId, hotelId, roomTypeId, checkIn, checkOut, roomsCount);
        reservation.setStatus(ReservationStatus.RESERVED);
        reservationRepository.save(reservation);
    }
}
