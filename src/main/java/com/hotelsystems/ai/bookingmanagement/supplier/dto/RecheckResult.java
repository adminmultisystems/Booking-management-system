package com.hotelsystems.ai.bookingmanagement.supplier.dto;

/**
 * Result of a recheck operation for a booking.
 * Indicates whether the booking rate is still available and valid.
 */
public class RecheckResult {
    private boolean available;
    private boolean priceChanged;
    private Long newPriceMinor;
    private String message;
    private String errorCode;

    public RecheckResult() {
    }

    public RecheckResult(boolean available, boolean priceChanged, Long newPriceMinor, 
                         String message, String errorCode) {
        this.available = available;
        this.priceChanged = priceChanged;
        this.newPriceMinor = newPriceMinor;
        this.message = message;
        this.errorCode = errorCode;
    }

    public static RecheckResult available() {
        return new RecheckResult(true, false, null, "Rate is available", null);
    }

    public static RecheckResult unavailable(String message) {
        return new RecheckResult(false, false, null, message, "RATE_UNAVAILABLE");
    }

    public static RecheckResult priceChanged(Long newPriceMinor) {
        return new RecheckResult(true, true, newPriceMinor, "Price has changed", null);
    }

    public static RecheckResult error(String message, String errorCode) {
        return new RecheckResult(false, false, null, message, errorCode);
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isPriceChanged() {
        return priceChanged;
    }

    public void setPriceChanged(boolean priceChanged) {
        this.priceChanged = priceChanged;
    }

    public Long getNewPriceMinor() {
        return newPriceMinor;
    }

    public void setNewPriceMinor(Long newPriceMinor) {
        this.newPriceMinor = newPriceMinor;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}

