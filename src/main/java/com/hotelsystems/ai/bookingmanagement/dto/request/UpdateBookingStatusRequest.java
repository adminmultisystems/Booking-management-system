package com.hotelsystems.ai.bookingmanagement.dto.request;

import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Update Booking Status Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateBookingStatusRequest {
    
    @NotNull(message = "Status is required")
    private BookingStatus status;
    
    private String failureReason;
}

