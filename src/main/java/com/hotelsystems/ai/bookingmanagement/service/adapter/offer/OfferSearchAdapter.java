package com.hotelsystems.ai.bookingmanagement.service.adapter.offer;

import com.hotelsystems.ai.bookingmanagement.dto.offer.OfferDto;

import java.time.LocalDate;
import java.util.List;

/**
 * Offer Search Adapter Interface
 * 
 * Defines the contract for searching offers from inventory systems.
 * Engineers will implement this interface to connect with real supplier/owner APIs.
 */
public interface OfferSearchAdapter {
    
    /**
     * Search for offers matching the criteria
     * 
     * @param hotelId Hotel identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @param guests Number of guests
     * @param roomsCount Number of rooms
     * @return List of available offers
     */
    List<OfferDto> searchOffers(
            String hotelId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount
    );
}

