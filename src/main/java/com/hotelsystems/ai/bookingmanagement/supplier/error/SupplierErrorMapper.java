package com.hotelsystems.ai.bookingmanagement.supplier.error;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.RecheckResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

/**
 * Utility class for mapping HTTP responses and exceptions to supplier error types.
 * Ensures consistent error handling across all supplier implementations.
 */
public class SupplierErrorMapper {

    private static final Logger logger = LoggerFactory.getLogger(SupplierErrorMapper.class);

    /**
     * Maps HTTP client errors to appropriate RecheckResult or exception.
     * 
     * @param e the HTTP client error exception
     * @param bookingId the booking ID for logging
     * @param supplierCode the supplier code for logging
     * @return RecheckResult for recoverable errors (SOLD_OUT), throws exception for others
     */
    public static RecheckResult mapRecheckClientError(HttpClientErrorException e, String bookingId, String supplierCode) {
        HttpStatusCode status = e.getStatusCode();
        
        if (status.value() == HttpStatus.NOT_FOUND.value() || status.value() == HttpStatus.GONE.value()) {
            // Invalid rateKey - treat as SOLD_OUT unless clearly client bug
            logger.warn("Rate not found (treating as sold out) [bookingId={}, supplierCode={}, status={}]", 
                    bookingId, supplierCode, status);
            return RecheckResult.unavailable("Rate is no longer available");
        }
        
        if (status.is4xxClientError()) {
            logger.error("Client error during recheck [bookingId={}, supplierCode={}, status={}]", 
                    bookingId, supplierCode, status, e);
            throw new BadRequestException("Invalid request to supplier: " + e.getMessage(), e);
        }
        
        logger.error("Unexpected client error during recheck [bookingId={}, supplierCode={}, status={}]", 
                bookingId, supplierCode, status, e);
        throw new BadRequestException("Supplier request failed: " + e.getMessage(), e);
    }

    /**
     * Maps HTTP client errors during booking creation to appropriate exception.
     * 
     * @param e the HTTP client error exception
     * @param bookingId the booking ID for logging
     * @param supplierCode the supplier code for logging
     * @throws ConflictException if rate is SOLD_OUT
     * @throws BadRequestException for other client errors
     */
    public static void mapBookClientError(HttpClientErrorException e, String bookingId, String supplierCode) {
        HttpStatusCode status = e.getStatusCode();
        
        if (status.value() == HttpStatus.NOT_FOUND.value() || status.value() == HttpStatus.GONE.value()) {
            // Invalid rateKey - treat as SOLD_OUT
            logger.warn("Rate not found during booking (treating as sold out) [bookingId={}, supplierCode={}, status={}]", 
                    bookingId, supplierCode, status);
            throw new ConflictException("Rate is no longer available");
        }
        
        logger.error("Client error during booking [bookingId={}, supplierCode={}, status={}]", 
                bookingId, supplierCode, status, e);
        throw new BadRequestException("Invalid request to supplier: " + e.getMessage(), e);
    }

    /**
     * Maps HTTP client errors during cancellation to appropriate exception.
     * 
     * @param e the HTTP client error exception
     * @param bookingId the booking ID for logging
     * @param bookingRef the booking reference for logging
     * @param supplierCode the supplier code for logging
     * @throws BadRequestException for client errors
     */
    public static void mapCancelClientError(HttpClientErrorException e, String bookingId, String bookingRef, String supplierCode) {
        HttpStatusCode status = e.getStatusCode();
        logger.error("Client error during cancellation [bookingId={}, supplierCode={}, bookingRef={}, status={}]", 
                bookingId, supplierCode, bookingRef, status.value(), e);
        throw new BadRequestException("Invalid cancellation request: " + e.getMessage(), e);
    }

    /**
     * Maps server errors and timeouts to ConflictException (TEMP_UNAVAILABLE).
     * 
     * @param e the exception (HttpServerErrorException or ResourceAccessException)
     * @param bookingId the booking ID for logging
     * @param operation the operation name for logging
     * @param supplierCode the supplier code for logging
     * @return ConflictException with appropriate message
     */
    public static ConflictException mapServerError(Exception e, String bookingId, String operation, String supplierCode) {
        if (e instanceof ResourceAccessException) {
            Throwable cause = e.getCause();
            if (cause instanceof SocketTimeoutException) {
                logger.error("Timeout during {} [bookingId={}, supplierCode={}]", 
                        operation, bookingId, supplierCode, e);
            } else {
                logger.error("Connection error during {} [bookingId={}, supplierCode={}]", 
                        operation, bookingId, supplierCode, e);
            }
        } else if (e instanceof HttpServerErrorException) {
            HttpServerErrorException httpError = (HttpServerErrorException) e;
            logger.error("Server error during {} [bookingId={}, supplierCode={}, status={}]", 
                    operation, bookingId, supplierCode, httpError.getStatusCode(), e);
        } else {
            logger.error("Unexpected error during {} [bookingId={}, supplierCode={}]", 
                    operation, bookingId, supplierCode, e);
        }
        
        return new ConflictException("Supplier temporarily unavailable", e);
    }

    /**
     * Checks if an HTTP status indicates the booking was already cancelled.
     * Used to swallow "already cancelled" errors as success.
     * 
     * @param status the HTTP status code
     * @return true if the status indicates already cancelled
     */
    public static boolean isAlreadyCancelled(HttpStatusCode status) {
        return status.value() == HttpStatus.NOT_FOUND.value() || status.value() == HttpStatus.GONE.value();
    }
}

