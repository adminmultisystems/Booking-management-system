package com.hotelsystems.ai.bookingmanagement.ownerinventory.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "pricing",
       uniqueConstraints = @UniqueConstraint(columnNames = {"hotel_id", "room_type_id", "date"}))
public class PricingEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "hotel_id", nullable = false)
    private UUID hotelId;
    
    @Column(name = "room_type_id", nullable = false)
    private UUID roomTypeId;
    
    @Column(nullable = false)
    private LocalDate date;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal basePrice;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal discountPrice;
    
    @Column(nullable = false)
    private String currency = "USD";
    
    // Constructors
    public PricingEntity() {
    }
    
    public PricingEntity(UUID hotelId, UUID roomTypeId, LocalDate date, BigDecimal basePrice) {
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.date = date;
        this.basePrice = basePrice;
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(UUID hotelId) {
        this.hotelId = hotelId;
    }
    
    public UUID getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(UUID roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public BigDecimal getBasePrice() {
        return basePrice;
    }
    
    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }
    
    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }
    
    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }
    
    public String getCurrency() {
        return currency;
    }
    
    public void setCurrency(String currency) {
        this.currency = currency;
    }
    
    public BigDecimal getEffectivePrice() {
        return discountPrice != null && discountPrice.compareTo(basePrice) < 0 
            ? discountPrice 
            : basePrice;
    }
}

