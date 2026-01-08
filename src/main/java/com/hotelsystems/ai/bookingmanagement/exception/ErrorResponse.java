package com.hotelsystems.ai.bookingmanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Error Response DTO
 * 
 * Standard error response format for API exceptions.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    
    private Instant timestamp;
    
    private int status;
    
    private String error;
    
    private String message;
    
    private String path;
}

