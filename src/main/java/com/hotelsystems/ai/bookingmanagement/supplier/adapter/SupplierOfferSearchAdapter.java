package com.hotelsystems.ai.bookingmanagement.supplier.adapter;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierOfferDto;
import java.time.LocalDate;
import java.util.List;

/**
 * Adapter interface for searching supplier offers.
 */
public interface SupplierOfferSearchAdapter {
    
    /**
     * Search for offers from a supplier.
     * 
     * @param hotelId the internal hotel ID
     * @param supplierHotelId the supplier's hotel ID
     * @param checkIn check-in date
     * @param checkOut check-out date
     * @param adults number of adults
     * @param children number of children
     * @param rooms number of rooms
     * @return list of offers
     */
    List<SupplierOfferDto> searchOffers(String hotelId, String supplierHotelId, 
                                       LocalDate checkIn, LocalDate checkOut, 
                                       int adults, int children, int rooms);
}

