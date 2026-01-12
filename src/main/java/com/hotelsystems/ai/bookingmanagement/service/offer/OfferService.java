package com.hotelsystems.ai.bookingmanagement.service.offer;

import com.hotelsystems.ai.bookingmanagement.dto.hotel.HotelResponse;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OfferDto;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckRequest;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersSearchRequest;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersSearchResponse;
import com.hotelsystems.ai.bookingmanagement.enums.OfferSource;
import com.hotelsystems.ai.bookingmanagement.exception.BadRequestException;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferRecheckAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.OfferSearchAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.offer.impl.SupplierOfferAdapterStub;
import com.hotelsystems.ai.bookingmanagement.service.hotel.BookingHotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * Offer Service
 * 
 * Service for searching and rechecking offers.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OfferService {
    
    private final BookingHotelService hotelService;
    private final OfferRoutingService offerRoutingService;
    private final SupplierOfferAdapterStub supplierOfferAdapter;
    private final OfferSearchAdapter ownerOfferSearchAdapter; // Use interface - @Primary will select RealOwnerOfferAdapter
    private final OfferRecheckAdapter ownerOfferRecheckAdapter; // Use interface - @Primary will select RealOwnerOfferAdapter
    
    /**
     * Search for offers by hotel slug
     * 
     * @param slug Hotel slug
     * @param request Search request with dates, guests, rooms
     * @return Search response with list of offers
     */
    public OffersSearchResponse searchOffers(String slug, OffersSearchRequest request) {
        log.info("Searching offers for hotel slug: {}, checkIn: {}, checkOut: {}",
                slug, request.getCheckIn(), request.getCheckOut());
        
        // Resolve hotel by slug (throws NotFoundException if not found)
        HotelResponse hotel = hotelService.getHotelBySlug(slug);
        String hotelId = hotel.getHotelId() != null ? hotel.getHotelId() : hotel.getSlug();
        
        // Validate dates
        validateDates(request.getCheckIn(), request.getCheckOut());
        
        // Default roomsCount to 1 if null or 0
        Integer roomsCount = request.getRoomsCount();
        if (roomsCount == null || roomsCount <= 0) {
            roomsCount = 1;
            log.debug("Defaulting roomsCount to 1");
        }
        
        // Decide routing: SUPPLIER vs OWNER
        OfferSource source = offerRoutingService.decideSourceForHotel(hotelId, slug);
        
        // Get appropriate adapter
        OfferSearchAdapter adapter = getSearchAdapter(source);
        
        // Search offers
        List<OfferDto> offers = adapter.searchOffers(
                hotelId,
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                roomsCount
        );
        
        log.info("Found {} offers for hotel slug: {} (source: {})", offers.size(), slug, source);
        
        return OffersSearchResponse.builder()
                .offers(offers)
                .build();
    }
    
    /**
     * Recheck offer availability and validity
     * 
     * @param request Recheck request with offerId, dates, guests, rooms
     * @return Recheck response with result and updated offer
     */
    public OffersRecheckResponse recheck(OffersRecheckRequest request) {
        log.info("Rechecking offer - offerId: {}, checkIn: {}, checkOut: {}",
                request.getOfferId(), request.getCheckIn(), request.getCheckOut());
        
        // Validate offerId not blank
        if (!StringUtils.hasText(request.getOfferId())) {
            throw new BadRequestException("Offer ID is required");
        }
        
        // Validate dates
        validateDates(request.getCheckIn(), request.getCheckOut());
        
        // Default roomsCount to 1 if null or 0
        Integer roomsCount = request.getRoomsCount();
        if (roomsCount == null || roomsCount <= 0) {
            roomsCount = 1;
            log.debug("Defaulting roomsCount to 1");
        }
        
        // Decide adapter based on offerId prefix
        OfferRecheckAdapter adapter = getRecheckAdapter(request.getOfferId());
        
        // Recheck offer
        OffersRecheckResponse response = adapter.recheck(
                request.getOfferId(),
                request.getCheckIn(),
                request.getCheckOut(),
                request.getGuests(),
                roomsCount
        );
        
        log.info("Recheck completed - offerId: {}, result: {}", request.getOfferId(), response.getResult());
        
        return response;
    }
    
    /**
     * Get search adapter based on source
     */
    private OfferSearchAdapter getSearchAdapter(OfferSource source) {
        if (source == OfferSource.SUPPLIER) {
            return supplierOfferAdapter;
        } else {
            return ownerOfferSearchAdapter;
        }
    }
    
    /**
     * Get recheck adapter based on offerId prefix
     * 
     * SUP- => supplier adapter
     * OWN- => owner adapter
     */
    private OfferRecheckAdapter getRecheckAdapter(String offerId) {
        if (offerId != null && offerId.startsWith("SUP-")) {
            log.debug("Using supplier adapter for offerId: {}", offerId);
            return supplierOfferAdapter;
        } else if (offerId != null && offerId.startsWith("OWN-")) {
            log.debug("Using owner adapter for offerId: {}", offerId);
            return ownerOfferRecheckAdapter;
        } else {
            // Safe default: owner adapter
            log.warn("Unknown offerId prefix, defaulting to owner adapter: {}", offerId);
            return ownerOfferRecheckAdapter;
        }
    }
    
    /**
     * Validate check-in and check-out dates
     * 
     * Reuses validation pattern from BookingOrchestrationService.
     * 
     * @param checkIn Check-in date
     * @param checkOut Check-out date
     * @throws BadRequestException if validation fails
     */
    private void validateDates(LocalDate checkIn, LocalDate checkOut) {
        if (checkIn == null || checkOut == null) {
            throw new BadRequestException("Check-in and check-out dates are required");
        }
        
        LocalDate today = LocalDate.now();
        if (checkIn.isBefore(today)) {
            throw new BadRequestException("Check-in date must be today or in the future");
        }
        
        if (checkOut.isBefore(checkIn) || checkOut.isEqual(checkIn)) {
            throw new BadRequestException("Check-out date must be after check-in date");
        }
    }
}

