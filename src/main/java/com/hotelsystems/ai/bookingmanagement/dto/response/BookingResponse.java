package com.hotelsystems.ai.bookingmanagement.dto.response;

import com.hotelsystems.ai.bookingmanagement.dto.request.GuestDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.OccupancyDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PolicySnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PriceSnapshotDto;
import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Booking Response DTO
 * 
 * Extended to include occupancy, guests, offer references, and price/policy snapshots.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    
    private UUID bookingId;
    
    private BookingStatus status;
    
    private BookingSource source;
    
    private LocalDate checkIn;
    
    private LocalDate checkOut;
    
    private String roomTypeId;
    
    private String confirmationRef;
    
    // Extended fields (optional)
    
    /**
     * Occupancy information
     */
    private OccupancyDto occupancy;
    
    /**
     * List of guests
     */
    private List<GuestDto> guests;
    
    /**
     * Primary guest name (for backward compatibility)
     */
    private String guestName;
    
    /**
     * Primary guest email (for backward compatibility)
     */
    private String guestEmail;
    
    /**
     * Primary guest phone (for backward compatibility)
     */
    private String guestPhone;
    
    /**
     * Offer ID reference
     */
    private String offerId;
    
    /**
     * Price snapshot at booking creation
     */
    private PriceSnapshotDto priceSnapshot;
    
    /**
     * Policy snapshot at booking creation
     */
    private PolicySnapshotDto policySnapshot;
    
    /**
     * Failure reason (when status is FAILED)
     */
    private String failureReason;
    
    // Additional booking draft details (optional)
    
    /**
     * Number of rooms
     */
    private Integer roomsCount;
    
    /**
     * Number of adults (separate from occupancy for flexibility)
     */
    private Integer adults;
    
    /**
     * Number of children (separate from occupancy for flexibility)
     */
    private Integer children;
    
    /**
     * Children ages as list of integers
     */
    private List<Integer> childrenAges;
    
    /**
     * Lead guest information
     */
    private GuestDto leadGuest;
    
    /**
     * Supplier rate key (for supplier bookings)
     */
    private String supplierRateKey;
    
    /**
     * Booking expiration timestamp (for draft bookings)
     */
    private Instant expiresAt;
    
    /**
     * Next actions information
     */
    private List<String> nextActions;
}
