package com.hotelsystems.ai.bookingmanagement.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT Authentication Filter (Stub)
 * 
 * Reads Authorization header, extracts userId (mock/simple parse for now),
 * and attaches userId to SecurityContext.
 * 
 * This is a stub implementation that will be replaced with proper JWT validation.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final AuthenticationService authenticationService;
    
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            try {
                // Extract userId from token (stub implementation)
                String userId = authenticationService.getUserIdFromToken(authHeader);
                
                if (userId != null && !userId.isEmpty()) {
                    // Create authentication object with userId
                    Authentication authentication = new UsernamePasswordAuthenticationToken(
                            userId,
                            null,
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );
                    
                    // Attach to SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    
                    log.debug("User authenticated: userId={}", userId);
                }
            } catch (Exception e) {
                log.warn("Failed to extract userId from token: {}", e.getMessage());
                // Continue without authentication - will be rejected by security config if required
            }
        }
        
        filterChain.doFilter(request, response);
    }
}

