package com.hotelsystems.ai.bookingmanagement.supplier.controller.debug;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierAdapterRegistry;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierBookingAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierOfferSearchAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.adapter.SupplierRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierBookResponse;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierOfferDto;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierRecheckResultDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Debug controller for testing Phase-1 stub supplier lifecycle.
 * Enabled only in "dev" profile and protected with X-Debug-Key header.
 */
@Profile("dev")
@RestController
@RequestMapping("/internal/suppliers")
public class SupplierStubDebugController {

    private static final String DEBUG_KEY_HEADER = "X-Debug-Key";

    private final SupplierAdapterRegistry adapterRegistry;
    private final ObjectMapper objectMapper;

    @Value("${debug.key:local-debug}")
    private String requiredDebugKey;

    public SupplierStubDebugController(SupplierAdapterRegistry adapterRegistry, ObjectMapper objectMapper) {
        this.adapterRegistry = adapterRegistry;
        this.objectMapper = objectMapper;
    }

    /**
     * Validates the X-Debug-Key header.
     * Returns true if valid, false otherwise.
     */
    private boolean validateDebugKey(String providedKey) {
        if (requiredDebugKey == null || requiredDebugKey.isEmpty()) {
            return false;
        }
        return requiredDebugKey.equals(providedKey);
    }

    /**
     * Interceptor method to check debug key before processing requests.
     */
    private ResponseEntity<?> checkDebugKey(String debugKey) {
        if (debugKey == null || debugKey.isEmpty()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Missing X-Debug-Key header"));
        }
        if (!validateDebugKey(debugKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Invalid X-Debug-Key header"));
        }
        return null; // Valid - continue processing
    }

    /**
     * POST /internal/suppliers/offers/search
     * Search for offers from a supplier.
     */
    @PostMapping("/offers/search")
    public ResponseEntity<?> searchOffers(
            @RequestHeader(value = DEBUG_KEY_HEADER, required = false) String debugKey,
            @RequestBody SearchOffersRequest request) {
        ResponseEntity<?> authError = checkDebugKey(debugKey);
        if (authError != null) {
            return authError;
        }

        try {
            SupplierOfferSearchAdapter adapter = adapterRegistry.getOfferSearchAdapter(request.getSupplierCode());
            List<SupplierOfferDto> offers = adapter.searchOffers(
                    request.getHotelId(),
                    request.getSupplierHotelId(),
                    request.getCheckIn(),
                    request.getCheckOut(),
                    request.getAdults(),
                    request.getChildren(),
                    request.getRooms()
            );
            return ResponseEntity.ok(offers);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Search failed", "message", e.getMessage()));
        }
    }

    /**
     * POST /internal/suppliers/offers/recheck
     * Recheck an offer.
     */
    @PostMapping("/offers/recheck")
    public ResponseEntity<?> recheckOffer(
            @RequestHeader(value = DEBUG_KEY_HEADER, required = false) String debugKey,
            @RequestBody RecheckOfferRequest request) {
        ResponseEntity<?> authError = checkDebugKey(debugKey);
        if (authError != null) {
            return authError;
        }

        try {
            SupplierRecheckAdapter adapter = adapterRegistry.getRecheckAdapter(request.getSupplierCode());
            String payloadJson = objectMapper.writeValueAsString(request.getOfferPayload());
            SupplierRecheckResultDto result = adapter.recheck(payloadJson);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Recheck failed", "message", e.getMessage()));
        }
    }

    /**
     * POST /internal/suppliers/bookings/create
     * Create a booking.
     */
    @PostMapping("/bookings/create")
    public ResponseEntity<?> createBooking(
            @RequestHeader(value = DEBUG_KEY_HEADER, required = false) String debugKey,
            @RequestBody CreateBookingRequest request) {
        ResponseEntity<?> authError = checkDebugKey(debugKey);
        if (authError != null) {
            return authError;
        }

        try {
            SupplierBookingAdapter adapter = adapterRegistry.getBookingAdapter(request.getSupplierCode());
            String offerJson = objectMapper.writeValueAsString(request.getOfferPayload());
            String guestJson = objectMapper.writeValueAsString(request.getGuestPayload());
            SupplierBookResponse response = adapter.createBooking(offerJson, guestJson);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Booking creation failed", "message", e.getMessage()));
        }
    }

    /**
     * POST /internal/suppliers/bookings/cancel
     * Cancel a booking.
     */
    @PostMapping("/bookings/cancel")
    public ResponseEntity<?> cancelBooking(
            @RequestHeader(value = DEBUG_KEY_HEADER, required = false) String debugKey,
            @RequestBody CancelBookingRequest request) {
        ResponseEntity<?> authError = checkDebugKey(debugKey);
        if (authError != null) {
            return authError;
        }

        try {
            SupplierBookingAdapter adapter = adapterRegistry.getBookingAdapter(request.getSupplierCode());
            adapter.cancelBooking(request.getSupplierBookingRef());
            return ResponseEntity.ok(Map.of(
                    "status", "CANCELLED",
                    "supplierBookingRef", request.getSupplierBookingRef()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Booking cancellation failed", "message", e.getMessage()));
        }
    }

    // Request DTOs

    public static class SearchOffersRequest {
        private SupplierCode supplierCode;
        private String hotelId;
        private String supplierHotelId;
        private LocalDate checkIn;
        private LocalDate checkOut;
        private int adults;
        private int children;
        private int rooms;

        public SupplierCode getSupplierCode() {
            return supplierCode;
        }

        public void setSupplierCode(SupplierCode supplierCode) {
            this.supplierCode = supplierCode;
        }

        public String getHotelId() {
            return hotelId;
        }

        public void setHotelId(String hotelId) {
            this.hotelId = hotelId;
        }

        public String getSupplierHotelId() {
            return supplierHotelId;
        }

        public void setSupplierHotelId(String supplierHotelId) {
            this.supplierHotelId = supplierHotelId;
        }

        public LocalDate getCheckIn() {
            return checkIn;
        }

        public void setCheckIn(LocalDate checkIn) {
            this.checkIn = checkIn;
        }

        public LocalDate getCheckOut() {
            return checkOut;
        }

        public void setCheckOut(LocalDate checkOut) {
            this.checkOut = checkOut;
        }

        public int getAdults() {
            return adults;
        }

        public void setAdults(int adults) {
            this.adults = adults;
        }

        public int getChildren() {
            return children;
        }

        public void setChildren(int children) {
            this.children = children;
        }

        public int getRooms() {
            return rooms;
        }

        public void setRooms(int rooms) {
            this.rooms = rooms;
        }
    }

    public static class RecheckOfferRequest {
        private SupplierCode supplierCode;
        private Map<String, Object> offerPayload; // Changed from 'payload' to 'offerPayload' to match JSON

        public SupplierCode getSupplierCode() {
            return supplierCode;
        }

        public void setSupplierCode(SupplierCode supplierCode) {
            this.supplierCode = supplierCode;
        }

        public Map<String, Object> getOfferPayload() {
            return offerPayload;
        }

        public void setOfferPayload(Map<String, Object> offerPayload) {
            this.offerPayload = offerPayload;
        }
    }

    public static class CreateBookingRequest {
        private SupplierCode supplierCode;
        private Object offerPayload;
        private Object guestPayload;

        public SupplierCode getSupplierCode() {
            return supplierCode;
        }

        public void setSupplierCode(SupplierCode supplierCode) {
            this.supplierCode = supplierCode;
        }

        public Object getOfferPayload() {
            return offerPayload;
        }

        public void setOfferPayload(Object offerPayload) {
            this.offerPayload = offerPayload;
        }

        public Object getGuestPayload() {
            return guestPayload;
        }

        public void setGuestPayload(Object guestPayload) {
            this.guestPayload = guestPayload;
        }
    }

    public static class CancelBookingRequest {
        private SupplierCode supplierCode;
        private String supplierBookingRef;

        public SupplierCode getSupplierCode() {
            return supplierCode;
        }

        public void setSupplierCode(SupplierCode supplierCode) {
            this.supplierCode = supplierCode;
        }

        public String getSupplierBookingRef() {
            return supplierBookingRef;
        }

        public void setSupplierBookingRef(String supplierBookingRef) {
            this.supplierBookingRef = supplierBookingRef;
        }
    }
}

