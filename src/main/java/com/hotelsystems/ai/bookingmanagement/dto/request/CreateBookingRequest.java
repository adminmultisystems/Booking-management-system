package com.hotelsystems.ai.bookingmanagement.dto.request;

import com.hotelsystems.ai.bookingmanagement.enums.SupplierCode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Create Booking Request DTO
 * 
 * Extended to support:
 * - Occupancy (adults/children)
 * - Multiple guests
 * - Offer references
 * - Price/policy snapshots
 * - Idempotency key
 * 
 * Note: Existing fields (guestName, guestEmail, guestPhone) are kept for backward compatibility.
 * When guests list is provided, it takes precedence.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    
    @NotBlank(message = "Hotel ID is required")
    private String hotelId;
    
    @NotBlank(message = "Room type ID is required")
    private String roomTypeId;
    
    @NotNull(message = "Check-in date is required")
    private LocalDate checkIn;
    
    @NotNull(message = "Check-out date is required")
    private LocalDate checkOut;
    
    // Legacy guest fields (kept for backward compatibility)
    // Note: Either these fields OR the guests list must be provided
    // If guests list is provided, it takes precedence
    // Validation is handled in service layer to support both approaches
    private String guestName;
    
    @Email(message = "Guest email must be valid (if provided)")
    private String guestEmail;
    
    private String guestPhone;
    
    private String specialRequests;
    
    private String offerPayloadJson; // optional
    
    private SupplierCode supplierCode; // optional - only for supplier bookings
    
    // New optional fields
    
    /**
     * Occupancy information (adults and children)
     */
    @Valid
    private OccupancyDto occupancy;
    
    /**
     * List of guests (optional - if provided, takes precedence over guestName/guestEmail/guestPhone)
     */
    @Valid
    private List<GuestDto> guests;
    
    /**
     * Offer ID reference (optional)
     */
    private String offerId;
    
    /**
     * Price snapshot at booking creation time (optional)
     */
    @Valid
    private PriceSnapshotDto priceSnapshot;
    
    /**
     * Policy snapshot at booking creation time (optional)
     */
    @Valid
    private PolicySnapshotDto policySnapshot;
    
    /**
     * Idempotency key for ensuring duplicate requests are handled safely (optional)
     */
    private String idempotencyKey;
    
    /**
     * Number of rooms (optional, defaults to 1)
     */
    private Integer roomsCount;
    
    /**
     * Children ages as list of integers (optional, required if children > 0)
     */
    private List<Integer> childrenAges;
}
