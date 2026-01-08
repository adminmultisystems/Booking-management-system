package com.hotelsystems.ai.bookingmanagement.util;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.dto.request.CreateBookingRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.GuestDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.OccupancyDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PolicySnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PriceSnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.response.BookingResponse;
import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * Booking Mapper
 * 
 * Helper class for mapping between DTOs and entities.
 * Handles JSON serialization/deserialization and backward compatibility rules.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class BookingMapper {
    
    private final JsonUtil jsonUtil;
    
    /**
     * Map CreateBookingRequest to BookingEntity
     * 
     * Backward compatibility rules:
     * - If leadGuest is null, populate from guestName, guestEmail, guestPhone
     * - If occupancy fields are null, default: roomsCount=1, adults=1, children=0, childrenAges=[]
     * - If new snapshots are null, don't set them
     * 
     * @param request Create booking request
     * @param userId User ID (from security context)
     * @param supplierCode Supplier code (resolved from request)
     * @return BookingEntity builder (call .build() to create entity)
     */
    public BookingEntity.BookingEntityBuilder toEntity(CreateBookingRequest request, String userId, 
                                                      com.hotelsystems.ai.bookingmanagement.enums.SupplierCode supplierCode) {
        
        // Determine primary guest information
        String guestName;
        String guestEmail;
        String guestPhone;
        String guestsJson = null;
        String leadGuestJson = null;
        
        // Handle guests list: if provided, use it; otherwise use legacy fields
        if (request.getGuests() != null && !request.getGuests().isEmpty()) {
            // Serialize guests list to JSON
            guestsJson = jsonUtil.toJson(request.getGuests());
            
            // Use first guest as primary guest for legacy fields (backward compatibility)
            GuestDto primaryGuest = request.getGuests().get(0);
            guestName = primaryGuest.getName();
            guestEmail = primaryGuest.getEmail();
            guestPhone = primaryGuest.getPhone();
            
            // Lead guest is the first guest in the list
            leadGuestJson = jsonUtil.toJson(primaryGuest);
        } else {
            // Use legacy fields
            guestName = request.getGuestName();
            guestEmail = request.getGuestEmail();
            guestPhone = request.getGuestPhone();
            
            // Create lead guest from legacy fields if not already set
            if (leadGuestJson == null && guestName != null && guestEmail != null && guestPhone != null) {
                GuestDto leadGuest = GuestDto.builder()
                        .name(guestName)
                        .email(guestEmail)
                        .phone(guestPhone)
                        .build();
                leadGuestJson = jsonUtil.toJson(leadGuest);
            }
        }
        
        // Handle occupancy with defaults
        Integer roomsCount = request.getRoomsCount() != null ? request.getRoomsCount() : 1; // Default to 1 if not provided
        Integer adults = 1; // Default
        Integer children = 0; // Default
        String childrenAgesJson = jsonUtil.toJson(Collections.emptyList()); // Default empty array
        
        if (request.getOccupancy() != null) {
            OccupancyDto occupancy = request.getOccupancy();
            if (occupancy.getAdults() != null) {
                adults = occupancy.getAdults();
            }
            if (occupancy.getChildren() != null) {
                children = occupancy.getChildren();
            }
        }
        
        // Handle childrenAges from request (if provided)
        if (request.getChildrenAges() != null && !request.getChildrenAges().isEmpty()) {
            childrenAgesJson = jsonUtil.toJson(request.getChildrenAges());
        }
        
        // Also set occupancyAdults and occupancyChildren for backward compatibility
        Integer occupancyAdults = adults;
        Integer occupancyChildren = children;
        
        // Serialize price snapshot (only if provided)
        String priceSnapshotJson = null;
        if (request.getPriceSnapshot() != null) {
            priceSnapshotJson = jsonUtil.toJson(request.getPriceSnapshot());
        }
        
        // Serialize policy snapshot (only if provided)
        String policySnapshotJson = null;
        if (request.getPolicySnapshot() != null) {
            policySnapshotJson = jsonUtil.toJson(request.getPolicySnapshot());
        }
        
        // Build entity
        return BookingEntity.builder()
                .userId(userId)
                .hotelId(request.getHotelId())
                .roomTypeId(request.getRoomTypeId())
                .checkIn(request.getCheckIn())
                .checkOut(request.getCheckOut())
                .guestName(guestName)
                .guestEmail(guestEmail)
                .guestPhone(guestPhone)
                .specialRequests(request.getSpecialRequests())
                .offerPayloadJson(request.getOfferPayloadJson())
                .supplierCode(supplierCode)
                .status(BookingStatus.DRAFT)
                // Extended fields
                .occupancyAdults(occupancyAdults)
                .occupancyChildren(occupancyChildren)
                .guestsJson(guestsJson)
                .offerId(request.getOfferId())
                .priceSnapshotJson(priceSnapshotJson)
                .policySnapshotJson(policySnapshotJson)
                .idempotencyKey(request.getIdempotencyKey())
                // Additional booking draft details
                .roomsCount(roomsCount)
                .adults(adults)
                .children(children)
                .childrenAgesJson(childrenAgesJson)
                .leadGuestJson(leadGuestJson)
                // supplierRateKey, expiresAt, nextActionsJson are not set from request
                // They can be set separately if needed
                ;
    }
    
    /**
     * Map BookingEntity to BookingResponse
     * 
     * Handles JSON deserialization for all JSON fields.
     * 
     * @param booking Booking entity
     * @return BookingResponse
     */
    public BookingResponse toResponse(BookingEntity booking) {
        // Determine confirmation reference
        String confirmationRef = null;
        if (booking.getSource() == BookingSource.SUPPLIER) {
            confirmationRef = booking.getSupplierBookingRef();
        } else {
            confirmationRef = booking.getInternalConfirmationRef();
        }
        
        // Deserialize occupancy
        OccupancyDto occupancy = null;
        if (booking.getOccupancyAdults() != null) {
            occupancy = OccupancyDto.builder()
                    .adults(booking.getOccupancyAdults())
                    .children(booking.getOccupancyChildren() != null ? booking.getOccupancyChildren() : 0)
                    .build();
        }
        
        // Deserialize guests list
        List<GuestDto> guests = null;
        if (booking.getGuestsJson() != null && !booking.getGuestsJson().trim().isEmpty()) {
            guests = jsonUtil.fromJsonList(booking.getGuestsJson(), GuestDto.class);
        }
        
        // Deserialize price snapshot
        PriceSnapshotDto priceSnapshot = null;
        if (booking.getPriceSnapshotJson() != null && !booking.getPriceSnapshotJson().trim().isEmpty()) {
            priceSnapshot = jsonUtil.fromJson(booking.getPriceSnapshotJson(), PriceSnapshotDto.class);
        }
        
        // Deserialize policy snapshot
        PolicySnapshotDto policySnapshot = null;
        if (booking.getPolicySnapshotJson() != null && !booking.getPolicySnapshotJson().trim().isEmpty()) {
            policySnapshot = jsonUtil.fromJson(booking.getPolicySnapshotJson(), PolicySnapshotDto.class);
        }
        
        // Deserialize children ages
        List<Integer> childrenAges = null;
        if (booking.getChildrenAgesJson() != null && !booking.getChildrenAgesJson().trim().isEmpty()) {
            childrenAges = jsonUtil.fromJsonIntegerList(booking.getChildrenAgesJson());
        }
        
        // Deserialize lead guest
        GuestDto leadGuest = null;
        if (booking.getLeadGuestJson() != null && !booking.getLeadGuestJson().trim().isEmpty()) {
            leadGuest = jsonUtil.fromJson(booking.getLeadGuestJson(), GuestDto.class);
        }
        
        // Deserialize next actions
        List<String> nextActions = null;
        if (booking.getNextActionsJson() != null && !booking.getNextActionsJson().trim().isEmpty()) {
            nextActions = jsonUtil.fromJsonList(booking.getNextActionsJson(), String.class);
        }
        
        return BookingResponse.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .source(booking.getSource())
                .checkIn(booking.getCheckIn())
                .checkOut(booking.getCheckOut())
                .roomTypeId(booking.getRoomTypeId())
                .confirmationRef(confirmationRef)
                // Extended fields
                .occupancy(occupancy)
                .guests(guests)
                .guestName(booking.getGuestName())
                .guestEmail(booking.getGuestEmail())
                .guestPhone(booking.getGuestPhone())
                .offerId(booking.getOfferId())
                .priceSnapshot(priceSnapshot)
                .policySnapshot(policySnapshot)
                .failureReason(booking.getFailureReason())
                // Additional booking draft details
                .roomsCount(booking.getRoomsCount())
                .adults(booking.getAdults())
                .children(booking.getChildren())
                .childrenAges(childrenAges)
                .leadGuest(leadGuest)
                .supplierRateKey(booking.getSupplierRateKey())
                .expiresAt(booking.getExpiresAt())
                .nextActions(nextActions)
                .build();
    }
}

