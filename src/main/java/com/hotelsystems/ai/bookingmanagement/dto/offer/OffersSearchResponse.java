package com.hotelsystems.ai.bookingmanagement.dto.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Offers Search Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffersSearchResponse {
    
    /**
     * List of offers
     */
    private List<OfferDto> offers;
}

