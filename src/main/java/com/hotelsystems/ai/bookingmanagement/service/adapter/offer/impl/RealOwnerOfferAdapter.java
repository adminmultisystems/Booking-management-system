package com.hotelsystems.ai.bookingmanagement.service.adapter.offer.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotelsystems.ai.bookingmanagement.dto.offer.MoneyDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OfferDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;
import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import com.hotelsystems.ai.bookingmanagement.enums.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.RoomTypeEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing.PricingIntelligenceClient;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.RoomTypeRepository;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferSearchAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Real Owner Offer Adapter
 * 
 * Real implementation for owner offer search and recheck using database.
 * Fetches real room types, checks actual inventory availability, and uses pricing service.
 */
@Component
@Primary
@RequiredArgsConstructor
@Slf4j
public class RealOwnerOfferAdapter implements OfferSearchAdapter, OfferRecheckAdapter {
    
    private final RoomTypeRepository roomTypeRepository;
    private final AvailabilityService availabilityService;
    private final PricingIntelligenceClient pricingClient;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<OfferDto> searchOffers(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.info("Searching owner offers from database - hotelId: {}, checkIn: {}, checkOut: {}, guests: {}, rooms: {}",
                hotelId, checkIn, checkOut, guests, roomsCount);
        
        // Default roomsCount to 1 if not provided
        int rooms = (roomsCount != null && roomsCount > 0) ? roomsCount : 1;
        
        // Fetch all active room types for this hotel from database
        List<RoomTypeEntity> roomTypes = roomTypeRepository.findByHotelId(hotelId);
        
        if (roomTypes.isEmpty()) {
            log.warn("No room types found for hotel: {}", hotelId);
            return new ArrayList<>();
        }
        
        List<OfferDto> offers = new ArrayList<>();
        
        // For each room type, check availability and create offer if available
        for (RoomTypeEntity roomType : roomTypes) {
            // Skip inactive room types
            if (!roomType.isActive()) {
                log.debug("Skipping inactive room type: {}", roomType.getId());
                continue;
            }
            
            // Check if room type has enough capacity for guests
            if (roomType.getMaxGuests() != null && guests != null && roomType.getMaxGuests() < guests) {
                log.debug("Room type {} maxGuests ({}) is less than requested guests ({})", 
                        roomType.getId(), roomType.getMaxGuests(), guests);
                continue;
            }
            
            // Check actual availability using AvailabilityService
            boolean isAvailable = availabilityService.isBookable(
                    hotelId, 
                    roomType.getId(), 
                    checkIn, 
                    checkOut, 
                    rooms
            );
            
            if (!isAvailable) {
                log.debug("Room type {} not available for dates {} to {}", 
                        roomType.getId(), checkIn, checkOut);
                continue;
            }
            
            // Get pricing from PricingIntelligenceClient
            long nights = ChronoUnit.DAYS.between(checkIn, checkOut);
            com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote pricingQuote;
            try {
                pricingQuote = pricingClient.getQuote(
                        hotelId,
                        roomType.getId(),
                        checkIn,
                        checkOut,
                        guests != null ? guests : 1,
                        "USD"
                );
            } catch (Exception e) {
                log.warn("Failed to get pricing quote for room type {}, using default: {}", 
                        roomType.getId(), e.getMessage());
                // Use default pricing if pricing service fails
                pricingQuote = new com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote(
                        "USD", 
                        nights * 10000L // Default 100 per night in minor units
                );
            }
            
            // Convert pricing from minor units to BigDecimal
            BigDecimal totalPrice = BigDecimal.valueOf(pricingQuote.getTotalPriceMinor())
                    .divide(BigDecimal.valueOf(100)); // Convert from minor units (cents) to dollars
            
            // Generate deterministic offer ID
            String offerId = "OWN-" + generateOfferId(hotelId, roomType.getId(), checkIn, checkOut);
            
            // Create offer
            OfferDto offer = OfferDto.builder()
                    .offerId(offerId)
                    .source(OfferSource.OWNER)
                    .hotelId(hotelId)
                    .roomTypeId(roomType.getId())
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .totalPrice(MoneyDto.builder()
                            .amount(totalPrice)
                            .currency(pricingQuote.getCurrency())
                            .build())
                    .cancellationPolicySummary("Free cancellation up to 48 hours before check-in")
                    .payload(createPayload(roomType, totalPrice, pricingQuote.getCurrency()))
                    .build();
            
            offers.add(offer);
            log.debug("Created offer for room type {}: {}", roomType.getId(), offerId);
        }
        
        log.info("Found {} available offers for hotel: {}", offers.size(), hotelId);
        return offers;
    }
    
    @Override
    public OffersRecheckResponse recheck(
            String offerId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.info("Rechecking owner offer from database - offerId: {}, checkIn: {}, checkOut: {}",
                offerId, checkIn, checkOut);
        
        // Default roomsCount
        int rooms = (roomsCount != null && roomsCount > 0) ? roomsCount : 1;
        
        // Extract hash from offerId (format: OWN-{hash})
        if (offerId == null || !offerId.startsWith("OWN-")) {
            log.warn("Invalid offerId format: {}", offerId);
            return OffersRecheckResponse.builder()
                    .result(RecheckResult.SOLD_OUT)
                    .message("Invalid offer ID format")
                    .build();
        }
        
        // Extract offer identifier (remove "OWN-" prefix)
        // Note: Currently not used for lookup, but kept for future use
        // In production, you'd store offer metadata or use a different ID format
        @SuppressWarnings("unused")
        String offerIdentifier = offerId.substring(4);
        
        // Try to find the offer by checking all room types
        // This is not optimal but works for MVP
        List<RoomTypeEntity> allRoomTypes = roomTypeRepository.findAll();
        
        for (RoomTypeEntity roomType : allRoomTypes) {
            if (!roomType.isActive()) {
                continue;
            }
            
            // Check if guests fit
            if (roomType.getMaxGuests() != null && guests != null && roomType.getMaxGuests() < guests) {
                continue;
            }
            
            // Try to regenerate offerId and see if it matches
            String testOfferId = "OWN-" + generateOfferId(roomType.getHotelId(), roomType.getId(), checkIn, checkOut);
            
            if (testOfferId.equals(offerId)) {
                // Found matching offer! Now check availability
                boolean isAvailable = availabilityService.isBookable(
                        roomType.getHotelId(),
                        roomType.getId(),
                        checkIn,
                        checkOut,
                        rooms
                );
                
                if (!isAvailable) {
                    log.info("Offer {} no longer available", offerId);
                    return OffersRecheckResponse.builder()
                            .result(RecheckResult.SOLD_OUT)
                            .message("Room is sold out or no longer available for the requested dates")
                            .build();
                }
                
                // Get updated pricing
                com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote pricingQuote;
                try {
                    pricingQuote = pricingClient.getQuote(
                            roomType.getHotelId(),
                            roomType.getId(),
                            checkIn,
                            checkOut,
                            guests != null ? guests : 1,
                            "USD"
                    );
                } catch (Exception e) {
                    log.warn("Failed to get pricing quote: {}", e.getMessage());
                    pricingQuote = new com.hotelsystems.ai.bookingmanagement.ownerinventory.dto.PricingQuote(
                            "USD",
                            ChronoUnit.DAYS.between(checkIn, checkOut) * 10000L
                    );
                }
                
                BigDecimal totalPrice = BigDecimal.valueOf(pricingQuote.getTotalPriceMinor())
                        .divide(BigDecimal.valueOf(100));
                
                // Build updated offer
                OfferDto updatedOffer = OfferDto.builder()
                        .offerId(offerId)
                        .source(OfferSource.OWNER)
                        .hotelId(roomType.getHotelId())
                        .roomTypeId(roomType.getId())
                        .checkIn(checkIn)
                        .checkOut(checkOut)
                        .totalPrice(MoneyDto.builder()
                                .amount(totalPrice)
                                .currency(pricingQuote.getCurrency())
                                .build())
                        .cancellationPolicySummary("Free cancellation up to 48 hours before check-in")
                        .payload(createPayload(roomType, totalPrice, pricingQuote.getCurrency()))
                        .build();
                
                log.info("Offer {} recheck successful - still available", offerId);
                return OffersRecheckResponse.builder()
                        .result(RecheckResult.OK)
                        .offer(updatedOffer)
                        .message("Offer is still available")
                        .build();
            }
        }
        
        // Offer not found
        log.warn("Offer {} not found in database", offerId);
        return OffersRecheckResponse.builder()
                .result(RecheckResult.SOLD_OUT)
                .message("Offer not found or no longer available")
                .build();
    }
    
    /**
     * Generate deterministic offer ID
     */
    private String generateOfferId(String hotelId, String roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        String input = hotelId + "-" + roomTypeId + "-" + checkIn + "-" + checkOut;
        int hash = input.hashCode();
        return String.format("%08x", Math.abs(hash));
    }
    
    /**
     * Create payload JSON for offer
     */
    private com.fasterxml.jackson.databind.JsonNode createPayload(
            RoomTypeEntity roomType, 
            BigDecimal price, 
            String currency) {
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("roomTypeId", roomType.getId());
        payload.put("roomTypeName", roomType.getName());
        payload.put("maxGuests", roomType.getMaxGuests() != null ? roomType.getMaxGuests() : 0);
        payload.put("price", price.doubleValue());
        payload.put("currency", currency);
        payload.put("source", "OWNER");
        return payload;
    }
}

