package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuoteRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for PricingIntelligenceInternalController.
 * Tests the internal pricing REST endpoint.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PricingIntelligenceInternalControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Test
    void testGetQuote_WithMissingCurrency_ShouldDefaultToINR() throws Exception {
        // Test 1: checkIn=2024-02-01, checkOut=2024-02-03 (2 nights), currency missing
        // Room type: DELUXE = 12000 per night
        // Expect: currency="INR", totalPriceMinor=2*12000=24000
        
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("DELUXE");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency(null); // Missing currency
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("INR"))
                .andExpect(jsonPath("$.totalPriceMinor").value(24000L)); // 2 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_WithEmptyCurrency_ShouldDefaultToINR() throws Exception {
        // Test with empty currency string
        // Room type: DELUXE = 12000 per night
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("DELUXE");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency(""); // Empty currency
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("INR"))
                .andExpect(jsonPath("$.totalPriceMinor").value(24000L)); // 2 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_WithProvidedCurrency_ShouldUseProvidedCurrency() throws Exception {
        // Test with provided currency
        // Room type: DELUXE = 12000 per night
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("DELUXE");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency("USD");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.totalPriceMinor").value(24000L)); // 2 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_CheckInAfterCheckOut_ShouldReturn400() throws Exception {
        // Test 2: checkIn >= checkOut returns 400
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("deluxe-room");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 3));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 1)); // checkOut before checkIn
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_CheckInEqualsCheckOut_ShouldReturn400() throws Exception {
        // Test: checkIn == checkOut should return 400
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("deluxe-room");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 1)); // Same date
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_MissingHotelId_ShouldReturn400() throws Exception {
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId(null); // Missing hotelId
        request.setRoomTypeId("deluxe-room");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_MissingRoomTypeId_ShouldReturn400() throws Exception {
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId(null); // Missing roomTypeId
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_MissingCheckIn_ShouldReturn400() throws Exception {
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("deluxe-room");
        request.setCheckIn(null); // Missing checkIn
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3));
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_MissingCheckOut_ShouldReturn400() throws Exception {
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("deluxe-room");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(null); // Missing checkOut
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
    
    @Test
    void testGetQuote_NightsCalculation_ShouldBeCorrect() throws Exception {
        // Test nights calculation: 1 night with DELUXE (12000 per night)
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("DELUXE");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 2)); // 1 night
        request.setGuests(1);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPriceMinor").value(12000L)); // 1 night * 12000 (DELUXE)
        
        // Test nights calculation: 5 nights with DELUXE
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 6)); // 5 nights
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPriceMinor").value(60000L)); // 5 nights * 12000 (DELUXE)
    }
    
    @Test
    void testGetQuote_RoomTypeBasedPricing() throws Exception {
        // Test SUITE room type (20000 per night)
        PricingQuoteRequest request = new PricingQuoteRequest();
        request.setHotelId("hotel-123");
        request.setRoomTypeId("SUITE");
        request.setCheckIn(java.time.LocalDate.of(2024, 2, 1));
        request.setCheckOut(java.time.LocalDate.of(2024, 2, 3)); // 2 nights
        request.setGuests(2);
        request.setCurrency("INR");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPriceMinor").value(40000L)); // 2 nights * 20000 (SUITE)
        
        // Test STANDARD room type (8000 per night)
        request.setRoomTypeId("STANDARD");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPriceMinor").value(16000L)); // 2 nights * 8000 (STANDARD)
        
        // Test default room type (10000 per night for unknown types)
        request.setRoomTypeId("UNKNOWN_TYPE");
        
        mockMvc.perform(post("/v1/internal/pricing/quote")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPriceMinor").value(20000L)); // 2 nights * 10000 (default)
    }
}

