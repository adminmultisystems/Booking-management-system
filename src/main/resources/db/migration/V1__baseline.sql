-- ============================================================================
-- FLYWAY BASELINE MIGRATION - Complete Schema
-- ============================================================================
-- 
-- This migration creates all core tables for the Hotelsystems.ai application.
-- Compatible with both H2 (PostgreSQL mode) and PostgreSQL.
--
-- Tables created:
-- 1. bookings_core - Core booking entity
-- 2. hotels - Hotel master data
-- 3. room_types - Room type master data
-- 4. inventory_allotments - Daily inventory allotments
-- 5. inventory_reservations - Inventory reservations for bookings
-- 6. pricing - Daily pricing for room types
--
-- Note: supplier_hotel_mapping is created by V2 migration
-- ============================================================================

-- ============================================================================
-- 1. HOTELS TABLE
-- ============================================================================
CREATE TABLE hotels (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- ============================================================================
-- 2. ROOM_TYPES TABLE
-- ============================================================================
CREATE TABLE room_types (
    id VARCHAR(255) NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    max_guests INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    PRIMARY KEY (id, hotel_id),
    CONSTRAINT uk_hotel_roomtype_id UNIQUE (hotel_id, id),
    CONSTRAINT uk_hotel_roomtype_name UNIQUE (hotel_id, name)
);

CREATE INDEX idx_hotel_id ON room_types(hotel_id);

-- ============================================================================
-- 3. BOOKINGS_CORE TABLE
-- ============================================================================
CREATE TABLE bookings_core (
    id UUID NOT NULL PRIMARY KEY,
    user_id VARCHAR(100) NOT NULL,
    hotel_id VARCHAR(100) NOT NULL,
    room_type_id VARCHAR(100) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    source VARCHAR(20) NOT NULL,
    guest_name VARCHAR(255) NOT NULL,
    guest_email VARCHAR(255) NOT NULL,
    guest_phone VARCHAR(50) NOT NULL,
    special_requests TEXT,
    supplier_code VARCHAR(50),
    supplier_hotel_id VARCHAR(100),
    supplier_booking_ref VARCHAR(100),
    supplier_rate_key VARCHAR(255),
    internal_confirmation_ref VARCHAR(100),
    offer_id VARCHAR(100),
    offer_payload_json TEXT,
    price_snapshot_json TEXT,
    policy_snapshot_json TEXT,
    occupancy_adults INTEGER,
    occupancy_children INTEGER,
    guests_json TEXT,
    children_ages_json TEXT,
    lead_guest_json TEXT,
    rooms_count INTEGER,
    adults INTEGER,
    children INTEGER,
    idempotency_key VARCHAR(255),
    expires_at TIMESTAMP,
    next_actions_json TEXT,
    failure_reason TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes for bookings_core
CREATE INDEX idx_bookings_core_hotel_id ON bookings_core(hotel_id);
CREATE INDEX idx_bookings_core_status ON bookings_core(status);
CREATE INDEX idx_bookings_core_created_at ON bookings_core(created_at);
CREATE INDEX idx_bookings_core_user_id ON bookings_core(user_id);
CREATE INDEX idx_bookings_core_idempotency_key ON bookings_core(idempotency_key);
CREATE INDEX idx_bookings_core_offer_id ON bookings_core(offer_id);

-- ============================================================================
-- 4. INVENTORY_ALLOTMENTS TABLE
-- ============================================================================
CREATE TABLE inventory_allotments (
    id UUID NOT NULL PRIMARY KEY,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    date DATE NOT NULL,
    allotment_qty INTEGER NOT NULL,
    stop_sell BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    CONSTRAINT uk_hotel_roomtype_date UNIQUE (hotel_id, room_type_id, date)
);

-- Indexes for inventory_allotments
CREATE INDEX idx_hotel_date ON inventory_allotments(hotel_id, date);
CREATE INDEX idx_hotel_roomtype_date ON inventory_allotments(hotel_id, room_type_id, date);

-- ============================================================================
-- 5. INVENTORY_RESERVATIONS TABLE
-- ============================================================================
CREATE TABLE inventory_reservations (
    id UUID NOT NULL PRIMARY KEY,
    booking_id UUID NOT NULL,
    hotel_id VARCHAR(255) NOT NULL,
    room_type_id VARCHAR(255) NOT NULL,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    rooms_count INTEGER NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- Indexes for inventory_reservations
CREATE INDEX idx_booking_id ON inventory_reservations(booking_id);
CREATE INDEX idx_hotel_roomtype ON inventory_reservations(hotel_id, room_type_id);

-- ============================================================================
-- 6. PRICING TABLE
-- ============================================================================
CREATE TABLE pricing (
    id UUID NOT NULL PRIMARY KEY,
    hotel_id UUID NOT NULL,
    room_type_id UUID NOT NULL,
    date DATE NOT NULL,
    base_price DECIMAL(10,2) NOT NULL,
    discount_price DECIMAL(10,2),
    currency VARCHAR(255) NOT NULL DEFAULT 'USD',
    CONSTRAINT uk_pricing_hotel_roomtype_date UNIQUE (hotel_id, room_type_id, date)
);