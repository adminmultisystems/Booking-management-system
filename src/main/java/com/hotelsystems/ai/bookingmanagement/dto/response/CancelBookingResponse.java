package com.hotelsystems.ai.bookingmanagement.dto.response;

import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * Cancel Booking Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CancelBookingResponse {
    
    private UUID bookingId;
    
    private BookingStatus status;
    
    private String message;
}

