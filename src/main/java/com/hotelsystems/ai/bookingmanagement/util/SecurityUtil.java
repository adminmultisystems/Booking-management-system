package com.hotelsystems.ai.bookingmanagement.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security Utility
 * 
 * Helper class for accessing security context information.
 */
public class SecurityUtil {
    
    /**
     * Get current user ID from SecurityContext
     * 
     * @return User ID as String, or null if not authenticated
     */
    public static String getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            Object principal = authentication.getPrincipal();
            
            // In our JWT filter, we set the principal as the userId (String)
            if (principal instanceof String) {
                return (String) principal;
            }
            
            // Fallback: try to get name (which might be userId)
            return authentication.getName();
        }
        
        return null;
    }
    
    /**
     * Check if current user is authenticated
     * 
     * @return true if authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }
}

