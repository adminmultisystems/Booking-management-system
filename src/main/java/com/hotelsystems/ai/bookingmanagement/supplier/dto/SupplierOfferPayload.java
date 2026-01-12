package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * POJO representing the supplier "rate identity" needed for booking confirmation.
 * This payload contains the supplier-specific rate information required to confirm a booking.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SupplierOfferPayload {

    @JsonProperty("supplierCode")
    private SupplierCode supplierCode;

    @JsonProperty("supplierHotelId")
    private String supplierHotelId;

    @JsonProperty("rateKey")
    private String rateKey;

    @JsonProperty("roomCode")
    private String roomCode;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("expectedTotalPriceMinor")
    private Long expectedTotalPriceMinor;

    // Default constructor for Jackson deserialization
    public SupplierOfferPayload() {
    }

    // Constructor with required fields
    public SupplierOfferPayload(SupplierCode supplierCode, String supplierHotelId, 
                                String rateKey, String roomCode) {
        this.supplierCode = supplierCode;
        this.supplierHotelId = supplierHotelId;
        this.rateKey = rateKey;
        this.roomCode = roomCode;
    }

    // Full constructor
    public SupplierOfferPayload(SupplierCode supplierCode, String supplierHotelId, 
                                String rateKey, String roomCode, String currency, 
                                Long expectedTotalPriceMinor) {
        this.supplierCode = supplierCode;
        this.supplierHotelId = supplierHotelId;
        this.rateKey = rateKey;
        this.roomCode = roomCode;
        this.currency = currency;
        this.expectedTotalPriceMinor = expectedTotalPriceMinor;
    }

    // Getters and Setters
    public SupplierCode getSupplierCode() {
        return supplierCode;
    }

    public void setSupplierCode(SupplierCode supplierCode) {
        this.supplierCode = supplierCode;
    }

    public String getSupplierHotelId() {
        return supplierHotelId;
    }

    public void setSupplierHotelId(String supplierHotelId) {
        this.supplierHotelId = supplierHotelId;
    }

    public String getRateKey() {
        return rateKey;
    }

    public void setRateKey(String rateKey) {
        this.rateKey = rateKey;
    }

    public String getRoomCode() {
        return roomCode;
    }

    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Long getExpectedTotalPriceMinor() {
        return expectedTotalPriceMinor;
    }

    public void setExpectedTotalPriceMinor(Long expectedTotalPriceMinor) {
        this.expectedTotalPriceMinor = expectedTotalPriceMinor;
    }

    @Override
    public String toString() {
        return "SupplierOfferPayload{" +
                "supplierCode=" + supplierCode +
                ", supplierHotelId='" + supplierHotelId + '\'' +
                ", rateKey='" + rateKey + '\'' +
                ", roomCode='" + roomCode + '\'' +
                ", currency='" + currency + '\'' +
                ", expectedTotalPriceMinor=" + expectedTotalPriceMinor +
                '}';
    }
}

