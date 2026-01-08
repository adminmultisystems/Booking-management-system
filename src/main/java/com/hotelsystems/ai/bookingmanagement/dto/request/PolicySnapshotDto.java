package com.hotelsystems.ai.bookingmanagement.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Policy Snapshot DTO
 * 
 * Represents a snapshot of cancellation and other policies at the time of booking creation.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PolicySnapshotDto {
    
    /**
     * Cancellation policy summary text
     */
    private String cancellationPolicySummary;
    
    /**
     * Free cancellation deadline (if applicable)
     */
    private LocalDateTime freeCancellationDeadline;
    
    /**
     * Whether cancellation is allowed
     */
    private Boolean cancellationAllowed;
    
    /**
     * Refund policy summary
     */
    private String refundPolicySummary;
    
    /**
     * Check-in policy
     */
    private String checkInPolicy;
    
    /**
     * Check-out policy
     */
    private String checkOutPolicy;
    
    /**
     * Additional policy information (JSON string for flexibility)
     */
    private String additionalPoliciesJson;
}

