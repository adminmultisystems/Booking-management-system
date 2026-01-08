package com.hotelsystems.ai.bookingmanagement.domain.entity;

import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import com.hotelsystems.ai.bookingmanagement.enums.SupplierCode;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Booking Entity
 * 
 * Core booking entity mapped to bookings_core table.
 * Represents a hotel booking with guest information, supplier/owner details, and offer snapshot.
 * 
 * Important:
 * - userId is REQUIRED
 * - roomTypeId is REQUIRED
 * - No nullable booking ownership
 */
@Entity
@Table(
    name = "bookings_core",
    indexes = {
        @Index(name = "idx_bookings_core_hotel_id", columnList = "hotel_id"),
        @Index(name = "idx_bookings_core_status", columnList = "status"),
        @Index(name = "idx_bookings_core_created_at", columnList = "created_at"),
        @Index(name = "idx_bookings_core_user_id", columnList = "user_id"),
        @Index(name = "idx_bookings_core_idempotency_key", columnList = "idempotency_key"),
        @Index(name = "idx_bookings_core_offer_id", columnList = "offer_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;
    
    @Column(name = "hotel_id", nullable = false, length = 100)
    private String hotelId;
    
    @Column(name = "room_type_id", nullable = false, length = 100)
    private String roomTypeId;
    
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;
    
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private BookingStatus status = BookingStatus.DRAFT;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "source", nullable = false, length = 20)
    @Builder.Default
    private BookingSource source = BookingSource.SUPPLIER;
    
    // Guest information
    @Column(name = "guest_name", nullable = false, length = 255)
    private String guestName;
    
    @Column(name = "guest_email", nullable = false, length = 255)
    private String guestEmail;
    
    @Column(name = "guest_phone", nullable = false, length = 50)
    private String guestPhone;
    
    @Column(name = "special_requests", columnDefinition = "TEXT")
    private String specialRequests;
    
    // Supplier fields (nullable - only used when source is SUPPLIER)
    @Enumerated(EnumType.STRING)
    @Column(name = "supplier_code", length = 50)
    private SupplierCode supplierCode;
    
    @Column(name = "supplier_hotel_id", length = 100)
    private String supplierHotelId;
    
    @Column(name = "supplier_booking_ref", length = 100)
    private String supplierBookingRef;
    
    // Owner fields (nullable - only used when source is OWNER)
    @Column(name = "internal_confirmation_ref", length = 100)
    private String internalConfirmationRef;
    
    // Offer snapshot
    @Column(name = "offer_payload_json", columnDefinition = "TEXT")
    private String offerPayloadJson;
    
    // Extended fields (nullable for backward compatibility)
    
    /**
     * Occupancy - number of adults
     */
    @Column(name = "occupancy_adults")
    private Integer occupancyAdults;
    
    /**
     * Occupancy - number of children
     */
    @Column(name = "occupancy_children")
    private Integer occupancyChildren;
    
    /**
     * List of guests (stored as JSON)
     */
    @Column(name = "guests_json", columnDefinition = "TEXT")
    private String guestsJson;
    
    /**
     * Offer ID reference
     */
    @Column(name = "offer_id", length = 100)
    private String offerId;
    
    /**
     * Price snapshot at booking creation (stored as JSON)
     */
    @Column(name = "price_snapshot_json", columnDefinition = "TEXT")
    private String priceSnapshotJson;
    
    /**
     * Policy snapshot at booking creation (stored as JSON)
     */
    @Column(name = "policy_snapshot_json", columnDefinition = "TEXT")
    private String policySnapshotJson;
    
    /**
     * Idempotency key for duplicate request handling
     */
    @Column(name = "idempotency_key", length = 255)
    private String idempotencyKey;
    
    // Additional booking draft details (nullable for backward compatibility)
    
    /**
     * Number of rooms
     */
    @Column(name = "rooms_count")
    private Integer roomsCount;
    
    /**
     * Number of adults (separate from occupancyAdults for flexibility)
     */
    @Column(name = "adults")
    private Integer adults;
    
    /**
     * Number of children (separate from occupancyChildren for flexibility)
     */
    @Column(name = "children")
    private Integer children;
    
    /**
     * Children ages stored as JSON array (e.g., [5, 8])
     */
    @Column(name = "children_ages_json", columnDefinition = "TEXT")
    private String childrenAgesJson;
    
    /**
     * Lead guest information stored as JSON
     */
    @Column(name = "lead_guest_json", columnDefinition = "TEXT")
    private String leadGuestJson;
    
    /**
     * Supplier rate key (for supplier bookings)
     */
    @Column(name = "supplier_rate_key", length = 255)
    private String supplierRateKey;
    
    /**
     * Booking expiration timestamp (for draft bookings)
     */
    @Column(name = "expires_at")
    private Instant expiresAt;
    
    /**
     * Next actions information stored as JSON
     */
    @Column(name = "next_actions_json", columnDefinition = "TEXT")
    private String nextActionsJson;
    
    /**
     * Failure reason (when booking status is FAILED)
     */
    @Column(name = "failure_reason", columnDefinition = "TEXT")
    private String failureReason;
    
    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        if (this.createdAt == null) {
            this.createdAt = now;
        }
        if (this.updatedAt == null) {
            this.updatedAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}

