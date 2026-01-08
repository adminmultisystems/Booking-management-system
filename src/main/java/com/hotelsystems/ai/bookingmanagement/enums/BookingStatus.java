package com.hotelsystems.ai.bookingmanagement.enums;

/**
 * Booking Status Enum
 * 
 * Defines the possible statuses in the booking lifecycle state machine.
 */
public enum BookingStatus {
    /**
     * Initial draft state when booking is being created
     */
    DRAFT,
    
    /**
     * Booking is being rechecked for availability
     */
    RECHECKING,
    
    /**
     * Booking is pending confirmation
     */
    PENDING_CONFIRMATION,
    
    /**
     * Booking has been confirmed and is active
     */
    CONFIRMED,
    
    /**
     * Booking failed due to an error (e.g., inventory unavailable, payment failed)
     */
    FAILED,
    
    /**
     * Booking has been cancelled
     */
    CANCELLED
}

