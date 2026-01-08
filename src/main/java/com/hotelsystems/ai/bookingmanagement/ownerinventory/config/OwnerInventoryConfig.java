package com.hotelsystems.ai.bookingmanagement.ownerinventory.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Owner Inventory module.
 * This configuration keeps the owner inventory module separate from orchestration.
 */
@Configuration
@EnableConfigurationProperties(PricingIntelligenceProperties.class)
public class OwnerInventoryConfig {
    
    // Configuration properties and beans for owner inventory can be added here
    // This keeps the module self-contained and separate from booking orchestration
    
}

