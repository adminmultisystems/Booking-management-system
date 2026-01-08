package com.hotelsystems.ai.bookingmanagement.service.adapter;

import com.hotelsystems.ai.bookingmanagement.dto.response.InventoryAvailabilityResponse;
import com.hotelsystems.ai.bookingmanagement.dto.request.ReserveInventoryRequest;
import com.hotelsystems.ai.bookingmanagement.dto.response.ReserveInventoryResponse;
import com.hotelsystems.ai.bookingmanagement.dto.request.ConfirmInventoryRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.CancelInventoryRequest;

import java.time.LocalDate;

/**
 * Supplier Inventory Adapter Interface
 * 
 * Defines the contract for integrating with supplier inventory systems.
 * Engineers will implement this interface to connect with real supplier APIs.
 */
public interface SupplierInventoryAdapter {
    
    /**
     * Check if inventory is available for the given criteria
     * 
     * @param hotelId Hotel identifier
     * @param roomType Room type identifier
     * @param checkInDate Check-in date
     * @param checkOutDate Check-out date
     * @param numberOfRooms Number of rooms needed
     * @return Availability response with availability status and details
     */
    InventoryAvailabilityResponse checkAvailability(
        String hotelId,
        String roomType,
        LocalDate checkInDate,
        LocalDate checkOutDate,
        Integer numberOfRooms
    );
    
    /**
     * Reserve inventory (temporary hold)
     * 
     * @param request Reserve inventory request
     * @return Reserve response with reservation details
     */
    ReserveInventoryResponse reserveInventory(ReserveInventoryRequest request);
    
    /**
     * Confirm inventory reservation (convert hold to confirmed booking)
     * 
     * @param request Confirm inventory request
     * @return True if confirmation successful, false otherwise
     */
    boolean confirmInventory(ConfirmInventoryRequest request);
    
    /**
     * Cancel inventory reservation
     * 
     * @param request Cancel inventory request
     * @return True if cancellation successful, false otherwise
     */
    boolean cancelInventory(CancelInventoryRequest request);
}

