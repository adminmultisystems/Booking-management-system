package com.hotelsystems.ai.bookingmanagement.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Authentication Interceptor (JWT-ready skeleton)
 * 
 * Intercepts requests to extract and validate authentication.
 * This will be replaced with proper JWT validation in production.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthenticationInterceptor implements HandlerInterceptor {
    
    @SuppressWarnings("unused")
    private final AuthenticationService authenticationService; // Reserved for future JWT validation implementation
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String authHeader = request.getHeader("Authorization");
        
        // For now, allow requests without authentication (stub)
        // In production, validate JWT token here using authenticationService
        if (authHeader == null || authHeader.isEmpty()) {
            log.debug("Request without Authorization header (stub mode - allowing)");
            // In production: if (!authenticationService.isAuthenticated(authHeader)) { return false; }
        } else {
            // Validate token in production: authenticationService.getUserIdFromToken(authHeader);
            log.debug("Authorization header present (stub mode - allowing)");
        }
        
        return true; // Allow request to proceed
    }
}

