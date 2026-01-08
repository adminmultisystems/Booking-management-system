package com.hotelsystems.ai.bookingmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Guest DTO
 * 
 * Represents individual guest information.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuestDto {
    
    /**
     * Guest full name
     */
    @NotBlank(message = "Guest name is required")
    private String name;
    
    /**
     * Guest email address
     */
    @NotBlank(message = "Guest email is required")
    @Email(message = "Guest email must be valid")
    private String email;
    
    /**
     * Guest phone number
     */
    @NotBlank(message = "Guest phone is required")
    private String phone;
}

