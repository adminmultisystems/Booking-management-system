package com.hotelsystems.ai.bookingmanagement.service.adapter.offer;

import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;

import java.time.LocalDate;

/**
 * Offer Recheck Adapter Interface
 * 
 * Defines the contract for rechecking offer availability with inventory systems.
 * Engineers will implement this interface to connect with real supplier/owner APIs.
 */
public interface OfferRecheckAdapter {
    
    /**
     * Recheck offer availability and validity
     * 
     * @param offerId Offer identifier
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @param guests Number of guests
     * @param roomsCount Number of rooms
     * @return Recheck response with result and updated offer if available
     */
    OffersRecheckResponse recheck(
            String offerId,
            LocalDate checkIn,
            LocalDate checkOut,
            Integer guests,
            Integer roomsCount
    );
}

