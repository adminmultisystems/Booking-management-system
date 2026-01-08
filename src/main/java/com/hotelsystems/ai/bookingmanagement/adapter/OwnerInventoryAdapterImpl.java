package com.hotelsystems.ai.bookingmanagement.adapter;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.availability.AvailabilityService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing.PricingService;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.reservation.ReservationService;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Primary implementation of OwnerInventoryAdapter.
 * This is the main adapter that integrates owner inventory and pricing services
 * with the booking orchestrator. It uses services from the ownerinventory package
 * to keep orchestration separate from inventory management.
 */
@Component
@Primary
public class OwnerInventoryAdapterImpl implements OwnerInventoryAdapter {
    
    private final AvailabilityService availabilityService;
    private final ReservationService reservationService;
    private final PricingService pricingService;
    
    public OwnerInventoryAdapterImpl(
            AvailabilityService availabilityService,
            ReservationService reservationService,
            PricingService pricingService) {
        this.availabilityService = availabilityService;
        this.reservationService = reservationService;
        this.pricingService = pricingService;
    }
    
    @Override
    public boolean isAvailable(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return availabilityService.isAvailable(hotelId, roomTypeId, checkIn, checkOut);
    }
    
    @Override
    public boolean reserveInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return reservationService.reserveInventory(
            hotelId,
            roomTypeId,
            checkIn,
            checkOut
        );
    }
    
    @Override
    public void releaseInventory(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        reservationService.releaseInventory(
            hotelId,
            roomTypeId,
            checkIn,
            checkOut
        );
    }
    
    @Override
    public BigDecimal calculatePrice(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        return pricingService.calculateTotalPrice(hotelId, roomTypeId, checkIn, checkOut);
    }
    
    @Override
    public BigDecimal getPriceForDate(UUID hotelId, UUID roomTypeId, LocalDate date) {
        return pricingService.getPriceForDate(hotelId, roomTypeId, date);
    }
}

