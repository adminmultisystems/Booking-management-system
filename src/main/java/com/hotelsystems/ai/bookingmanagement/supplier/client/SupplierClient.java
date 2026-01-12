package com.hotelsystems.ai.bookingmanagement.supplier.client;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierBookingRequest;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierBookingResponse;

/**
 * Base interface for supplier HTTP clients.
 * Each supplier implementation should provide its own client implementation.
 */
public interface SupplierClient {
    /**
     * Creates a booking with the supplier.
     *
     * @param request the supplier booking request
     * @return the supplier booking response
     */
    SupplierBookingResponse createBooking(SupplierBookingRequest request);

    /**
     * Cancels a booking with the supplier.
     *
     * @param supplierBookingId the supplier booking ID
     * @return true if cancellation was successful
     */
    boolean cancelBooking(String supplierBookingId);

    /**
     * Gets the status of a booking from the supplier.
     *
     * @param supplierBookingId the supplier booking ID
     * @return the booking status
     */
    String getBookingStatus(String supplierBookingId);
}

