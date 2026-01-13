package com.hotelsystems.ai.bookingmanagement.service.adapter.offer.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.hotelsystems.ai.bookingmanagement.dto.offer.MoneyDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OfferDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;
import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import com.hotelsystems.ai.bookingmanagement.enums.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferSearchAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierAdapterRegistry;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierOfferSearchAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierOfferDto;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierRecheckResultDto;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingEntity;
import com.hotelsystems.ai.bookingmanagement.supplier.service.SupplierMappingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

/**
 * Supplier Offer Adapter (Bridge)
 * 
 * Bridges between public API layer (OfferSearchAdapter/OfferRecheckAdapter) 
 * and supplier package adapters (SupplierAdapterRegistry).
 * 
 * Routes to correct supplier adapter based on active mapping and normalizes
 * supplier DTOs to public API DTOs.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierOfferAdapter implements OfferSearchAdapter, OfferRecheckAdapter {
    
    private final SupplierAdapterRegistry supplierAdapterRegistry;
    private final SupplierMappingService supplierMappingService;
    private final ObjectMapper objectMapper;
    
    @Override
    public List<OfferDto> searchOffers(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.debug("SupplierOfferAdapter.searchOffers - hotelId: {}, checkIn: {}, checkOut: {}, guests: {}, rooms: {}",
                hotelId, checkIn, checkOut, guests, roomsCount);
        
        // Step 1: Get active mapping
        Optional<SupplierHotelMappingEntity> mapping = supplierMappingService.findActiveMapping(hotelId);
        if (mapping.isEmpty()) {
            throw new IllegalStateException("No ACTIVE supplier mapping for hotelId=" + hotelId);
        }
        
        SupplierHotelMappingEntity mappingEntity = mapping.get();
        SupplierCode supplierCode = mappingEntity.getSupplierCode();
        String supplierHotelId = mappingEntity.getSupplierHotelId();
        
        log.debug("Found active mapping - hotelId: {}, supplierCode: {}, supplierHotelId: {}",
                hotelId, supplierCode, supplierHotelId);
        
        // Step 2: Determine occupancy safely
        int adults = (guests != null && guests > 0) ? guests : 2;
        int children = 0;
        int rooms = (roomsCount != null && roomsCount > 0) ? roomsCount : 1;
        
        // Step 3: Get supplier adapter
        SupplierOfferSearchAdapter adapter = supplierAdapterRegistry.getOfferSearchAdapter(supplierCode);
        
        // Step 4: Call supplier search
        List<SupplierOfferDto> supplierOffers = adapter.searchOffers(
                hotelId,
                supplierHotelId,
                checkIn,
                checkOut,
                adults,
                children,
                rooms
        );
        
        log.debug("Supplier adapter returned {} offers", supplierOffers.size());
        
        // Step 5: Normalize SupplierOfferDto -> OfferDto
        List<OfferDto> offers = new ArrayList<>();
        for (SupplierOfferDto supplierOffer : supplierOffers) {
            OfferDto offer = normalizeToOfferDto(supplierOffer, hotelId, checkIn, checkOut, mappingEntity);
            offers.add(offer);
        }
        
        log.debug("Normalized {} supplier offers to OfferDto", offers.size());
        return offers;
    }
    
    @Override
    public OffersRecheckResponse recheck(
            String offerId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount) {
        
        log.debug("SupplierOfferAdapter.recheck - offerId: {}, checkIn: {}, checkOut: {}",
                offerId, checkIn, checkOut);
        
        // Step 1: Validate offerId starts with "SUP-"
        if (offerId == null || !offerId.startsWith("SUP-")) {
            throw new IllegalArgumentException("OfferId must start with 'SUP-': " + offerId);
        }
        
        // Step 2: Parse offerId segments
        ParsedOfferIdSegments segments = parseOfferIdSegments(offerId);
        if (segments.supplierCode == null) {
            throw new IllegalArgumentException("Could not parse supplierCode from offerId: " + offerId);
        }
        
        // Step 3: Get supplier recheck adapter
        SupplierRecheckAdapter adapter = supplierAdapterRegistry.getRecheckAdapter(segments.supplierCode);
        
        // Step 4: Build offerPayloadJson string (SupplierRecheckRequest format)
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("supplierHotelId", segments.supplierHotelId);
        
        // Set rateKey (null if "NA")
        if (segments.rateKey != null && !segments.rateKey.equals("NA")) {
            payload.put("rateKey", segments.rateKey);
        } else {
            payload.putNull("rateKey");
        }
        
        // Set roomCode (null if "NA")
        if (segments.roomCode != null && !segments.roomCode.equals("NA")) {
            payload.put("roomCode", segments.roomCode);
        } else {
            payload.putNull("roomCode");
        }
        
        payload.put("checkIn", checkIn.toString());
        payload.put("checkOut", checkOut.toString());
        
        String offerPayloadJson = payload.toString();
        
        log.debug("Calling supplier recheck adapter with supplierCode: {}, supplierHotelId: {}, rateKey: {}, roomCode: {}",
                segments.supplierCode, segments.supplierHotelId, segments.rateKey, segments.roomCode);
        
        // Step 5: Call adapter.recheck(offerPayloadJson)
        SupplierRecheckResultDto recheckResult = adapter.recheck(offerPayloadJson);
        
        // Step 6: Map SupplierRecheckResultDto -> OffersRecheckResponse
        RecheckResult result = mapRecheckStatus(recheckResult.getStatus());
        
        OffersRecheckResponse.OffersRecheckResponseBuilder responseBuilder = OffersRecheckResponse.builder()
                .result(result)
                .message("Recheck completed: " + result);
        
        // If updated price exists, populate updated offer
        if (result == RecheckResult.OK || result == RecheckResult.PRICE_CHANGED) {
            OfferDto updatedOffer = OfferDto.builder()
                    .offerId(offerId)
                    .source(OfferSource.SUPPLIER)
                    .checkIn(checkIn)
                    .checkOut(checkOut)
                    .totalPrice(createMoneyDto(recheckResult.getNewTotalPriceNullable(), 
                            recheckResult.getCurrencyNullable()))
                    .build();
            responseBuilder.offer(updatedOffer);
        }
        
        return responseBuilder.build();
    }
    
    /**
     * Normalize SupplierOfferDto to OfferDto
     */
    private OfferDto normalizeToOfferDto(SupplierOfferDto supplierOffer, String hotelId,
                                        LocalDate checkIn, LocalDate checkOut,
                                        SupplierHotelMappingEntity mapping) {
        
        // Extract rateKey and roomCode from supplier offer
        String rateKey = safeJsonExtract(supplierOffer.getRawPayloadJson(), 
                "rateKey", "rate_key", "rateId", "bookingKey");
        String roomCode = safeJsonExtract(supplierOffer.getRawPayloadJson(), 
                "roomCode", "room_code", "roomId");
        
        // Generate offerId: SUP-{supplierCode}-{base64Url(supplierHotelId)}-{base64Url(rateKey)}-{base64Url(roomCode)}
        String encodedSupplierHotelId = base64UrlEncode(mapping.getSupplierHotelId());
        String encodedRateKey = (rateKey != null && !rateKey.isEmpty()) 
                ? base64UrlEncode(rateKey) 
                : base64UrlEncode("NA");
        String encodedRoomCode = (roomCode != null && !roomCode.isEmpty()) 
                ? base64UrlEncode(roomCode) 
                : base64UrlEncode("NA");
        
        String offerId = String.format("SUP-%s-%s-%s-%s",
                mapping.getSupplierCode(),
                encodedSupplierHotelId,
                encodedRateKey,
                encodedRoomCode);
        
        // Create payload JsonNode with all required fields
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("supplierCode", mapping.getSupplierCode().toString());
        payload.put("supplierHotelId", mapping.getSupplierHotelId());
        if (rateKey != null) {
            payload.put("rateKey", rateKey);
        } else {
            payload.putNull("rateKey");
        }
        if (roomCode != null) {
            payload.put("roomCode", roomCode);
        } else {
            payload.putNull("roomCode");
        }
        payload.put("currency", supplierOffer.getCurrency() != null ? supplierOffer.getCurrency() : "USD");
        payload.put("totalPrice", supplierOffer.getTotalPrice() != null 
                ? supplierOffer.getTotalPrice().doubleValue() 
                : 0.0);
        if (supplierOffer.getBoard() != null) {
            payload.put("board", supplierOffer.getBoard());
        } else {
            payload.putNull("board");
        }
        if (supplierOffer.getRoomName() != null) {
            payload.put("roomName", supplierOffer.getRoomName());
        } else {
            payload.putNull("roomName");
        }
        
        // Include perNightBreakdown if available
        if (supplierOffer.getPerNightBreakdown() != null) {
            JsonNode perNightBreakdown = objectMapper.valueToTree(supplierOffer.getPerNightBreakdown());
            payload.set("perNightBreakdown", perNightBreakdown);
        } else {
            payload.putNull("perNightBreakdown");
        }
        
        // Include taxesAndFees if available
        if (supplierOffer.getTaxesAndFees() != null) {
            JsonNode taxesAndFees = objectMapper.valueToTree(supplierOffer.getTaxesAndFees());
            payload.set("taxesAndFees", taxesAndFees);
        } else {
            payload.putNull("taxesAndFees");
        }
        
        // Include rawPayloadJson
        if (supplierOffer.getRawPayloadJson() != null) {
            payload.put("rawPayloadJson", supplierOffer.getRawPayloadJson());
        } else {
            payload.putNull("rawPayloadJson");
        }
        
        // Build OfferDto
        return OfferDto.builder()
                .offerId(offerId)
                .source(OfferSource.SUPPLIER)
                .hotelId(hotelId)
                .roomTypeId(supplierOffer.getRoomName() != null 
                        ? supplierOffer.getRoomName() 
                        : "SUPPLIER_ROOM")
                .checkIn(checkIn)
                .checkOut(checkOut)
                .totalPrice(createMoneyDto(supplierOffer.getTotalPrice(), supplierOffer.getCurrency()))
                .cancellationPolicySummary(supplierOffer.getCancellationSummary())
                .payload(payload)
                .build();
    }
    
    /**
     * Parsed offerId segments
     */
    private static class ParsedOfferIdSegments {
        SupplierCode supplierCode;
        String supplierHotelId;
        String rateKey;
        String roomCode;
        
        ParsedOfferIdSegments(SupplierCode supplierCode, String supplierHotelId, String rateKey, String roomCode) {
            this.supplierCode = supplierCode;
            this.supplierHotelId = supplierHotelId;
            this.rateKey = rateKey;
            this.roomCode = roomCode;
        }
    }
    
    /**
     * Parse offerId segments
     * Format: SUP-{supplierCode}-{base64Url(supplierHotelId)}-{base64Url(rateKey)}-{base64Url(roomCode)}
     * 
     * Note: Base64 URL-safe encoding may contain dashes, so we parse carefully:
     * - First segment after "SUP-" is supplierCode (known values: HOTELBEDS, TRAVELLANDA)
     * - Remaining segments are base64-encoded values separated by single dashes
     */
    private ParsedOfferIdSegments parseOfferIdSegments(String offerId) {
        if (offerId == null || !offerId.startsWith("SUP-")) {
            return new ParsedOfferIdSegments(null, null, null, null);
        }
        
        String withoutPrefix = offerId.substring(4); // Remove "SUP-"
        
        // Find supplierCode (first segment before dash)
        int firstDash = withoutPrefix.indexOf('-');
        if (firstDash < 0) {
            log.warn("Invalid offerId format, missing segments after supplierCode: {}", offerId);
            return new ParsedOfferIdSegments(null, null, null, null);
        }
        
        String supplierCodeStr = withoutPrefix.substring(0, firstDash);
        SupplierCode supplierCode;
        try {
            supplierCode = SupplierCode.valueOf(supplierCodeStr);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid supplierCode in offerId: {}", supplierCodeStr);
            return new ParsedOfferIdSegments(null, null, null, null);
        }
        
        // Find remaining segments (base64-encoded values)
        // Strategy: Find dashes that separate the segments
        // We know there should be exactly 3 more segments after supplierCode
        String remaining = withoutPrefix.substring(firstDash + 1);
        
        // Find second segment (supplierHotelId)
        int secondDash = remaining.indexOf('-');
        if (secondDash < 0) {
            log.warn("Invalid offerId format, missing rateKey segment: {}", offerId);
            return new ParsedOfferIdSegments(null, null, null, null);
        }
        String encodedSupplierHotelId = remaining.substring(0, secondDash);
        
        // Find third segment (rateKey)
        String afterSecond = remaining.substring(secondDash + 1);
        int thirdDash = afterSecond.indexOf('-');
        if (thirdDash < 0) {
            log.warn("Invalid offerId format, missing roomCode segment: {}", offerId);
            return new ParsedOfferIdSegments(null, null, null, null);
        }
        String encodedRateKey = afterSecond.substring(0, thirdDash);
        
        // Fourth segment (roomCode) is everything after third dash
        String encodedRoomCode = afterSecond.substring(thirdDash + 1);
        
        // Decode segments
        String supplierHotelId = base64UrlDecode(encodedSupplierHotelId);
        String rateKey = base64UrlDecode(encodedRateKey);
        String roomCode = base64UrlDecode(encodedRoomCode);
        
        // Handle "NA" values
        if ("NA".equals(rateKey)) {
            rateKey = null;
        }
        if ("NA".equals(roomCode)) {
            roomCode = null;
        }
        
        return new ParsedOfferIdSegments(supplierCode, supplierHotelId, rateKey, roomCode);
    }
    
    /**
     * Base64 URL-safe encode without padding
     */
    private String base64UrlEncode(String value) {
        if (value == null) {
            return base64UrlEncode("NA");
        }
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
    
    /**
     * Base64 URL-safe decode
     */
    private String base64UrlDecode(String encoded) {
        if (encoded == null || encoded.isEmpty()) {
            return null;
        }
        try {
            byte[] decoded = Base64.getUrlDecoder().decode(encoded);
            return new String(decoded, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            log.warn("Failed to decode base64Url string: {}", encoded);
            return null;
        }
    }
    
    /**
     * Safely extract value from JSON string using multiple possible keys
     * Returns first matching key's value, or null if none found
     */
    private String safeJsonExtract(String jsonString, String... keys) {
        if (jsonString == null || jsonString.isEmpty()) {
            return null;
        }
        
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonString);
            for (String key : keys) {
                if (jsonNode.has(key) && jsonNode.get(key).isTextual()) {
                    return jsonNode.get(key).asText();
                }
            }
        } catch (Exception e) {
            log.debug("Could not parse JSON for key extraction: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Map SupplierRecheckResultDto.RecheckStatus to RecheckResult enum
     */
    private RecheckResult mapRecheckStatus(SupplierRecheckResultDto.RecheckStatus status) {
        if (status == null) {
            return RecheckResult.OK; // Safe default
        }
        
        switch (status) {
            case OK:
                return RecheckResult.OK;
            case PRICE_CHANGED:
                return RecheckResult.PRICE_CHANGED;
            case SOLD_OUT:
                return RecheckResult.SOLD_OUT;
            default:
                log.warn("Unknown recheck status: {}, defaulting to OK", status);
                return RecheckResult.OK;
        }
    }
    
    /**
     * Create MoneyDto from nullable price and currency
     */
    private MoneyDto createMoneyDto(BigDecimal price, String currency) {
        BigDecimal amount = (price != null) ? price : BigDecimal.ZERO;
        String currencyCode = (currency != null && !currency.isEmpty()) ? currency : "USD";
        return MoneyDto.builder()
                .amount(amount)
                .currency(currencyCode)
                .build();
    }
}
