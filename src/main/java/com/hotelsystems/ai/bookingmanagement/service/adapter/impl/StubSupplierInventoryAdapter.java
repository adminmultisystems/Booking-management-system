package com.hotelsystems.ai.bookingmanagement.service.adapter.impl;

import com.hotelsystems.ai.bookingmanagement.service.adapter.SupplierInventoryAdapter;
import com.hotelsystems.ai.bookingmanagement.dto.response.InventoryAvailabilityResponse;
import com.hotelsystems.ai.bookingmanagement.dto.request.ReserveInventoryRequest;
import com.hotelsystems.ai.bookingmanagement.dto.response.ReserveInventoryResponse;
import com.hotelsystems.ai.bookingmanagement.dto.request.ConfirmInventoryRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.CancelInventoryRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Stub Supplier Inventory Adapter
 * 
 * Stub implementation for supplier inventory integration.
 * This will be replaced by engineers with real supplier API integration.
 */
@Component
@Slf4j
public class StubSupplierInventoryAdapter implements SupplierInventoryAdapter {
    
    @Override
    public InventoryAvailabilityResponse checkAvailability(
            String hotelId,
            String roomType,
            LocalDate checkInDate,
            LocalDate checkOutDate,
            Integer numberOfRooms) {
        
        log.info("STUB: Checking supplier inventory availability - hotelId: {}, roomType: {}, checkIn: {}, checkOut: {}, rooms: {}",
                hotelId, roomType, checkInDate, checkOutDate, numberOfRooms);
        
        // Stub: Always return available with a mock price
        return InventoryAvailabilityResponse.builder()
                .available(true)
                .roomType(roomType)
                .price(BigDecimal.valueOf(100.00))
                .currency("USD")
                .message("STUB: Inventory available (stub implementation)")
                .inventoryReference("STUB-SUPPLIER-" + System.currentTimeMillis())
                .build();
    }
    
    @Override
    public ReserveInventoryResponse reserveInventory(ReserveInventoryRequest request) {
        log.info("STUB: Reserving supplier inventory - bookingReference: {}, hotelId: {}",
                request.getBookingReference(), request.getHotelId());
        
        // Stub: Always succeed
        return ReserveInventoryResponse.builder()
                .success(true)
                .inventoryReservationId("STUB-RESERVATION-" + System.currentTimeMillis())
                .message("STUB: Inventory reserved (stub implementation)")
                .expiresInSeconds(300L) // 5 minutes
                .build();
    }
    
    @Override
    public boolean confirmInventory(ConfirmInventoryRequest request) {
        log.info("STUB: Confirming supplier inventory - bookingReference: {}, reservationId: {}",
                request.getBookingReference(), request.getInventoryReservationId());
        
        // Stub: Always succeed
        return true;
    }
    
    @Override
    public boolean cancelInventory(CancelInventoryRequest request) {
        log.info("STUB: Cancelling supplier inventory - bookingReference: {}, reservationId: {}",
                request.getBookingReference(), request.getInventoryReservationId());
        
        // Stub: Always succeed
        return true;
    }
}

