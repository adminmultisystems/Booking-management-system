package com.hotelsystems.ai.bookingmanagement.supplier.adapter.stub;

import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierBookingAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierOfferSearchAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.*;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Stub implementation of Hotelbeds adapters for Phase-1.
 */
@Component
public class HotelbedsStubAdapters implements SupplierOfferSearchAdapter, SupplierRecheckAdapter, SupplierBookingAdapter {

    @Override
    public List<SupplierOfferDto> searchOffers(String hotelId, String supplierHotelId, 
                                               LocalDate checkIn, LocalDate checkOut, 
                                               int adults, int children, int rooms) {
        // Deterministic: derive from hotelId + checkIn
        int basePrice = Math.abs((hotelId + checkIn.toString()).hashCode() % 1000) + 100;
        int numOffers = (Math.abs(hotelId.hashCode()) % 3) + 1; // 1-3 offers
        
        List<SupplierOfferDto> offers = new ArrayList<>();
        for (int i = 0; i < numOffers; i++) {
            SupplierOfferDto offer = new SupplierOfferDto();
            offer.setOfferId("HB-OFFER-" + hotelId + "-" + checkIn + "-" + i);
            offer.setSupplierCode(SupplierCode.HOTELBEDS);
            offer.setSupplierHotelId(supplierHotelId);
            offer.setRoomName("Standard Room " + (i + 1));
            offer.setBoard("Breakfast Included");
            
            BigDecimal totalPrice = BigDecimal.valueOf(basePrice + (i * 50));
            offer.setTotalPrice(totalPrice);
            offer.setCurrency("USD");
            
            // Per-night breakdown
            List<PerNightRateDto> perNightBreakdown = new ArrayList<>();
            LocalDate current = checkIn;
            while (current.isBefore(checkOut)) {
                BigDecimal nightPrice = BigDecimal.valueOf(basePrice / 2 + (i * 25));
                perNightBreakdown.add(new PerNightRateDto(current, nightPrice));
                current = current.plusDays(1);
            }
            offer.setPerNightBreakdown(perNightBreakdown);
            
            offer.setCancellationSummary("Free cancellation until 24 hours before check-in");
            
            TaxesAndFeesPlaceholderDto taxesAndFees = new TaxesAndFeesPlaceholderDto(
                    true, 
                    BigDecimal.valueOf(basePrice * 0.1), 
                    "Taxes and fees included"
            );
            offer.setTaxesAndFees(taxesAndFees);
            
            // Raw payload
            try {
                String rawPayload = String.format(
                    "{\"supplier\":\"HOTELBEDS\",\"hotelId\":\"%s\",\"checkIn\":\"%s\",\"checkOut\":\"%s\",\"price\":%s}",
                    supplierHotelId, checkIn, checkOut, totalPrice
                );
                offer.setRawPayloadJson(rawPayload);
            } catch (Exception e) {
                offer.setRawPayloadJson("{}");
            }
            
            offers.add(offer);
        }
        
        return offers;
    }

    @Override
    public SupplierRecheckResultDto recheck(String offerPayloadJson) {
        // Check for force flags in JSON
        if (offerPayloadJson != null) {
            if (offerPayloadJson.contains("\"forceSoldOut\":true") || 
                offerPayloadJson.contains("\"forceSoldOut\": true")) {
                return new SupplierRecheckResultDto(
                    SupplierRecheckResultDto.RecheckStatus.SOLD_OUT,
                    null,
                    null
                );
            }
            
            if (offerPayloadJson.contains("\"forcePriceChange\":true") || 
                offerPayloadJson.contains("\"forcePriceChange\": true")) {
                // Extract old price and add 10
                try {
                    // Simple extraction - in real implementation would use proper JSON parsing
                    String priceStr = offerPayloadJson.replaceAll(".*\"price\":(\\d+).*", "$1");
                    BigDecimal oldPrice = new BigDecimal(priceStr);
                    BigDecimal newPrice = oldPrice.add(BigDecimal.TEN);
                    return new SupplierRecheckResultDto(
                        SupplierRecheckResultDto.RecheckStatus.PRICE_CHANGED,
                        newPrice,
                        "USD"
                    );
                } catch (Exception e) {
                    // Default price change
                    return new SupplierRecheckResultDto(
                        SupplierRecheckResultDto.RecheckStatus.PRICE_CHANGED,
                        BigDecimal.valueOf(100),
                        "USD"
                    );
                }
            }
        }
        
        // Default: OK
        return new SupplierRecheckResultDto(
            SupplierRecheckResultDto.RecheckStatus.OK,
            null,
            null
        );
    }

    @Override
    public SupplierBookResponse createBooking(String offerPayloadJson, String guestPayloadJson) {
        // Generate deterministic booking ref
        String shortRandom = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String supplierBookingRef = "HB-BOOK-" + shortRandom;
        
        String rawPayload = String.format(
            "{\"bookingRef\":\"%s\",\"status\":\"CONFIRMED\",\"offer\":%s,\"guest\":%s}",
            supplierBookingRef, offerPayloadJson, guestPayloadJson
        );
        
        return new SupplierBookResponse(
            supplierBookingRef,
            SupplierBookResponse.BookingStatus.CONFIRMED,
            rawPayload
        );
    }

    @Override
    public void cancelBooking(String supplierBookingRef) {
        // No-op success
    }
}

