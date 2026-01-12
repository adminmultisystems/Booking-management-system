package com.hotelsystems.ai.bookingmanagement.supplier.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for sanitizing supplier payloads before logging.
 * Removes sensitive information like rateKeys, PII, and signatures.
 */
public class SupplierPayloadSanitizer {

    private static final Logger logger = LoggerFactory.getLogger(SupplierPayloadSanitizer.class);
    private static final String RATE_KEY_PATTERN = "\"rateKey\"\\s*:\\s*\"[^\"]+\"";
    private static final String RATE_KEY_REPLACEMENT = "\"rateKey\":\"***\"";
    private static final String SIGNATURE_PATTERN = "\"signature\"\\s*:\\s*\"[^\"]+\"";
    private static final String SIGNATURE_REPLACEMENT = "\"signature\":\"***\"";
    private static final String EMAIL_PATTERN = "\"email\"\\s*:\\s*\"[^\"]+\"";
    private static final String EMAIL_REPLACEMENT = "\"email\":\"***\"";
    private static final String PHONE_PATTERN = "\"phone\"\\s*:\\s*\"[^\"]+\"";
    private static final String PHONE_REPLACEMENT = "\"phone\":\"***\"";

    private SupplierPayloadSanitizer() {
        // Utility class
    }

    /**
     * Sanitizes a JSON payload by removing sensitive information.
     * 
     * @param rawPayload the raw JSON payload
     * @return sanitized payload with sensitive data masked
     */
    public static String sanitize(String rawPayload) {
        if (rawPayload == null || rawPayload.isEmpty()) {
            return rawPayload;
        }

        try {
            String sanitized = rawPayload;
            
            // Remove rateKeys
            sanitized = sanitized.replaceAll(RATE_KEY_PATTERN, RATE_KEY_REPLACEMENT);
            sanitized = sanitized.replaceAll("(?i)" + RATE_KEY_PATTERN, RATE_KEY_REPLACEMENT);
            
            // Remove signatures
            sanitized = sanitized.replaceAll(SIGNATURE_PATTERN, SIGNATURE_REPLACEMENT);
            sanitized = sanitized.replaceAll("(?i)" + SIGNATURE_PATTERN, SIGNATURE_REPLACEMENT);
            
            // Remove email addresses
            sanitized = sanitized.replaceAll(EMAIL_PATTERN, EMAIL_REPLACEMENT);
            sanitized = sanitized.replaceAll("(?i)" + EMAIL_PATTERN, EMAIL_REPLACEMENT);
            
            // Remove phone numbers
            sanitized = sanitized.replaceAll(PHONE_PATTERN, PHONE_REPLACEMENT);
            sanitized = sanitized.replaceAll("(?i)" + PHONE_PATTERN, PHONE_REPLACEMENT);
            
            return sanitized;
        } catch (Exception e) {
            logger.warn("Failed to sanitize payload, returning original", e);
            return rawPayload;
        }
    }
}

