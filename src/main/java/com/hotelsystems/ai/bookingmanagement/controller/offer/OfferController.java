package com.hotelsystems.ai.bookingmanagement.controller.offer;

import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckRequest;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersRecheckResponse;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersSearchRequest;
import com.hotelsystems.ai.bookingmanagement.dto.offer.OffersSearchResponse;
import com.hotelsystems.ai.bookingmanagement.service.offer.OfferService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Offer Controller
 * 
 * REST endpoints for offer search and recheck operations.
 */
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@Slf4j
public class OfferController {
    
    private final OfferService offerService;
    
    /**
     * Search offers for a hotel
     * 
     * POST /v1/hotels/{slug}/offers:search
     */
    @PostMapping("/hotels/{slug}/offers:search")
    public ResponseEntity<OffersSearchResponse> searchOffers(
            @PathVariable String slug,
            @Valid @RequestBody OffersSearchRequest request) {
        
        log.info("POST /v1/hotels/{}/offers:search - checkIn: {}, checkOut: {}, guests: {}",
                slug, request.getCheckIn(), request.getCheckOut(), request.getGuests());
        
        OffersSearchResponse response = offerService.searchOffers(slug, request);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Recheck offer availability and validity
     * 
     * POST /v1/offers:recheck
     */
    @PostMapping("/offers:recheck")
    public ResponseEntity<OffersRecheckResponse> recheckOffer(
            @Valid @RequestBody OffersRecheckRequest request) {
        
        log.info("POST /v1/offers:recheck - offerId: {}, checkIn: {}, checkOut: {}",
                request.getOfferId(), request.getCheckIn(), request.getCheckOut());
        
        OffersRecheckResponse response = offerService.recheck(request);
        
        return ResponseEntity.ok(response);
    }
}

