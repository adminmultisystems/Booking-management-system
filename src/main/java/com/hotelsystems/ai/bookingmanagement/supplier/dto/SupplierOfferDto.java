package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO representing a normalized supplier offer.
 */
public class SupplierOfferDto {
    private String offerId;
    private SupplierCode supplierCode;
    private String supplierHotelId;
    private String roomName;
    private String board;
    private BigDecimal totalPrice;
    private String currency;
    private List<PerNightRateDto> perNightBreakdown;
    private String cancellationSummary;
    private TaxesAndFeesPlaceholderDto taxesAndFees;
    private String rawPayloadJson;

    public SupplierOfferDto() {
    }

    public String getOfferId() {
        return offerId;
    }

    public void setOfferId(String offerId) {
        this.offerId = offerId;
    }

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

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<PerNightRateDto> getPerNightBreakdown() {
        return perNightBreakdown;
    }

    public void setPerNightBreakdown(List<PerNightRateDto> perNightBreakdown) {
        this.perNightBreakdown = perNightBreakdown;
    }

    public String getCancellationSummary() {
        return cancellationSummary;
    }

    public void setCancellationSummary(String cancellationSummary) {
        this.cancellationSummary = cancellationSummary;
    }

    public TaxesAndFeesPlaceholderDto getTaxesAndFees() {
        return taxesAndFees;
    }

    public void setTaxesAndFees(TaxesAndFeesPlaceholderDto taxesAndFees) {
        this.taxesAndFees = taxesAndFees;
    }

    public String getRawPayloadJson() {
        return rawPayloadJson;
    }

    public void setRawPayloadJson(String rawPayloadJson) {
        this.rawPayloadJson = rawPayloadJson;
    }
}

