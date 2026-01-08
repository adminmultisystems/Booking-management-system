package com.hotelsystems.ai.bookingmanagement.ownerinventory.pricing;

import com.hotelsystems.ai.bookingmanagement.ownerinventory.entity.PricingEntity;
import com.hotelsystems.ai.bookingmanagement.ownerinventory.repository.PricingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class PricingService {
    
    private final PricingRepository pricingRepository;
    
    public PricingService(PricingRepository pricingRepository) {
        this.pricingRepository = pricingRepository;
    }
    
    /**
     * Calculates the total price for a date range.
     */
    @Transactional(readOnly = true)
    public BigDecimal calculateTotalPrice(UUID hotelId, UUID roomTypeId, LocalDate checkIn, LocalDate checkOut) {
        List<PricingEntity> pricingList = pricingRepository.findPricingForDateRange(
            hotelId, roomTypeId, checkIn, checkOut);
        
        return pricingList.stream()
            .map(PricingEntity::getEffectivePrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * Gets the price for a specific date.
     */
    @Transactional(readOnly = true)
    public BigDecimal getPriceForDate(UUID hotelId, UUID roomTypeId, LocalDate date) {
        return pricingRepository.findByHotelIdAndRoomTypeIdAndDate(hotelId, roomTypeId, date)
            .map(PricingEntity::getEffectivePrice)
            .orElse(BigDecimal.ZERO);
    }
    
    /**
     * Gets or creates pricing for a specific date.
     */
    @Transactional
    public PricingEntity getOrCreatePricing(UUID hotelId, UUID roomTypeId, LocalDate date, BigDecimal defaultPrice) {
        return pricingRepository.findByHotelIdAndRoomTypeIdAndDate(hotelId, roomTypeId, date)
            .orElseGet(() -> {
                PricingEntity newPricing = new PricingEntity(hotelId, roomTypeId, date, defaultPrice);
                return pricingRepository.save(newPricing);
            });
    }
}

