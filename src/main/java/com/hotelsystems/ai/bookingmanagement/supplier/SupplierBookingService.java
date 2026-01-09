package com.hotelsystems.ai.bookingmanagement.supplier;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierOfferPayload;
import org.springframework.context.annotation.Profile;

/**
 * Service interface for supplier-specific booking operations.
 * Each supplier implementation provides its own service implementation.
 * 
 * NOTE: This interface is coupled to BookingEntity and is only active with the "supplier-real" profile.
 * All implementations must be annotated with @Profile("supplier-real").
 * This interface is not used in Phase-1 (default profile).
 * 
 * NOTE: BookingEntity is not available in Phase-1. This interface will be updated when TL-owned
 * orchestration logic is integrated. For Phase-1, use SupplierBookingAdapter instead.
 */
@Profile("supplier-real")
public interface SupplierBookingService {

    /**
     * Returns the supplier code this service handles.
     *
     * @return the supplier code
     */
    SupplierCode supplierCode();

    /**
     * Rechecks the availability and price of a booking offer.
     *
     * @param booking the booking entity (not available in Phase-1)
     * @param payload the supplier offer payload containing rate identity
     * @return the recheck result indicating availability and any price changes
     */
    RecheckResult recheck(Object booking, SupplierOfferPayload payload);

    /**
     * Creates a booking with the supplier.
     *
     * @param booking the booking entity (not available in Phase-1)
     * @param payload the supplier offer payload containing rate identity
     * @return the supplier booking reference
     */
    String createBooking(Object booking, SupplierOfferPayload payload);

    /**
     * Cancels a booking with the supplier.
     *
     * @param booking the booking entity (not available in Phase-1)
     */
    void cancelBooking(Object booking);
}

