package com.hotelsystems.ai.bookingmanagement.ownerinventory.dto;

/**
 * DTO representing a pricing quote from the pricing intelligence service.
 */
public class PricingQuote {
    
    private String currency;
    private long totalPriceMinor; // Price in minor units (e.g., paise for INR)
    
    // Note: Additional API fields (e.g., List<NightlyRate> nightlyRates) can be added when available
    
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

