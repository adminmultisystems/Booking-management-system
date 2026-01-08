package com.hotelsystems.ai.bookingmanagement.dto.offer;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Offers Search Request DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffersSearchRequest {
    
    /**
     * Check-in date (yyyy-MM-dd)
     */
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;
    
    /**
     * Check-out date (yyyy-MM-dd)
     */
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;
    
    /**
     * Number of guests
     */
    @NotNull(message = "Number of guests is required")
    @Min(value = 1, message = "Number of guests must be at least 1")
    private Integer guests;
    
    /**
     * Number of rooms (optional, default 1)
     */
    @Min(value = 1, message = "Number of rooms must be at least 1")
    @Builder.Default
    private Integer roomsCount = 1;
}

