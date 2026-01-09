package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import java.math.BigDecimal;

/**
 * DTO representing taxes and fees information (placeholder for Phase-1).
 */
public class TaxesAndFeesPlaceholderDto {
    private boolean included;
    private BigDecimal amountNullable;
    private String note;

    public TaxesAndFeesPlaceholderDto() {
    }

    public TaxesAndFeesPlaceholderDto(boolean included, BigDecimal amountNullable, String note) {
        this.included = included;
        this.amountNullable = amountNullable;
        this.note = note;
    }

    public boolean isIncluded() {
        return included;
    }

    public void setIncluded(boolean included) {
        this.included = included;
    }

    public BigDecimal getAmountNullable() {
        return amountNullable;
    }

    public void setAmountNullable(BigDecimal amountNullable) {
        this.amountNullable = amountNullable;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}

