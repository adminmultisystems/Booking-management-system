package com.hotelsystems.ai.bookingmanagement.supplier.controller.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierMappingResponse;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.UpsertSupplierMappingRequest;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import com.hotelsystems.ai.bookingmanagement.supplier.error.ConflictException;
import com.hotelsystems.ai.bookingmanagement.supplier.error.NotFoundException;
import com.hotelsystems.ai.bookingmanagement.supplier.service.SupplierMappingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SupplierMappingAdminController.class)
class SupplierMappingAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupplierMappingService service;

    @Autowired
    private ObjectMapper objectMapper;

    private String hotelId = "hotel-123";

    @Test
    void testPostMapping_ThenGetMappings_ReturnsIt() throws Exception {
        // Given: POST request
        UpsertSupplierMappingRequest request = new UpsertSupplierMappingRequest();
        request.setSupplierCode(SupplierCode.HOTELBEDS);
        request.setSupplierHotelId("HB-123");
        request.setStatus(SupplierMappingStatus.ACTIVE);

        SupplierMappingResponse response = new SupplierMappingResponse(
                hotelId,
                SupplierCode.HOTELBEDS,
                "HB-123",
                SupplierMappingStatus.ACTIVE,
                Instant.now(),
                Instant.now()
        );

        when(service.upsert(eq(hotelId), any(UpsertSupplierMappingRequest.class)))
                .thenReturn(response);

        // When: POST mapping
        mockMvc.perform(post("/v1/admin/hotels/{hotelId}/supplier-mapping", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hotelId").value(hotelId))
                .andExpect(jsonPath("$.supplierCode").value("HOTELBEDS"))
                .andExpect(jsonPath("$.supplierHotelId").value("HB-123"));

        // Then: GET mappings
        List<SupplierMappingResponse> mappings = Arrays.asList(response);
        when(service.getMappings(hotelId)).thenReturn(mappings);

        mockMvc.perform(get("/v1/admin/hotels/{hotelId}/supplier-mapping", hotelId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].hotelId").value(hotelId))
                .andExpect(jsonPath("$[0].supplierCode").value("HOTELBEDS"));
    }

    @Test
    void testPostSecondActive_Returns409() throws Exception {
        // Given: First ACTIVE mapping exists
        UpsertSupplierMappingRequest request = new UpsertSupplierMappingRequest();
        request.setSupplierCode(SupplierCode.TRAVELLANDA);
        request.setSupplierHotelId("TL-456");
        request.setStatus(SupplierMappingStatus.ACTIVE);

        when(service.upsert(eq(hotelId), any(UpsertSupplierMappingRequest.class)))
                .thenThrow(new ConflictException("Cannot set supplier TRAVELLANDA as ACTIVE: hotel already has ACTIVE supplier HOTELBEDS"));

        // When/Then: POST second ACTIVE returns 409
        mockMvc.perform(post("/v1/admin/hotels/{hotelId}/supplier-mapping", hotelId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void testGetMappings_NotFound_Returns404() throws Exception {
        // Given: No mappings exist
        when(service.getMappings(hotelId))
                .thenThrow(new NotFoundException("No supplier mappings found for hotel: " + hotelId));

        // When/Then: GET returns 404
        mockMvc.perform(get("/v1/admin/hotels/{hotelId}/supplier-mapping", hotelId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").exists());
    }
}

