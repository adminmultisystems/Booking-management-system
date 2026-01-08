package com.hotelsystems.ai.bookingmanagement.controller;

import com.hotelsystems.ai.bookingmanagement.dto.request.ConfirmBookingRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.CreateBookingRequest;
import com.hotelsystems.ai.bookingmanagement.dto.response.BookingResponse;
import com.hotelsystems.ai.bookingmanagement.dto.response.CancelBookingResponse;
import com.hotelsystems.ai.bookingmanagement.dto.response.ConfirmBookingResponse;
import com.hotelsystems.ai.bookingmanagement.dto.response.CreateBookingResponse;
import com.hotelsystems.ai.bookingmanagement.service.orchestration.BookingOrchestrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Booking Controller
 * 
 * REST endpoints for booking management operations.
 */
@RestController
@RequestMapping("/v1/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    
    private final BookingOrchestrationService bookingOrchestrationService;
    
    /**
     * Create a new booking
     * 
     * POST /v1/bookings
     */
    @PostMapping
    public ResponseEntity<CreateBookingResponse> createBooking(
            @Valid @RequestBody CreateBookingRequest request) {
        
        log.info("POST /v1/bookings - hotelId: {}, roomTypeId: {}", 
                request.getHotelId(), request.getRoomTypeId());
        
        // Create booking (userId extracted from SecurityContext internally)
        CreateBookingResponse response = bookingOrchestrationService.createBooking(request);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    /**
     * Get booking by ID
     * 
     * GET /v1/bookings/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<BookingResponse> getBooking(@PathVariable UUID id) {
        
        log.info("GET /v1/bookings/{}", id);
        
        BookingResponse response = bookingOrchestrationService.getBooking(id);
        return ResponseEntity.ok(response);
    }
    
    /**
     * Confirm a booking
     * 
     * POST /v1/bookings/{id}/confirm
     * 
     * Optional request body supports idempotency key for safe retries.
     */
    @PostMapping("/{id}/confirm")
    public ResponseEntity<ConfirmBookingResponse> confirmBooking(
            @PathVariable UUID id,
            @RequestBody(required = false) @Valid ConfirmBookingRequest request) {
        
        log.info("POST /v1/bookings/{}/confirm - Confirming booking", id);
        
        BookingResponse bookingResponse = bookingOrchestrationService.confirmBooking(id, request);
        
        ConfirmBookingResponse response = ConfirmBookingResponse.builder()
                .bookingId(bookingResponse.getBookingId())
                .status(bookingResponse.getStatus())
                .confirmationRef(bookingResponse.getConfirmationRef())
                .failureReason(bookingResponse.getFailureReason())
                .build();
        
        // If status is FAILED, return 409 Conflict, otherwise 200 OK
        if (bookingResponse.getStatus() == com.hotelsystems.ai.bookingmanagement.enums.BookingStatus.FAILED) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Cancel a booking
     * 
     * POST /v1/bookings/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public ResponseEntity<CancelBookingResponse> cancelBooking(@PathVariable UUID id) {
        
        log.info("POST /v1/bookings/{}/cancel - Cancelling booking", id);
        
        BookingResponse bookingResponse = bookingOrchestrationService.cancelBooking(id);
        
        CancelBookingResponse response = CancelBookingResponse.builder()
                .bookingId(bookingResponse.getBookingId())
                .status(bookingResponse.getStatus())
                .message("Booking cancelled")
                .build();
        
        return ResponseEntity.ok(response);
    }
}
