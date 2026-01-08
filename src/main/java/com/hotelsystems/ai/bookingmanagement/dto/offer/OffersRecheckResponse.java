package com.hotelsystems.ai.bookingmanagement.dto.offer;

import com.hotelsystems.ai.bookingmanagement.enums.RecheckResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Offers Recheck Response DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffersRecheckResponse {
    
    /**
     * Recheck result: OK, PRICE_CHANGED, or SOLD_OUT
     */
    private RecheckResult result;
    
    /**
     * Updated offer (optional - present when result is OK or PRICE_CHANGED)
     */
    private OfferDto offer;
    
    /**
     * Optional message describing the result
     */
    private String message;
}

