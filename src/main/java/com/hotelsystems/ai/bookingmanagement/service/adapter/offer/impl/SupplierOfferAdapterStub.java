package com.hotelsystems.ai.bookingmanagement.service.adapter.offer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotelsystems.ai.bookingmanagement.dto.offer.MoneyDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OfferDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;
import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import com.hotelsystems.ai.bookingmanagement.enums.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferSearchAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stub Supplier Offer Adapter
 * 
 * Stub implementation for supplier offer search and recheck.
 * This will be replaced by engineers with real supplier API integration.
 * 
 * Returns offers with source=SUPPLIER, deterministic pricing.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SupplierOfferAdapterStub implements OfferSearchAdapter, OfferRecheckAdapter {
    
    private final ObjectMapper objectMapper;
    
    @Override
    public List<OfferDto> searchOffers(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.info("STUB: Searching supplier offers - hotelId: {}, checkIn: {}, checkOut: {}, guests: {}, rooms: {}",
                hotelId, checkIn, checkOut, guests, roomsCount);
        
        List<OfferDto> offers = new ArrayList<>();
        
        // Calculate deterministic price based on hotelId and dates
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal basePrice = calculateDeterministicPrice(hotelId, nights, roomsCount);
        
        // Offer 1: Standard Room
        String offerId1 = "SUP-" + generateDeterministicId(hotelId + "-standard-" + checkIn + "-" + checkOut);
        OfferDto offer1 = OfferDto.builder()
                .offerId(offerId1)
                .source(OfferSource.SUPPLIER)
                .hotelId(hotelId)
                .roomTypeId("ROOM-STANDARD")
                .checkIn(checkIn)
                .checkOut(checkOut)
                .totalPrice(MoneyDto.builder()
                        .amount(basePrice)
                        .currency("USD")
                        .build())
                .cancellationPolicySummary("Free cancellation up to 24 hours before check-in")
                .payload(createStubPayload("standard", basePrice))
                .build();
        offers.add(offer1);
        
        // Offer 2: Deluxe Room (if nights >= 3)
        if (nights >= 3) {
            String offerId2 = "SUP-" + generateDeterministicId(hotelId + "-deluxe-" + checkIn + "-" + checkOut);
            BigDecimal deluxePrice = basePrice.multiply(BigDecimal.valueOf(1.3)); // 30% more
            OfferDto offer2 = OfferDto.builder()
                    .offerId(offerId2)
                    .source(OfferSource.SUPPLIER)
                    .hotelId(hotelId)
                    .roomTypeId("ROOM-DELUXE")
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .totalPrice(MoneyDto.builder()
                            .amount(deluxePrice)
                            .currency("USD")
                            .build())
                    .cancellationPolicySummary("Free cancellation up to 48 hours before check-in")
                    .payload(createStubPayload("deluxe", deluxePrice))
                    .build();
            offers.add(offer2);
        }
        
        log.info("STUB: Found {} supplier offers", offers.size());
        return offers;
    }
    
    @Override
    public OffersRecheckResponse recheck(
            String offerId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.info("STUB: Rechecking supplier offer - offerId: {}, checkIn: {}, checkOut: {}",
                offerId, checkIn, checkOut);
        
        // Stub: Always return OK with updated offer
        // Extract hotelId from offerId pattern or use a default
        String hotelId = extractHotelIdFromOfferId(offerId);
        long nights = java.time.temporal.ChronoUnit.DAYS.between(checkIn, checkOut);
        BigDecimal price = calculateDeterministicPrice(hotelId, nights, roomsCount);
        
        OfferDto updatedOffer = OfferDto.builder()
                .offerId(offerId)
                .source(OfferSource.SUPPLIER)
                .hotelId(hotelId)
                .roomTypeId("ROOM-STANDARD")
                .checkIn(checkIn)
                .checkOut(checkOut)
                .totalPrice(MoneyDto.builder()
                        .amount(price)
                        .currency("USD")
                        .build())
                .cancellationPolicySummary("Free cancellation up to 24 hours before check-in")
                .payload(createStubPayload("standard", price))
                .build();
        
        return OffersRecheckResponse.builder()
                .result(RecheckResult.OK)
                .offer(updatedOffer)
                .message("STUB: Supplier offer recheck successful")
                .build();
    }
    
    /**
     * Calculate deterministic price based on hotelId and stay duration
     */
    private BigDecimal calculateDeterministicPrice(String hotelId, long nights, int roomsCount) {
        // Deterministic: hash hotelId to get base price
        int hash = hotelId.hashCode();
        BigDecimal basePricePerNight = BigDecimal.valueOf(100 + (Math.abs(hash) % 200)); // $100-$300 per night
        return basePricePerNight.multiply(BigDecimal.valueOf(nights)).multiply(BigDecimal.valueOf(roomsCount));
    }
    
    /**
     * Generate deterministic ID from input string
     */
    private String generateDeterministicId(String input) {
        // Use hash of input to create deterministic UUID-like string
        int hash = input.hashCode();
        return String.format("%08x", Math.abs(hash));
    }
    
    /**
     * Extract hotelId from offerId (stub implementation)
     */
    private String extractHotelIdFromOfferId(String offerId) {
        // Stub: return default or extract from pattern
        if (offerId != null && offerId.startsWith("SUP-")) {
            return "HOTEL-SUPPLIER-001"; // Default stub hotel
        }
        return "HOTEL-SUPPLIER-001";
    }
    
    /**
     * Create stub payload JSON
     */
    private com.fasterxml.jackson.databind.JsonNode createStubPayload(String roomType, BigDecimal price) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("roomType", roomType);
        payload.put("price", price.doubleValue());
        payload.put("currency", "USD");
        payload.put("supplierCode", "STUB");
        return payload;
    }
}

