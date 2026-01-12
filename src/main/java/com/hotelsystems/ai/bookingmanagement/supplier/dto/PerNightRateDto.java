package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing a per-night rate breakdown.
 */
public class PerNightRateDto {
    private LocalDate date;
    private BigDecimal amount;

    public PerNightRateDto() {
    }

    public PerNightRateDto(LocalDate date, BigDecimal amount) {
        this.date = date;
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}

