package com.hotelsystems.ai.bookingmanagement.service.adapter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recheck Result
 * 
 * Result of rechecking booking availability with inventory systems.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecheckResult {
    
    private RecheckStatus status;
    
    private String message;
}


