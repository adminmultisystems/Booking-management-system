package com.hotelsystems.ai.bookingmanagement.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * CORS Configuration
 * 
 * Configures Cross-Origin Resource Sharing (CORS) for the booking management service.
 * Allows frontend applications to access the API.
 */
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    
    @Value("${cors.allowed-origins:http://localhost:3000,http://localhost:8080}")
    private String allowedOrigins;
    
    @Value("${cors.allowed-credentials:true}")
    private boolean allowedCredentials;
    
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        
        registry.addMapping("/**")
                .allowedOrigins(origins.toArray(new String[0]))
                .allowedMethods("GET", "POST", "PATCH")
                .allowedHeaders("Authorization", "Content-Type", "Idempotency-Key")
                .allowCredentials(allowedCredentials)
                .maxAge(3600); // Cache preflight response for 1 hour
    }
    
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Exclude H2 console from Spring MVC resource handling
        // This allows the H2 console servlet to handle the requests
        registry.addResourceHandler("/h2-console/**")
                .resourceChain(false);
    }
    
    /**
     * CORS Configuration Source for Spring Security
     * 
     * This is used by Spring Security to apply CORS settings.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        List<String> origins = Arrays.asList(allowedOrigins.split(","));
        // Use setAllowedOrigins for specific origins (when credentials are allowed)
        configuration.setAllowedOrigins(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Idempotency-Key"));
        configuration.setAllowCredentials(allowedCredentials);
        configuration.setMaxAge(3600L); // Cache preflight response for 1 hour
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        
        return source;
    }
}

