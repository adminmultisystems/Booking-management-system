package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Request DTO for supplier recheck operation.
 * Minimal DTO - may evolve based on supplier specifications.
 */
public class SupplierRecheckRequest {

    @JsonProperty("supplierHotelId")
    private String supplierHotelId;

    @JsonProperty("rateKey")
    private String rateKey;

    @JsonProperty("roomCode")
    private String roomCode;

    @JsonProperty("checkIn")
    private LocalDate checkIn;

    @JsonProperty("checkOut")
    private LocalDate checkOut;

    // Note: Additional fields can be added as supplier specifications evolve

    public SupplierRecheckRequest() {
    }

    public SupplierRecheckRequest(String supplierHotelId, String rateKey, String roomCode, 
                                  LocalDate checkIn, LocalDate checkOut) {
        this.supplierHotelId = supplierHotelId;
        this.rateKey = rateKey;
        this.roomCode = roomCode;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
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

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
}

