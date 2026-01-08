package com.hotelsystems.ai.bookingmanagement.service.orchestration;

import com.hotelsystems.ai.bookingmanagement.domain.entity.BookingEntity;
import com.hotelsystems.ai.bookingmanagement.dto.request.ConfirmBookingRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.CreateBookingRequest;
import com.hotelsystems.ai.bookingmanagement.dto.request.GuestDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.OccupancyDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PolicySnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.request.PriceSnapshotDto;
import com.hotelsystems.ai.bookingmanagement.dto.response.BookingResponse;
import com.hotelsystems.ai.bookingmanagement.dto.response.CreateBookingResponse;
import com.hotelsystems.ai.bookingmanagement.enums.BookingSource;
import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import com.hotelsystems.ai.bookingmanagement.enums.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.exception.BadRequestException;
import com.hotelsystems.ai.bookingmanagement.exception.ConflictException;
import com.hotelsystems.ai.bookingmanagement.exception.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.repository.BookingRepository;
import com.hotelsystems.ai.bookingmanagement.service.adapter.OwnerInventoryAdapter;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckResult;
import com.hotelsystems.ai.bookingmanagement.service.adapter.RecheckStatus;
import com.hotelsystems.ai.bookingmanagement.service.adapter.SupplierBookingAdapter;
import com.hotelsystems.ai.bookingmanagement.util.BookingMapper;
import com.hotelsystems.ai.bookingmanagement.util.JsonUtil;
import com.hotelsystems.ai.bookingmanagement.util.SecurityUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Booking Orchestration Service
 * 
 * Orchestrates the booking lifecycle and manages status transitions.
 * Coordinates with adapters to create, confirm, and cancel bookings.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingOrchestrationService {
    
    private final BookingRepository bookingRepository;
    private final BookingStateMachine bookingStateMachine;
    private final SupplierBookingAdapter supplierBookingAdapter;
    private final OwnerInventoryAdapter ownerInventoryAdapter;
    private final ObjectMapper objectMapper;
    private final BookingMapper bookingMapper;
    private final JsonUtil jsonUtil;
    
    /**
     * Create a new booking
     * 
     * - Validate dates
     * - Get userId from SecurityContext
     * - status = DRAFT
     * - save booking
     * 
     * @param request Create booking request
     * @return Created booking response
     */
    @Transactional
    public CreateBookingResponse createBooking(CreateBookingRequest request) {
        log.info("Creating booking - hotelId: {}, roomTypeId: {}", request.getHotelId(), request.getRoomTypeId());
        
        // Validate dates (existing validation)
        validateDates(request.getCheckIn(), request.getCheckOut());
        
        // Get userId from SecurityContext
        String userId = SecurityUtil.getCurrentUserId();
        if (userId == null || userId.isEmpty()) {
            throw new BadRequestException("User ID is required. User must be authenticated.");
        }
        
        // Handle idempotency: if idempotencyKey is provided, check for existing booking
        if (request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
            Optional<BookingEntity> existingBooking = bookingRepository.findByUserIdAndIdempotencyKey(
                    userId, request.getIdempotencyKey());
            if (existingBooking.isPresent()) {
                log.info("Duplicate booking request detected - returning existing booking. idempotencyKey: {}, bookingId: {}", 
                        request.getIdempotencyKey(), existingBooking.get().getId());
                return CreateBookingResponse.builder()
                        .bookingId(existingBooking.get().getId())
                        .status(existingBooking.get().getStatus())
                        .build();
            }
        }
        
        // Validate guest information: either legacy fields OR guests list must be provided
        validateGuestInformation(request);
        
        // Soft validation for new fields (only when provided)
        validateNewFields(request);
        
        // Resolve supplier code from request or offer payload
        SupplierCode supplierCode = resolveSupplierCode(request);
        
        // Use BookingMapper to create entity builder
        BookingEntity.BookingEntityBuilder entityBuilder = bookingMapper.toEntity(request, userId, supplierCode);
        
        // Set expiresAt = now + 15 minutes for drafts
        Instant expiresAt = Instant.now().plus(15, ChronoUnit.MINUTES);
        entityBuilder.expiresAt(expiresAt);
        
        // Set nextActions to ["CONFIRM_REQUIRED"]
        String nextActionsJson = jsonUtil.toJson(Arrays.asList("CONFIRM_REQUIRED"));
        entityBuilder.nextActionsJson(nextActionsJson);
        
        // Note: roomsCount and childrenAges are already handled by BookingMapper
        
        // Build and save booking entity
        BookingEntity booking = entityBuilder.build();
        booking = bookingRepository.save(booking);
        
        log.info("Booking created successfully - bookingId: {}, userId: {}, expiresAt: {}", 
                booking.getId(), userId, expiresAt);
        
        return CreateBookingResponse.builder()
                .bookingId(booking.getId())
                .status(booking.getStatus())
                .build();
    }
    
    /**
     * Confirm a booking
     * 
     * - Idempotent
     * - DRAFT → RECHECKING
     * - Decide path: if supplierCode != null → SUPPLIER, else → OWNER
     * - Call recheck
     * - If SOLD_OUT/PRICE_CHANGED → FAILED
     * - Transition → PENDING_CONFIRMATION
     * - Call supplierAdapter.createBooking() OR ownerInventoryAdapter.reserveAndConfirm()
     * - Save confirmation ref
     * - status → CONFIRMED
     * 
     * @param bookingId Booking ID
     * @param request Optional confirm booking request (for idempotency key)
     * @return Confirmed booking response
     */
    @Transactional
    public BookingResponse confirmBooking(UUID bookingId, ConfirmBookingRequest request) {
        log.info("Confirming booking - bookingId: {}", bookingId);
        
        // Get booking
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        
        // Idempotent: if already CONFIRMED, return as-is
        if (booking.getStatus() == BookingStatus.CONFIRMED) {
            log.info("Booking already confirmed - bookingId: {}", bookingId);
            return mapToResponse(booking);
        }
        
        // Verify userId ownership
        String userId = SecurityUtil.getCurrentUserId();
        if (userId == null || !userId.equals(booking.getUserId())) {
            throw new BadRequestException("User does not have permission to confirm this booking");
        }
        
        // Handle idempotency key from request (if provided)
        // Note: This is for additional idempotency support at confirmation time
        // The booking creation already supports idempotency via idempotencyKey
        if (request != null && request.getIdempotencyKey() != null && !request.getIdempotencyKey().trim().isEmpty()) {
            // If booking already has an idempotency key and it matches, proceed
            // Otherwise, this is just informational logging
            log.debug("Confirm booking request with idempotency key: {}", request.getIdempotencyKey());
        }
        
        // Transition: DRAFT → RECHECKING (only if in DRAFT)
        if (booking.getStatus() == BookingStatus.DRAFT) {
            bookingStateMachine.validateTransition(BookingStatus.DRAFT, BookingStatus.RECHECKING);
            booking.setStatus(BookingStatus.RECHECKING);
            booking = bookingRepository.save(booking);
        }
        
        // If already in RECHECKING or PENDING_CONFIRMATION, continue from current state
        // Decide path: if supplierCode != null → SUPPLIER, else → OWNER
        BookingSource source = (booking.getSupplierCode() != null) 
                ? BookingSource.SUPPLIER 
                : BookingSource.OWNER;
        booking.setSource(source);
        
        // Check if offer reference exists (for routing/logging)
        boolean hasOfferReference = booking.getOfferId() != null && !booking.getOfferId().trim().isEmpty();
        boolean hasSupplierRateKey = booking.getSupplierRateKey() != null && !booking.getSupplierRateKey().trim().isEmpty();
        
        if (hasOfferReference || hasSupplierRateKey) {
            log.info("Booking has offer reference - bookingId: {}, offerId: {}, supplierRateKey: {}", 
                    bookingId, booking.getOfferId(), booking.getSupplierRateKey());
            // Note: Adapters receive the full booking entity, so they can access offerId/supplierRateKey
            // If adapter APIs support these fields, they will be used; otherwise stored and ignored for now
        }
        
        // Check if price snapshot exists (for price change validation)
        boolean hasPriceSnapshot = booking.getPriceSnapshotJson() != null && 
                                   !booking.getPriceSnapshotJson().trim().isEmpty();
        
        // Call recheck (only if not already in PENDING_CONFIRMATION)
        if (booking.getStatus() == BookingStatus.RECHECKING) {
            RecheckResult recheckResult;
            if (source == BookingSource.SUPPLIER) {
                recheckResult = supplierBookingAdapter.recheck(booking);
            } else {
                recheckResult = ownerInventoryAdapter.recheck(booking);
            }
            
            // Handle recheck failures
            if (recheckResult.getStatus() == RecheckStatus.SOLD_OUT) {
                bookingStateMachine.validateTransition(BookingStatus.RECHECKING, BookingStatus.FAILED);
                booking.setStatus(BookingStatus.FAILED);
                String failureReason = "Room is sold out or no longer available: " + recheckResult.getMessage();
                booking.setFailureReason(failureReason);
                booking = bookingRepository.save(booking);
                log.warn("Booking recheck failed - SOLD_OUT: bookingId: {}, reason: {}", bookingId, failureReason);
                return mapToResponse(booking);
            }
            
            // Handle PRICE_CHANGED with price snapshot validation
            if (recheckResult.getStatus() == RecheckStatus.PRICE_CHANGED) {
                bookingStateMachine.validateTransition(BookingStatus.RECHECKING, BookingStatus.FAILED);
                booking.setStatus(BookingStatus.FAILED);
                
                String failureReason;
                if (hasPriceSnapshot) {
                    failureReason = "Price has changed from the original offer: " + recheckResult.getMessage();
                    // Optionally: Store updated price snapshot if available in RecheckResult
                    // For now, RecheckResult doesn't include updated price, so we just set failure reason
                    log.warn("Price changed for booking {} with price snapshot. Original price snapshot: {}", 
                            bookingId, booking.getPriceSnapshotJson());
                } else {
                    failureReason = "Price has changed: " + recheckResult.getMessage();
                }
                
                booking.setFailureReason(failureReason);
                booking = bookingRepository.save(booking);
                log.warn("Booking recheck failed - PRICE_CHANGED: bookingId: {}, reason: {}", bookingId, failureReason);
                return mapToResponse(booking);
            }
            
            // Transition → PENDING_CONFIRMATION
            bookingStateMachine.validateTransition(BookingStatus.RECHECKING, BookingStatus.PENDING_CONFIRMATION);
            booking.setStatus(BookingStatus.PENDING_CONFIRMATION);
            booking = bookingRepository.save(booking);
        }
        
        // If already in PENDING_CONFIRMATION, proceed to create booking
        String confirmationRef = null;
        if (booking.getStatus() == BookingStatus.PENDING_CONFIRMATION) {
            // Call adapter to create booking (only if not already confirmed)
            if (booking.getSupplierBookingRef() == null && booking.getInternalConfirmationRef() == null) {
                if (source == BookingSource.SUPPLIER) {
                    confirmationRef = supplierBookingAdapter.createBooking(booking);
                    booking.setSupplierBookingRef(confirmationRef);
                } else {
                    confirmationRef = ownerInventoryAdapter.reserveAndConfirm(booking);
                    booking.setInternalConfirmationRef(confirmationRef);
                }
            } else {
                // Use existing confirmation reference
                confirmationRef = (source == BookingSource.SUPPLIER) 
                        ? booking.getSupplierBookingRef() 
                        : booking.getInternalConfirmationRef();
            }
            
            // Transition to CONFIRMED
            bookingStateMachine.validateTransition(BookingStatus.PENDING_CONFIRMATION, BookingStatus.CONFIRMED);
            booking.setStatus(BookingStatus.CONFIRMED);
            booking = bookingRepository.save(booking);
        }
        
        log.info("Booking confirmed successfully - bookingId: {}, confirmationRef: {}", bookingId, confirmationRef);
        
        return mapToResponse(booking);
    }
    
    /**
     * Cancel a booking
     * 
     * - Only CONFIRMED allowed
     * - Verify userId ownership
     * - Call cancel/release
     * - status → CANCELLED
     * 
     * @param bookingId Booking ID
     * @return Cancelled booking response
     */
    @Transactional
    public BookingResponse cancelBooking(UUID bookingId) {
        log.info("Cancelling booking - bookingId: {}", bookingId);
        
        // Get booking
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        
        // Verify userId ownership
        String userId = SecurityUtil.getCurrentUserId();
        if (userId == null || !userId.equals(booking.getUserId())) {
            throw new BadRequestException("User does not have permission to cancel this booking");
        }
        
        // Idempotent: if already CANCELLED, return as-is
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.info("Booking already cancelled - bookingId: {}, status: {}", bookingId, booking.getStatus());
            return mapToResponse(booking);
        }
        
        // Only CONFIRMED allowed
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ConflictException("Only CONFIRMED bookings can be cancelled. Current status: " + booking.getStatus());
        }
        
        // Call cancel/release based on source
        if (booking.getSource() == BookingSource.SUPPLIER) {
            supplierBookingAdapter.cancelBooking(booking);
        } else {
            ownerInventoryAdapter.release(booking);
        }
        
        // Transition to CANCELLED
        bookingStateMachine.validateTransition(BookingStatus.CONFIRMED, BookingStatus.CANCELLED);
        booking.setStatus(BookingStatus.CANCELLED);
        booking = bookingRepository.save(booking);
        
        log.info("Booking cancelled successfully - bookingId: {}, status: {}", bookingId, booking.getStatus());
        
        return mapToResponse(booking);
    }
    
    /**
     * Get booking by ID
     * 
     * - Verify userId ownership
     * 
     * @param bookingId Booking ID
     * @return Booking response
     */
    public BookingResponse getBooking(UUID bookingId) {
        log.info("Getting booking - bookingId: {}", bookingId);
        
        // Get booking
        BookingEntity booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found: " + bookingId));
        
        // Verify userId ownership
        String userId = SecurityUtil.getCurrentUserId();
        if (userId == null || !userId.equals(booking.getUserId())) {
            throw new BadRequestException("User does not have permission to access this booking");
        }
        
        return mapToResponse(booking);
    }
    
    // Helper methods
    
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
    
    /**
     * Validate guest information
     * 
     * Either legacy fields (guestName, guestEmail, guestPhone) OR guests list must be provided.
     * 
     * @param request Create booking request
     */
    private void validateGuestInformation(CreateBookingRequest request) {
        boolean hasLegacyFields = request.getGuestName() != null && !request.getGuestName().trim().isEmpty() &&
                                  request.getGuestEmail() != null && !request.getGuestEmail().trim().isEmpty() &&
                                  request.getGuestPhone() != null && !request.getGuestPhone().trim().isEmpty();
        
        boolean hasGuestsList = request.getGuests() != null && !request.getGuests().isEmpty();
        
        if (!hasLegacyFields && !hasGuestsList) {
            throw new BadRequestException("Either guests list or guest name/email/phone must be provided");
        }
        
        // Validate guests list if provided
        if (hasGuestsList) {
            for (GuestDto guest : request.getGuests()) {
                if (guest.getName() == null || guest.getName().trim().isEmpty()) {
                    throw new BadRequestException("All guests must have a name");
                }
                if (guest.getEmail() == null || guest.getEmail().trim().isEmpty()) {
                    throw new BadRequestException("All guests must have an email");
                }
                if (guest.getPhone() == null || guest.getPhone().trim().isEmpty()) {
                    throw new BadRequestException("All guests must have a phone");
                }
            }
        }
    }
    
    /**
     * Soft validation for new optional fields
     * 
     * Only validates when fields are provided:
     * - If adults is provided and <=0 → return 400
     * - If children > 0 and childrenAges missing/size mismatch → return 400
     * - If roomsCount provided and <=0 → return 400
     * 
     * @param request Create booking request
     */
    private void validateNewFields(CreateBookingRequest request) {
        // Validate adults (only if provided)
        if (request.getOccupancy() != null && request.getOccupancy().getAdults() != null) {
            Integer adults = request.getOccupancy().getAdults();
            if (adults <= 0) {
                throw new BadRequestException("Number of adults must be greater than 0");
            }
        }
        
        // Validate children and childrenAges (only if children > 0)
        if (request.getOccupancy() != null && request.getOccupancy().getChildren() != null) {
            Integer children = request.getOccupancy().getChildren();
            if (children > 0) {
                // If children > 0, childrenAges must be provided and match count
                if (request.getChildrenAges() == null || request.getChildrenAges().isEmpty()) {
                    throw new BadRequestException("childrenAges is required when children > 0");
                }
                if (request.getChildrenAges().size() != children) {
                    throw new BadRequestException(
                            String.format("childrenAges size (%d) must match children count (%d)", 
                                    request.getChildrenAges().size(), children));
                }
            }
        }
        
        // Validate roomsCount (only if provided)
        if (request.getRoomsCount() != null) {
            if (request.getRoomsCount() <= 0) {
                throw new BadRequestException("Number of rooms must be greater than 0");
            }
        }
    }
    
    private BookingResponse mapToResponse(BookingEntity booking) {
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
            try {
                guests = objectMapper.readValue(
                        booking.getGuestsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, GuestDto.class)
                );
            } catch (Exception e) {
                log.warn("Failed to deserialize guests JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
        
        // Deserialize price snapshot
        PriceSnapshotDto priceSnapshot = null;
        if (booking.getPriceSnapshotJson() != null && !booking.getPriceSnapshotJson().trim().isEmpty()) {
            try {
                priceSnapshot = objectMapper.readValue(booking.getPriceSnapshotJson(), PriceSnapshotDto.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize price snapshot JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
        
        // Deserialize policy snapshot
        PolicySnapshotDto policySnapshot = null;
        if (booking.getPolicySnapshotJson() != null && !booking.getPolicySnapshotJson().trim().isEmpty()) {
            try {
                policySnapshot = objectMapper.readValue(booking.getPolicySnapshotJson(), PolicySnapshotDto.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize policy snapshot JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
        
        // Deserialize children ages
        List<Integer> childrenAges = null;
        if (booking.getChildrenAgesJson() != null && !booking.getChildrenAgesJson().trim().isEmpty()) {
            try {
                childrenAges = objectMapper.readValue(
                        booking.getChildrenAgesJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class)
                );
            } catch (Exception e) {
                log.warn("Failed to deserialize children ages JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
        
        // Deserialize lead guest
        GuestDto leadGuest = null;
        if (booking.getLeadGuestJson() != null && !booking.getLeadGuestJson().trim().isEmpty()) {
            try {
                leadGuest = objectMapper.readValue(booking.getLeadGuestJson(), GuestDto.class);
            } catch (Exception e) {
                log.warn("Failed to deserialize lead guest JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
        }
        
        // Deserialize next actions
        List<String> nextActions = null;
        if (booking.getNextActionsJson() != null && !booking.getNextActionsJson().trim().isEmpty()) {
            try {
                nextActions = objectMapper.readValue(
                        booking.getNextActionsJson(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, String.class)
                );
            } catch (Exception e) {
                log.warn("Failed to deserialize next actions JSON for booking {}: {}", booking.getId(), e.getMessage());
            }
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
    
    /**
     * Resolve supplier code from request
     * 
     * Priority:
     * 1. Direct supplierCode field in request
     * 2. Parse from offerPayloadJson if present
     * 3. Return null (OWNER path)
     * 
     * @param request Create booking request
     * @return SupplierCode or null
     */
    private SupplierCode resolveSupplierCode(CreateBookingRequest request) {
        // Priority 1: Direct field
        if (request.getSupplierCode() != null) {
            log.debug("Supplier code from request field: {}", request.getSupplierCode());
            return request.getSupplierCode();
        }
        
        // Priority 2: Parse from offerPayloadJson
        if (request.getOfferPayloadJson() != null && !request.getOfferPayloadJson().trim().isEmpty()) {
            try {
                Map<String, Object> offerData = objectMapper.readValue(
                        request.getOfferPayloadJson(), 
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
                );
                
                Object supplierCodeValue = offerData.get("supplierCode");
                if (supplierCodeValue != null) {
                    String supplierCodeStr = supplierCodeValue.toString().toUpperCase();
                    try {
                        SupplierCode supplierCode = SupplierCode.valueOf(supplierCodeStr);
                        log.debug("Supplier code parsed from offerPayloadJson: {}", supplierCode);
                        return supplierCode;
                    } catch (IllegalArgumentException e) {
                        log.debug("Invalid supplier code in offerPayloadJson: {}", supplierCodeStr);
                    }
                }
            } catch (Exception e) {
                log.debug("Failed to parse supplierCode from offerPayloadJson: {}", e.getMessage());
                // Continue - will default to null (OWNER path)
            }
        }
        
        // Priority 3: No supplier code found - OWNER path
        log.debug("No supplier code found - will use OWNER path");
        return null;
    }
}
