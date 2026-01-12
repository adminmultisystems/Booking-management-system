package com.hotelsystems.ai.bookingmanagement.supplier.adapter;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierBookResponse;

/**
 * Adapter interface for booking operations with suppliers.
 */
public interface SupplierBookingAdapter {
    
    /**
     * Create a booking with the supplier.
     * 
     * @param offerPayloadJson the offer payload as JSON
     * @param guestPayloadJson the guest information as JSON
     * @return booking response
     */
    SupplierBookResponse createBooking(String offerPayloadJson, String guestPayloadJson);
    
    /**
     * Cancel a booking with the supplier.
     * 
     * @param supplierBookingRef the supplier booking reference
     */
    void cancelBooking(String supplierBookingRef);
}

