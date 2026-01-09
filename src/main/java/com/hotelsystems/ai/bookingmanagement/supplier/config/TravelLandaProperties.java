package com.hotelsystems.ai.bookingmanagement.supplier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for TravelLanda supplier.
 */
@ConfigurationProperties(prefix = "supplier.travellanda")
public class TravelLandaProperties {
    private String baseUrl;
    private String apiKey;
    private Long timeoutMs;

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

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}

