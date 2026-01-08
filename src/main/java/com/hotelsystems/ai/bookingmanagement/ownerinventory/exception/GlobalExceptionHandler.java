package com.hotelsystems.ai.bookingmanagement.ownerinventory.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Global exception handler for REST controllers.
 * Handles all exceptions and maps them to appropriate HTTP status codes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handles NotFoundException - returns 404 Not Found.
     */
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFoundException(NotFoundException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Not Found");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }
    
    /**
     * Handles DuplicateException - returns 409 Conflict.
     */
    @ExceptionHandler(DuplicateException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateException(DuplicateException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handles ConflictException - returns 409 Conflict.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflictException(ConflictException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Conflict");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
    }
    
    /**
     * Handles IllegalArgumentException - returns 400 Bad Request.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Bad Request");
        errorResponse.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles validation errors from @Valid annotations - returns 400 Bad Request.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", "Validation Failed");
        
        // Extract field errors and create a detailed message
        String errorMessage = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        
        errorResponse.put("message", errorMessage);
        errorResponse.put("details", ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .collect(Collectors.toMap(
                error -> error.getField(),
                error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value"
            )));
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handles all other exceptions - returns 500 Internal Server Error.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(Exception ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal Server Error");
        errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : "An unexpected error occurred");
        // In production, you might want to log the full stack trace here
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}

