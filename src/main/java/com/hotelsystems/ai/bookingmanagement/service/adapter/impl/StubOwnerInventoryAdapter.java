package com.hotelsystems.ai.bookingmanagement.service.adapter.impl;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.service.adapter.OwnerInventoryAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Stub Owner Inventory Adapter
 * 
 * Stub implementation for owner inventory integration.
 * This will be replaced by engineers with real owner inventory API integration.
 */
@Component
@Slf4j
public class StubOwnerInventoryAdapter implements OwnerInventoryAdapter {
    
    @Override
    public RecheckResult recheck(BookingEntity booking) {
        log.info("STUB: Rechecking owner inventory - bookingId: {}, hotelId: {}",
                booking.getId(), booking.getHotelId());
        
        // Stub: Always return OK
        return RecheckResult.builder()
                .status(RecheckStatus.OK)
                .message("STUB: Owner inventory recheck successful (stub implementation)")
                .build();
    }
    
    @Override
    public String reserveAndConfirm(BookingEntity booking) {
        log.info("STUB: Reserving and confirming owner inventory - bookingId: {}, hotelId: {}",
                booking.getId(), booking.getHotelId());
        
        // Stub: Return mock internal confirmation reference
        String confirmationRef = "OWN-" + UUID.randomUUID();
        log.info("STUB: Created owner confirmation reference: {}", confirmationRef);
        return confirmationRef;
    }
    
    @Override
    public void release(BookingEntity booking) {
        log.info("STUB: Releasing owner inventory - bookingId: {}, confirmationRef: {}",
                booking.getId(), booking.getInternalConfirmationRef());
        
        // Stub: No-op
    }
}
