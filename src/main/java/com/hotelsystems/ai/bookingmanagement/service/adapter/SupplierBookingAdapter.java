package com.hotelsystems.ai.bookingmanagement.service.adapter;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;

/**
 * Supplier Booking Adapter Interface
 * 
 * Defines the contract for integrating with supplier booking systems.
 * Engineers will implement this interface to connect with real supplier APIs.
 */
public interface SupplierBookingAdapter {
    
    /**
     * Recheck booking availability and validity with supplier
     * 
     * @param booking Booking entity to recheck
     * @return RecheckResult with status and details
     */
    RecheckResult recheck(BookingEntity booking);
    
    /**
     * Create booking with supplier
     * 
     * @param booking Booking entity to create
     * @return Supplier booking reference/confirmation number
     */
    String createBooking(BookingEntity booking);
    
    /**
     * Cancel booking with supplier
     * 
     * @param booking Booking entity to cancel
     */
    void cancelBooking(BookingEntity booking);
}


