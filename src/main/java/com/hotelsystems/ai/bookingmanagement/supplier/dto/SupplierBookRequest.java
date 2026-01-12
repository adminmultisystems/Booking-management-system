package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;

/**
 * Request DTO for supplier booking operation.
 * Minimal DTO - may evolve based on supplier specifications.
 */
public class SupplierBookRequest {

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

    @JsonProperty("guestName")
    private String guestName;

    @JsonProperty("guestEmail")
    private String guestEmail;

    @JsonProperty("guestPhone")
    private String guestPhone;

    // Note: Additional fields can be added as supplier specifications evolve

    public SupplierBookRequest() {
    }

    public SupplierBookRequest(String supplierHotelId, String rateKey, String roomCode,
                              LocalDate checkIn, LocalDate checkOut,
                              String guestName, String guestEmail, String guestPhone) {
        this.supplierHotelId = supplierHotelId;
        this.rateKey = rateKey;
        this.roomCode = roomCode;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.guestName = guestName;
        this.guestEmail = guestEmail;
        this.guestPhone = guestPhone;
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

    public String getGuestName() {
        return guestName;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public String getGuestEmail() {
        return guestEmail;
    }

    public void setGuestEmail(String guestEmail) {
        this.guestEmail = guestEmail;
    }

    public String getGuestPhone() {
        return guestPhone;
    }

    public void setGuestPhone(String guestPhone) {
        this.guestPhone = guestPhone;
    }
}

