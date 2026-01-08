package com.hotelsystems.ai.bookingmanagement.ownerinventory.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Pricing Intelligence service integration.
 * Optional - works without configuration for dummy pricing mode.
 */
@ConfigurationProperties(prefix = "pricing.intelligence")
public class PricingIntelligenceProperties {
    
    private String baseUrl;
    private String apiKey;
    private int timeoutMs = 5000; // Default 5 seconds
    
    // Default constructor for cases where configuration is not provided
    public PricingIntelligenceProperties() {
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }
    
    public int getTimeoutMs() {
        return timeoutMs;
    }
    
    public void setTimeoutMs(int timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}

