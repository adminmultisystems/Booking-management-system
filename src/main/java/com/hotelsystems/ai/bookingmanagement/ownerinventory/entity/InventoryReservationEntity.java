package com.hotelsystems.ai.bookingmanagement.ownerinventory.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations",
       indexes = {
           @Index(name = "idx_booking_id", columnList = "booking_id"),
           @Index(name = "idx_hotel_roomtype", columnList = "hotel_id,room_type_id")
       })
public class InventoryReservationEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;
    
    @Column(name = "hotel_id", nullable = false)
    private String hotelId;
    
    @Column(name = "room_type_id", nullable = false)
    private String roomTypeId;
    
    @Column(name = "check_in", nullable = false)
    private LocalDate checkIn;
    
    @Column(name = "check_out", nullable = false)
    private LocalDate checkOut;
    
    @Column(name = "rooms_count", nullable = false)
    private int roomsCount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    
    // Constructors
    public InventoryReservationEntity() {
    }
    
    public InventoryReservationEntity(UUID bookingId, String hotelId, String roomTypeId, 
                                     LocalDate checkIn, LocalDate checkOut, int roomsCount) {
        this.bookingId = bookingId;
        this.hotelId = hotelId;
        this.roomTypeId = roomTypeId;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.roomsCount = roomsCount;
        this.status = ReservationStatus.RESERVED;
    }
    
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = ReservationStatus.RESERVED;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
    
    // Getters and Setters
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public UUID getBookingId() {
        return bookingId;
    }
    
    public void setBookingId(UUID bookingId) {
        this.bookingId = bookingId;
    }
    
    public String getHotelId() {
        return hotelId;
    }
    
    public void setHotelId(String hotelId) {
        this.hotelId = hotelId;
    }
    
    public String getRoomTypeId() {
        return roomTypeId;
    }
    
    public void setRoomTypeId(String roomTypeId) {
        this.roomTypeId = roomTypeId;
    }
    
    public LocalDate getCheckIn() {
        return checkIn;
    }
    
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
    }
    
    public LocalDate getCheckOut() {
        return checkOut;
    }
    
    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
    }
    
    public int getRoomsCount() {
        return roomsCount;
    }
    
    public void setRoomsCount(int roomsCount) {
        this.roomsCount = roomsCount;
    }
    
    public ReservationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ReservationStatus status) {
        this.status = status;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    public Instant getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}

