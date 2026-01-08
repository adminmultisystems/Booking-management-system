package com.hotelsystems.ai.bookingmanagement.ownerinventory.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * HTTP configuration for Owner Pricing service.
 * Provides RestTemplate with timeouts and safe logging interceptor.
 */
@Configuration
@EnableConfigurationProperties(PricingIntelligenceProperties.class)
public class OwnerPricingHttpConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(OwnerPricingHttpConfig.class);
    
    /**
     * RestTemplate bean for pricing intelligence service.
     * Note: Currently not used as PricingIntelligenceClient uses dummy pricing for local/testing.
     * Kept for potential future use or production scenarios.
     */
    @Bean
    public RestTemplate pricingIntelligenceRestTemplate(PricingIntelligenceProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Use default timeout if not configured (safe for local/testing)
        int timeout = (properties != null && properties.getTimeoutMs() > 0) 
            ? properties.getTimeoutMs() 
            : 5000; // Default 5 seconds
        factory.setConnectTimeout(timeout);
        factory.setReadTimeout(timeout);
        
        RestTemplate restTemplate = new RestTemplate(factory);
        
        // Add safe logging interceptor
        List<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new SafeLoggingInterceptor());
        restTemplate.setInterceptors(interceptors);
        
        return restTemplate;
    }
    
    /**
     * Safe logging interceptor that logs request/response without exposing sensitive data.
     */
    private static class SafeLoggingInterceptor implements ClientHttpRequestInterceptor {
        
        @Override
        public ClientHttpResponse intercept(
                HttpRequest request,
                byte[] body,
                ClientHttpRequestExecution execution) throws IOException {
            
            // Log request (safe - no sensitive data)
            logger.debug("Pricing Intelligence Request: {} {}", 
                request.getMethod(), request.getURI());
            
            try {
                ClientHttpResponse response = execution.execute(request, body);
                
                // Log response (safe - only status)
                logger.debug("Pricing Intelligence Response: {} {}", 
                    response.getStatusCode(), response.getStatusText());
                
                return response;
            } catch (Exception e) {
                logger.error("Pricing Intelligence Request failed: {}", e.getMessage());
                throw e;
            }
        }
    }
}

