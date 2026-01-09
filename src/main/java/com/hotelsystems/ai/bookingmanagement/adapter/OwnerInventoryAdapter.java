package com.hotelsystems.ai.bookingmanagement.adapter;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Adapter interface for owner inventory and pricing operations.
 * This interface allows the booking orchestrator to check availability and pricing
 * without directly coupling to the inventory implementation.
 */
public interface OwnerInventoryAdapter {
    
    /**
     * Checks if rooms are available for the given booking parameters.
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return true if rooms are available for all dates in the range, false otherwise
     */
    boolean isAvailable(UUID hotelId, UUID roomTypeId, java.time.LocalDate checkIn, java.time.LocalDate checkOut);
    
    /**
     * Reserves inventory for a booking.
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return true if reservation was successful, false otherwise
     */
    boolean reserveInventory(UUID hotelId, UUID roomTypeId, java.time.LocalDate checkIn, java.time.LocalDate checkOut);
    
    /**
     * Releases inventory for a booking (e.g., on cancellation).
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     */
    void releaseInventory(UUID hotelId, UUID roomTypeId, java.time.LocalDate checkIn, java.time.LocalDate checkOut);
    
    /**
     * Calculates the total price for a booking.
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @return The total price for the booking period
     */
    BigDecimal calculatePrice(UUID hotelId, UUID roomTypeId, java.time.LocalDate checkIn, java.time.LocalDate checkOut);
    
    /**
     * Gets the price per night for a specific date.
     * 
     * @param hotelId The hotel identifier
     * @param roomTypeId The room type identifier
     * @param date The date
     * @return The price for that date
     */
    BigDecimal getPriceForDate(UUID hotelId, UUID roomTypeId, java.time.LocalDate date);
}

