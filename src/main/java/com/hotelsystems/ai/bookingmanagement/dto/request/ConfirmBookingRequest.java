package com.hotelsystems.ai.bookingmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Confirm Booking Request DTO
 * 
 * Optional request body for booking confirmation.
 * Supports idempotency key for safe retries.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmBookingRequest {
    
    /**
     * Idempotency key for ensuring duplicate confirmation requests are handled safely (optional)
     */
    private String idempotencyKey;
}

