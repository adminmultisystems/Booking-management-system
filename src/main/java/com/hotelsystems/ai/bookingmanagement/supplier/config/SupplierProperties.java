package com.hotelsystems.ai.bookingmanagement.supplier.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for supplier integrations.
 */
@Component
@ConfigurationProperties(prefix = "supplier")
public class SupplierProperties {

    private HotelbedsConfig hotelbeds;
    private TravellandaConfig travellanda;

    public HotelbedsConfig getHotelbeds() {
        return hotelbeds;
    }

    public void setHotelbeds(HotelbedsConfig hotelbeds) {
        this.hotelbeds = hotelbeds;
    }

    public TravellandaConfig getTravellanda() {
        return travellanda;
    }

    public void setTravellanda(TravellandaConfig travellanda) {
        this.travellanda = travellanda;
    }

    public static class HotelbedsConfig {
        private String baseUrl;
        private String apiKey;

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
    }

    public static class TravellandaConfig {
        private String baseUrl;
        private String apiKey;

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
    }
}

