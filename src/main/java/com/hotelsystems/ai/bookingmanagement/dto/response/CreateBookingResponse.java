package com.hotelsystems.ai.bookingmanagement.dto.response;

import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Create Booking Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingResponse {
    
    private UUID bookingId;
    
    private BookingStatus status;
    
    /**
     * Idempotency key (if provided in request)
     */
    private String idempotencyKey;
    
    /**
     * Booking source (OWNER or SUPPLIER)
     */
    private BookingSource source;
}

