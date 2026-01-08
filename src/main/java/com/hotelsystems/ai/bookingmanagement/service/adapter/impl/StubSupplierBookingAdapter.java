package com.hotelsystems.ai.bookingmanagement.service.adapter.impl;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckStatus;
import com.hotelsystems.ai.bookingmanagement.service.adapter.SupplierBookingAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Stub Supplier Booking Adapter
 * 
 * Stub implementation for supplier booking integration.
 * This will be replaced by engineers with real supplier API integration.
 */
@Service
@Primary
@Slf4j
public class StubSupplierBookingAdapter implements SupplierBookingAdapter {
    
    @Override
    public RecheckResult recheck(BookingEntity booking) {
        log.info("STUB: Rechecking supplier booking - bookingId: {}, hotelId: {}",
                booking.getId(), booking.getHotelId());
        
        // Stub: Always return OK
        return RecheckResult.builder()
                .status(RecheckStatus.OK)
                .message("STUB: Booking recheck successful (stub implementation)")
                .build();
    }
    
    @Override
    public String createBooking(BookingEntity booking) {
        log.info("STUB: Creating supplier booking - bookingId: {}, hotelId: {}",
                booking.getId(), booking.getHotelId());
        
        // Stub: Return mock supplier booking reference
        String supplierRef = "SUP-" + UUID.randomUUID();
        log.info("STUB: Created supplier booking reference: {}", supplierRef);
        return supplierRef;
    }
    
    @Override
    public void cancelBooking(BookingEntity booking) {
        log.info("STUB: Cancelling supplier booking - bookingId: {}, supplierRef: {}",
                booking.getId(), booking.getSupplierBookingRef());
        
        // Stub: No-op
    }
}

