package com.hotelsystems.ai.bookingmanagement.service.adapter;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;

/**
 * Owner Inventory Adapter Interface
 * 
 * Defines the contract for integrating with owner's direct inventory system.
 * Engineers will implement this interface to connect with real owner inventory APIs.
 */
public interface OwnerInventoryAdapter {
    
    /**
     * Recheck booking availability and validity with owner inventory
     * 
     * @param booking Booking entity to recheck
     * @return RecheckResult with status and details
     */
    RecheckResult recheck(BookingEntity booking);
    
    /**
     * Reserve and confirm booking with owner inventory
     * 
     * @param booking Booking entity to reserve and confirm
     * @return Internal confirmation reference
     */
    String reserveAndConfirm(BookingEntity booking);
    
    /**
     * Release booking from owner inventory
     * 
     * @param booking Booking entity to release
     */
    void release(BookingEntity booking);
}
