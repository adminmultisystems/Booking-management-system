package com.hotelsystems.ai.bookingmanagement.service.orchestration;

import com.hotelsystems.ai.bookingmanagement.enums.BookingStatus;
import com.hotelsystems.ai.bookingmanagement.exception.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Booking State Machine
 * 
 * Validates and manages booking status transitions according to business rules.
 */
@Component
@Slf4j
public class BookingStateMachine {
    
    /**
     * Validate if a status transition is allowed
     * 
     * @param from Current booking status
     * @param to Target booking status
     * @throws ConflictException if transition is not allowed
     */
    public void validateTransition(BookingStatus from, BookingStatus to) {
        log.debug("Validating status transition: {} → {}", from, to);
        
        // Same status is always allowed (no-op transition)
        if (from == to) {
            return;
        }
        
        boolean isValid = switch (from) {
            case DRAFT -> to == BookingStatus.RECHECKING;
            
            case RECHECKING -> to == BookingStatus.PENDING_CONFIRMATION || to == BookingStatus.FAILED;
            
            case PENDING_CONFIRMATION -> to == BookingStatus.CONFIRMED || to == BookingStatus.FAILED;
            
            case CONFIRMED -> to == BookingStatus.CANCELLED;
            
            case FAILED, CANCELLED -> false; // Terminal states - no transitions allowed
        };
        
        if (!isValid) {
            String message = String.format("Invalid status transition from %s to %s", from, to);
            log.warn(message);
            throw new ConflictException(message);
        }
        
        log.debug("Status transition validated: {} → {}", from, to);
    }
}

