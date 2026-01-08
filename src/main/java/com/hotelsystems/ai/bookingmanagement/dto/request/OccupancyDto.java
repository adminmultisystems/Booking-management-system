package com.hotelsystems.ai.bookingmanagement.dto.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Occupancy DTO
 * 
 * Represents the number of adults and children for a booking.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OccupancyDto {
    
    /**
     * Number of adults (minimum 1)
     */
    @Min(value = 1, message = "Number of adults must be at least 1")
    private Integer adults;
    
    /**
     * Number of children (optional, defaults to 0)
     */
    @Min(value = 0, message = "Number of children cannot be negative")
    @Builder.Default
    private Integer children = 0;
}

