package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PricingIntelligenceClient.
 * Tests that the client delegates to PricingIntelligenceService correctly.
 */
@SpringBootTest
class PricingIntelligenceClientTest {
    
    @Autowired
    private PricingIntelligenceClient client;
    
    private static final String HOTEL_ID = "hotel-123";
    private static final String ROOM_TYPE_ID_DELUXE = "DELUXE";
    private static final String ROOM_TYPE_ID_SUITE = "SUITE";
    private static final String ROOM_TYPE_ID_STANDARD = "STANDARD";
    private static final LocalDate CHECK_IN = LocalDate.of(2024, 2, 1);
    private static final LocalDate CHECK_OUT = LocalDate.of(2024, 2, 3);
    private static final int GUESTS = 2;
    private static final String CURRENCY = "INR";
    
    @Test
    void testGetQuote_DeluxeRoom_ReturnsCorrectPricing() {
        // Given: DELUXE room type (12000 per night), 2 nights
        // When: Client requests quote
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_DELUXE, CHECK_IN, CHECK_OUT, GUESTS, CURRENCY);
        
        // Then: Returns correct pricing (2 nights * 12000 = 24000)
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(24000L);
    }
    
    @Test
    void testGetQuote_SuiteRoom_ReturnsCorrectPricing() {
        // Given: SUITE room type (20000 per night), 2 nights
        // When: Client requests quote
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_SUITE, CHECK_IN, CHECK_OUT, GUESTS, CURRENCY);
        
        // Then: Returns correct pricing (2 nights * 20000 = 40000)
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(40000L);
    }
    
    @Test
    void testGetQuote_StandardRoom_ReturnsCorrectPricing() {
        // Given: STANDARD room type (8000 per night), 2 nights
        // When: Client requests quote
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_STANDARD, CHECK_IN, CHECK_OUT, GUESTS, CURRENCY);
        
        // Then: Returns correct pricing (2 nights * 8000 = 16000)
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(16000L);
    }
    
    @Test
    void testGetQuote_InvalidDateRange_ThrowsException() {
        // Given: checkOut is before or equal to checkIn
        LocalDate invalidCheckOut = CHECK_IN; // Same date
        
        // When/Then: Should throw ResponseStatusException
        assertThatThrownBy(() -> 
            client.getQuote(HOTEL_ID, ROOM_TYPE_ID_DELUXE, CHECK_IN, invalidCheckOut, GUESTS, CURRENCY))
            .isInstanceOf(org.springframework.web.server.ResponseStatusException.class);
    }
    
    @Test
    void testGetQuote_EmptyCurrency_UsesINRDefault() {
        // When: Client requests quote with empty currency
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_DELUXE, CHECK_IN, CHECK_OUT, GUESTS, "");
        
        // Then: Returns quote with INR default
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(24000L); // 2 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_NullCurrency_UsesINRDefault() {
        // When: Client requests quote with null currency
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_DELUXE, CHECK_IN, CHECK_OUT, GUESTS, null);
        
        // Then: Returns quote with INR default
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(24000L); // 2 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_ThreeNights_CalculatesCorrectly() {
        // Given: 3 nights stay with DELUXE room
        LocalDate checkOut = CHECK_IN.plusDays(3);
        
        // When: Client requests quote
        PricingQuote quote = client.getQuote(HOTEL_ID, ROOM_TYPE_ID_DELUXE, CHECK_IN, checkOut, GUESTS, "USD");
        
        // Then: Returns correct pricing (3 nights * 12000 = 36000)
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("USD");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(36000L);
    }
    
    @Test
    void testGetQuote_UnknownRoomType_UsesDefaultRate() {
        // Given: Unknown room type (should use default 10000 per night)
        // When: Client requests quote
        PricingQuote quote = client.getQuote(HOTEL_ID, "UNKNOWN_TYPE", CHECK_IN, CHECK_OUT, GUESTS, CURRENCY);
        
        // Then: Returns default pricing (2 nights * 10000 = 20000)
        assertThat(quote).isNotNull();
        assertThat(quote.getCurrency()).isEqualTo("INR");
        assertThat(quote.getTotalPriceMinor()).isEqualTo(20000L);
    }
}

