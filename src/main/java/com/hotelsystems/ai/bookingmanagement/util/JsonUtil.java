package com.hotelsystems.ai.bookingmanagement.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * JSON Utility
 * 
 * Helper methods for safe JSON serialization and deserialization using Jackson ObjectMapper.
 * Handles errors gracefully and returns null on failure.
 */
@Component
@Slf4j
public class JsonUtil {
    
    private final ObjectMapper objectMapper;
    
    public JsonUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    
    /**
     * Serialize object to JSON string
     * 
     * @param object Object to serialize
     * @return JSON string or null if serialization fails
     */
    public String toJson(Object object) {
        if (object == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            log.warn("Failed to serialize object to JSON: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Deserialize JSON string to object of specified type
     * 
     * @param json JSON string
     * @param clazz Target class
     * @param <T> Type
     * @return Deserialized object or null if deserialization fails
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.warn("Failed to deserialize JSON to {}: {}", clazz.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Deserialize JSON string to List of specified type
     * 
     * @param json JSON string
     * @param elementClass Element class
     * @param <T> Type
     * @return List of deserialized objects or null if deserialization fails
     */
    public <T> List<T> fromJsonList(String json, Class<T> elementClass) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass)
            );
        } catch (Exception e) {
            log.warn("Failed to deserialize JSON to List<{}>: {}", elementClass.getSimpleName(), e.getMessage());
            return null;
        }
    }
    
    /**
     * Deserialize JSON string to Map
     * 
     * @param json JSON string
     * @return Map or null if deserialization fails
     */
    public Map<String, Object> fromJsonMap(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class)
            );
        } catch (Exception e) {
            log.warn("Failed to deserialize JSON to Map: {}", e.getMessage());
            return null;
        }
    }
    
    /**
     * Deserialize JSON array string to List of Integers (for childrenAgesJson)
     * 
     * @param json JSON array string
     * @return List of integers or null if deserialization fails
     */
    public List<Integer> fromJsonIntegerList(String json) {
        if (json == null || json.trim().isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Integer.class)
            );
        } catch (Exception e) {
            log.warn("Failed to deserialize JSON to List<Integer>: {}", e.getMessage());
            return null;
        }
    }
}

