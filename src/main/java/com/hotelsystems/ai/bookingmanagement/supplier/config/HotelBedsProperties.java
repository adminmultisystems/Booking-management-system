package com.hotelsystems.ai.bookingmanagement.supplier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for HotelBeds supplier.
 */
@ConfigurationProperties(prefix = "suppliers.hotelbeds")
public class HotelBedsProperties {
    private String baseUrl;
    private String apiKey;
    private String secret;
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

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getTimeoutMs() {
        return timeoutMs;
    }

    public void setTimeoutMs(Long timeoutMs) {
        this.timeoutMs = timeoutMs;
    }
}

