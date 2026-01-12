package com.hotelsystems.ai.bookingmanagement.supplier.repo;

import com.hotelsystems.ai.bookingmanagement.supplier.dto.SupplierCode;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingEntity;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierHotelMappingId;
import com.hotelsystems.ai.bookingmanagement.supplier.entity.SupplierMappingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for supplier hotel mapping entities.
 */
@Repository
public interface SupplierHotelMappingRepository extends JpaRepository<SupplierHotelMappingEntity, SupplierHotelMappingId> {

    /**
     * Find all mappings for a given hotel ID.
     */
    List<SupplierHotelMappingEntity> findByHotelId(String hotelId);

    /**
     * Find a mapping by hotel ID and supplier code.
     */
    Optional<SupplierHotelMappingEntity> findByHotelIdAndSupplierCode(String hotelId, SupplierCode supplierCode);

    /**
     * Find the first ACTIVE mapping for a given hotel ID.
     */
    Optional<SupplierHotelMappingEntity> findFirstByHotelIdAndStatus(String hotelId, SupplierMappingStatus status);
}

