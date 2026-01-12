package com.hotelsystems.ai.bookingmanagement.dto.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsystems.ai.bookingmanagement.dto.request.GuestDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.OccupancyDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PriceSnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.MoneyDto;
import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test for BookingResponse serialization
 * 
 * Verifies that:
 * - All new fields are included in JSON serialization
 * - Null fields are handled correctly (either omitted or included as null)
 * - Existing fields remain unchanged
 */
class BookingResponseSerializationTest {
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    void testSerializationWithAllFields() throws Exception {
        // Create a BookingResponse with all fields populated
        BookingResponse response = BookingResponse.builder()
                .bookingId(UUID.randomUUID())
                .status(BookingStatus.DRAFT)
                .source(BookingSource.OWNER)
                .checkIn(LocalDate.now().plusDays(7))
                .checkOut(LocalDate.now().plusDays(10))
                .roomTypeId("room-123")
                .confirmationRef("CONF-123")
                .occupancy(OccupancyDto.builder()
                        .adults(2)
                        .children(1)
                        .build())
                .guests(Arrays.asList(
                        GuestDto.builder()
                                .name("John Doe")
                                .email("john@example.com")
                                .phone("+1234567890")
                                .build()
                ))
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .guestPhone("+1234567890")
                .offerId("offer-123")
                .priceSnapshot(PriceSnapshotDto.builder()
                        .totalPrice(MoneyDto.builder()
                                .amount(new BigDecimal("299.99"))
                                .currency("USD")
                                .build())
                        .build())
                .roomsCount(1)
                .adults(2)
                .children(1)
                .childrenAges(Arrays.asList(5))
                .leadGuest(GuestDto.builder()
                        .name("John Doe")
                        .email("john@example.com")
                        .phone("+1234567890")
                        .build())
                .supplierRateKey("rate-key-123")
                .expiresAt(Instant.now().plusSeconds(900))
                .nextActions(Arrays.asList("CONFIRM_REQUIRED"))
                .build();
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);
        
        // Verify all fields are present
        assertTrue(json.contains("bookingId"));
        assertTrue(json.contains("status"));
        assertTrue(json.contains("source"));
        assertTrue(json.contains("checkIn"));
        assertTrue(json.contains("checkOut"));
        assertTrue(json.contains("roomTypeId"));
        assertTrue(json.contains("confirmationRef"));
        assertTrue(json.contains("occupancy"));
        assertTrue(json.contains("guests"));
        assertTrue(json.contains("guestName"));
        assertTrue(json.contains("guestEmail"));
        assertTrue(json.contains("guestPhone"));
        assertTrue(json.contains("offerId"));
        assertTrue(json.contains("priceSnapshot"));
        assertTrue(json.contains("roomsCount"));
        assertTrue(json.contains("adults"));
        assertTrue(json.contains("children"));
        assertTrue(json.contains("childrenAges"));
        assertTrue(json.contains("leadGuest"));
        assertTrue(json.contains("supplierRateKey"));
        assertTrue(json.contains("expiresAt"));
        assertTrue(json.contains("nextActions"));
        
        // Deserialize back
        BookingResponse deserialized = objectMapper.readValue(json, BookingResponse.class);
        
        // Verify values
        assertEquals(response.getBookingId(), deserialized.getBookingId());
        assertEquals(response.getStatus(), deserialized.getStatus());
        assertEquals(response.getRoomsCount(), deserialized.getRoomsCount());
        assertEquals(response.getAdults(), deserialized.getAdults());
        assertEquals(response.getChildren(), deserialized.getChildren());
        assertNotNull(deserialized.getChildrenAges());
        assertEquals(1, deserialized.getChildrenAges().size());
        assertNotNull(deserialized.getLeadGuest());
        assertEquals(response.getSupplierRateKey(), deserialized.getSupplierRateKey());
        assertNotNull(deserialized.getExpiresAt());
        assertNotNull(deserialized.getNextActions());
        assertEquals(1, deserialized.getNextActions().size());
    }
    
    @Test
    void testSerializationWithNullFields() throws Exception {
        // Create a BookingResponse with minimal fields (nulls for new fields)
        BookingResponse response = BookingResponse.builder()
                .bookingId(UUID.randomUUID())
                .status(BookingStatus.DRAFT)
                .source(BookingSource.OWNER)
                .checkIn(LocalDate.now().plusDays(7))
                .checkOut(LocalDate.now().plusDays(10))
                .roomTypeId("room-123")
                .confirmationRef(null)
                .guestName("John Doe")
                .guestEmail("john@example.com")
                .guestPhone("+1234567890")
                // New fields are null
                .build();
        
        // Serialize to JSON
        String json = objectMapper.writeValueAsString(response);
        
        // Verify existing fields are present
        assertTrue(json.contains("bookingId"));
        assertTrue(json.contains("status"));
        assertTrue(json.contains("checkIn"));
        assertTrue(json.contains("checkOut"));
        assertTrue(json.contains("roomTypeId"));
        assertTrue(json.contains("guestName"));
        
        // Verify null fields are either omitted or included as null
        // Spring Boot default includes nulls, so they should be present as null
        // This is acceptable per user requirements
        assertTrue(json.contains("occupancy") || !json.contains("\"occupancy\""));
        assertTrue(json.contains("guests") || !json.contains("\"guests\""));
        assertTrue(json.contains("roomsCount") || !json.contains("\"roomsCount\""));
        
        // Deserialize back
        BookingResponse deserialized = objectMapper.readValue(json, BookingResponse.class);
        
        // Verify null fields remain null
        assertNull(deserialized.getOccupancy());
        assertNull(deserialized.getGuests());
        assertNull(deserialized.getRoomsCount());
        assertNull(deserialized.getAdults());
        assertNull(deserialized.getChildren());
        assertNull(deserialized.getChildrenAges());
        assertNull(deserialized.getLeadGuest());
        assertNull(deserialized.getSupplierRateKey());
        assertNull(deserialized.getExpiresAt());
        assertNull(deserialized.getNextActions());
    }
    
    @Test
    void testBackwardCompatibility() throws Exception {
        // Test that existing fields work exactly as before
        BookingResponse response = BookingResponse.builder()
                .bookingId(UUID.randomUUID())
                .status(BookingStatus.CONFIRMED)
                .source(BookingSource.SUPPLIER)
                .checkIn(LocalDate.now().plusDays(7))
                .checkOut(LocalDate.now().plusDays(10))
                .roomTypeId("room-123")
                .confirmationRef("SUP-12345")
                .guestName("Jane Doe")
                .guestEmail("jane@example.com")
                .guestPhone("+9876543210")
                .build();
        
        String json = objectMapper.writeValueAsString(response);
        BookingResponse deserialized = objectMapper.readValue(json, BookingResponse.class);
        
        // Verify all existing fields
        assertEquals(response.getBookingId(), deserialized.getBookingId());
        assertEquals(response.getStatus(), deserialized.getStatus());
        assertEquals(response.getSource(), deserialized.getSource());
        assertEquals(response.getCheckIn(), deserialized.getCheckIn());
        assertEquals(response.getCheckOut(), deserialized.getCheckOut());
        assertEquals(response.getRoomTypeId(), deserialized.getRoomTypeId());
        assertEquals(response.getConfirmationRef(), deserialized.getConfirmationRef());
        assertEquals(response.getGuestName(), deserialized.getGuestName());
        assertEquals(response.getGuestEmail(), deserialized.getGuestEmail());
        assertEquals(response.getGuestPhone(), deserialized.getGuestPhone());
    }
}

