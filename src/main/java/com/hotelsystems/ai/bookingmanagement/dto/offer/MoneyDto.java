package com.hotelsystems.ai.bookingmanagement.dto.offer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Money DTO
 * 
 * Represents a monetary amount with currency.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MoneyDto {
    
    /**
     * Amount value
     */
    private BigDecimal amount;
    
    /**
     * Currency code (ISO 4217, e.g., "USD", "EUR")
     */
    private String currency;
}

