package com.hotelsystems.ai.bookingmanagement.supplier.controller;

import com.hotelsystems.ai.bookingmanagement.supplier.error.BadRequestException;
import com.hotelsystems.ai.bookingmanagement.supplier.error.ConflictException;
import com.hotelsystems.ai.bookingmanagement.supplier.error.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler for supplier package controllers.
 */
@RestControllerAdvice(basePackages = "com.hotelsystems.ai.bookingmanagement.supplier")
public class SupplierExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(NotFoundException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Not Found");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, String>> handleBadRequest(BadRequestException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Bad Request");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, String>> handleConflict(ConflictException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Conflict");
        error.put("message", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Failed");
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((a, b) -> a + "; " + b)
                .orElse("Validation failed");
        error.put("message", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

