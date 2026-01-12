package com.hotelsystems.ai.bookingmanagement.ownerinventory.reservation;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryAllotmentEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.InventoryReservationEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.ReservationStatus;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.exception.ConflictException;
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
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ReservationService.
 */
@DataJpaTest
@Import({ReservationService.class, com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService.class})
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
class ReservationServiceTest {
    
    @Autowired
    private InventoryAllotmentRepository allotmentRepository;
    
    @Autowired
    private InventoryReservationRepository reservationRepository;
    
    @Autowired
    private ReservationService reservationService;
    
    private String hotelId;
    private String roomTypeId;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private UUID bookingId;
    
    @BeforeEach
    void setUp() {
        hotelId = "hotel-123";
        roomTypeId = "room-type-456";
        checkIn = LocalDate.of(2024, 1, 1);
        checkOut = LocalDate.of(2024, 1, 5); // 4 nights: Jan 1, 2, 3, 4
        bookingId = UUID.randomUUID();
    }
    
    @Test
    void reserve_Succeeds_WhenAllotmentsExist_AndQtySufficient_AndStopSellFalse() {
        // Given: Allotments exist for all nights with qty >= 1 and stopSell = false
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 5, false);
        
        // When: Reserve 1 room
        UUID reservationId = reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // Then: Reservation is created
        assertNotNull(reservationId);
        
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertEquals(1, reservations.size());
        
        InventoryReservationEntity reservation = reservations.get(0);
        assertEquals(bookingId, reservation.getBookingId());
        assertEquals(hotelId, reservation.getHotelId());
        assertEquals(roomTypeId, reservation.getRoomTypeId());
        assertEquals(checkIn, reservation.getCheckIn());
        assertEquals(checkOut, reservation.getCheckOut());
        assertEquals(1, reservation.getRoomsCount());
        assertEquals(ReservationStatus.RESERVED, reservation.getStatus());
    }
    
    @Test
    void reserve_Fails_WhenAnyNightMissingAllotment() {
        // Given: Allotments exist for only some nights (missing one night)
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(2), 5, false);
        // Missing checkIn.plusDays(3)
        
        // When/Then: Reserve should fail with ConflictException
        assertThrows(ConflictException.class, () -> {
            reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        });
        
        // Verify no reservation was created
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertTrue(reservations.isEmpty());
    }
    
    @Test
    void reserve_Fails_WhenAnyNightStopSellTrue() {
        // Given: Allotments exist but one night has stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn, 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(1), 5, true); // stopSell = true
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(2), 5, false);
        createAllotment(hotelId, roomTypeId, checkIn.plusDays(3), 5, false);
        
        // When/Then: Reserve should fail with ConflictException
        assertThrows(ConflictException.class, () -> {
            reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        });
        
        // Verify no reservation was created
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertTrue(reservations.isEmpty());
    }
    
    @Test
    void reserve_Fails_WhenReservedCountConsumesCapacity() {
        // Given: Allotments exist with qty = 2, and one reservation already exists for 2 rooms
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 2, false);
        
        UUID existingBookingId = UUID.randomUUID();
        createReservation(existingBookingId, hotelId, roomTypeId, checkIn, checkOut, 2);
        
        // When/Then: Reserve 1 more room should fail (2 + 1 > 2 capacity)
        assertThrows(ConflictException.class, () -> {
            reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        });
        
        // Verify no new reservation was created
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertTrue(reservations.isEmpty());
    }
    
    @Test
    void reserve_Succeeds_WhenReservedCountDoesNotExceedCapacity() {
        // Given: Allotments exist with qty = 3, and one reservation exists for 1 room
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 3, false);
        
        UUID existingBookingId = UUID.randomUUID();
        createReservation(existingBookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // When: Reserve 1 more room (1 + 1 = 2 <= 3 capacity)
        UUID reservationId = reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        
        // Then: Reservation is created
        assertNotNull(reservationId);
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertEquals(1, reservations.size());
    }
    
    @Test
    void releaseByBookingId_IsIdempotent() {
        // Given: A reservation exists
        createAllotments(hotelId, roomTypeId, checkIn, checkOut, 5, false);
        reservationService.reserve(bookingId, hotelId, roomTypeId, checkIn, checkOut, 1);
        
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertEquals(1, reservations.size());
        assertEquals(ReservationStatus.RESERVED, reservations.get(0).getStatus());
        
        // When: Release first time
        reservationService.releaseByBookingId(bookingId);
        
        // Then: Status is RELEASED
        reservations = reservationRepository.findByBookingId(bookingId);
        assertEquals(1, reservations.size());
        assertEquals(ReservationStatus.RELEASED, reservations.get(0).getStatus());
        
        // When: Release second time (idempotent call)
        reservationService.releaseByBookingId(bookingId);
        
        // Then: Still RELEASED (no error, no change)
        reservations = reservationRepository.findByBookingId(bookingId);
        assertEquals(1, reservations.size());
        assertEquals(ReservationStatus.RELEASED, reservations.get(0).getStatus());
    }
    
    @Test
    void releaseByBookingId_NoOp_WhenNoReservationsExist() {
        // Given: No reservations exist for bookingId
        
        // When: Release
        reservationService.releaseByBookingId(bookingId);
        
        // Then: No error, no-op
        List<InventoryReservationEntity> reservations = reservationRepository.findByBookingId(bookingId);
        assertTrue(reservations.isEmpty());
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
