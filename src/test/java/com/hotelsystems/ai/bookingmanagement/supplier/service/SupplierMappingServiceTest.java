package com.hotelsystems.ai.bookingmanagement.supplier.service;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.dto.UpsertSupplierMappingRequest;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingEntity;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import com.hotelsystems.ai.bookingmanagement.supplier.error.ConflictException;
import com.hotelsystems.ai.bookingmanagement.supplier.repo.SupplierHotelMappingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierMappingServiceTest {

    @Mock
    private SupplierHotelMappingRepository repository;

    @InjectMocks
    private SupplierMappingService service;

    private String hotelId;
    private UpsertSupplierMappingRequest request;

    @BeforeEach
    void setUp() {
        hotelId = "hotel-123";
        request = new UpsertSupplierMappingRequest();
        request.setSupplierCode(SupplierCode.HOTELBEDS);
        request.setSupplierHotelId("HB-123");
        request.setStatus(SupplierMappingStatus.ACTIVE);
    }

    @Test
    void testUpsert_ActiveRequiresSupplierHotelId() {
        // Given: ACTIVE status without supplierHotelId
        request.setSupplierHotelId(null);

        // When/Then: Should throw ConflictException
        assertThrows(ConflictException.class, () -> {
            service.upsert(hotelId, request);
        });
    }

    @Test
    void testUpsert_ActiveRequiresNonBlankSupplierHotelId() {
        // Given: ACTIVE status with blank supplierHotelId
        request.setSupplierHotelId("   ");

        // When/Then: Should throw ConflictException
        assertThrows(ConflictException.class, () -> {
            service.upsert(hotelId, request);
        });
    }

    @Test
    void testUpsert_ConflictWhenSettingSecondActiveSupplier() {
        // Given: Existing ACTIVE mapping for different supplier
        SupplierHotelMappingEntity existing = new SupplierHotelMappingEntity();
        existing.setHotelId(hotelId);
        existing.setSupplierCode(SupplierCode.TRAVELLANDA);
        existing.setStatus(SupplierMappingStatus.ACTIVE);

        when(repository.findFirstByHotelIdAndStatus(hotelId, SupplierMappingStatus.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(repository.findByHotelIdAndSupplierCode(hotelId, SupplierCode.HOTELBEDS))
                .thenReturn(Optional.empty());

        // When/Then: Should throw ConflictException
        assertThrows(ConflictException.class, () -> {
            service.upsert(hotelId, request);
        });
    }

    @Test
    void testUpsert_SuccessWhenUpdatingSameSupplierToActive() {
        // Given: Existing ACTIVE mapping for same supplier
        SupplierHotelMappingEntity existing = new SupplierHotelMappingEntity();
        existing.setHotelId(hotelId);
        existing.setSupplierCode(SupplierCode.HOTELBEDS);
        existing.setStatus(SupplierMappingStatus.ACTIVE);

        when(repository.findFirstByHotelIdAndStatus(hotelId, SupplierMappingStatus.ACTIVE))
                .thenReturn(Optional.of(existing));
        when(repository.findByHotelIdAndSupplierCode(hotelId, SupplierCode.HOTELBEDS))
                .thenReturn(Optional.of(existing));
        when(repository.save(any(SupplierHotelMappingEntity.class))).thenReturn(existing);

        // When: Should succeed
        assertDoesNotThrow(() -> {
            service.upsert(hotelId, request);
        });

        verify(repository).save(any(SupplierHotelMappingEntity.class));
    }

    @Test
    void testGetMappings_ThrowsNotFoundWhenEmpty() {
        // Given: No mappings exist
        when(repository.findByHotelId(hotelId)).thenReturn(new ArrayList<>());

        // When/Then: Should throw NotFoundException
        assertThrows(com.hotelsystems.ai.bookingmanagement.supplier.error.NotFoundException.class, () -> {
            service.getMappings(hotelId);
        });
    }
}

