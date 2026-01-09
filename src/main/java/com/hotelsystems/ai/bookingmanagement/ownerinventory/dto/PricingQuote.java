package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

import java.util.List;

/**
 * DTO representing a pricing quote from the pricing intelligence service.
 */
public class PricingQuote {
    
    private String currency;
    private long totalPriceMinor; // Price in minor units (e.g., paise for INR)
    
    // TODO: Add real API fields when available
    // Optional: List<NightlyRate> nightlyRates;
    
    public PricingQuote() {
    }
    
    public PricingQuote(String currency, long totalPriceMinor) {
        this.currency = currency;
        this.totalPriceMinor = totalPriceMinor;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public long getTotalPriceMinor() {
        return totalPriceMinor;
    }
    
    public void setTotalPriceMinor(long totalPriceMinor) {
        this.totalPriceMinor = totalPriceMinor;
    }
}

