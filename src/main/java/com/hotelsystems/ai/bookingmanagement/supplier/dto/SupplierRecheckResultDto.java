package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import java.math.BigDecimal;

/**
 * DTO representing the result of a recheck operation.
 */
public class SupplierRecheckResultDto {
    public enum RecheckStatus {
        OK,
        PRICE_CHANGED,
        SOLD_OUT
    }

    private RecheckStatus status;
    private BigDecimal newTotalPriceNullable;
    private String currencyNullable;

    public SupplierRecheckResultDto() {
    }

    public SupplierRecheckResultDto(RecheckStatus status, BigDecimal newTotalPriceNullable, String currencyNullable) {
        this.status = status;
        this.newTotalPriceNullable = newTotalPriceNullable;
        this.currencyNullable = currencyNullable;
    }

    public RecheckStatus getStatus() {
        return status;
    }

    public void setStatus(RecheckStatus status) {
        this.status = status;
    }

    public BigDecimal getNewTotalPriceNullable() {
        return newTotalPriceNullable;
    }

    public void setNewTotalPriceNullable(BigDecimal newTotalPriceNullable) {
        this.newTotalPriceNullable = newTotalPriceNullable;
    }

    public String getCurrencyNullable() {
        return currencyNullable;
    }

    public void setCurrencyNullable(String currencyNullable) {
        this.currencyNullable = currencyNullable;
    }
}

