package com.hotelsystems.ai.bookingmanagement.supplier.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enum representing supplier codes.
 */
public enum SupplierCode {
    HOTELBEDS("HOTELBEDS"),
    TRAVELLANDA("TRAVELLANDA");

    private final String code;

    SupplierCode(String code) {
        this.code = code;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String toString() {
        return code;
    }
}

