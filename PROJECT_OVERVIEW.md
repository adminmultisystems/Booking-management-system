# Hotelsystems.ai - Complete Project Overview

## 📋 Table of Contents
1. [Project Introduction](#project-introduction)
2. [Technology Stack](#technology-stack)
3. [Architecture Overview](#architecture-overview)
4. [Main Components](#main-components)
5. [Database Entities](#database-entities)
6. [API Endpoints](#api-endpoints)
7. [Key Features](#key-features)
8. [Workflow & Business Logic](#workflow--business-logic)
9. [Security](#security)
10. [Configuration](#configuration)
11. [How to Run](#how-to-run)

---

## 🎯 Project Introduction

**Hotelsystems.ai** is a comprehensive Hotel Booking Management System built with Spring Boot. It provides a complete solution for managing hotels, room types, inventory, pricing, and bookings with support for both owner-managed inventory and supplier integrations.

### Key Capabilities:
- Hotel and Room Type Management
- Inventory Allotment Management
- Real-time Availability Checking
- Booking Lifecycle Management (DRAFT → RECHECKING → PENDING_CONFIRMATION → CONFIRMED)
- Offer Search and Recheck
- Pricing Intelligence
- JWT-based Authentication

---

## 🛠 Technology Stack

### Backend Framework
- **Spring Boot 3.5.9**
- **Java 21**
- **Maven** (Build Tool)

### Core Dependencies
- **Spring Web** - REST API development
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **Spring Validation** - Request validation
- **Spring Actuator** - Health checks and monitoring

### Database
- **PostgreSQL** - Production database
- **H2 Database** - Development/testing (in-memory)
- **Flyway** - Database migration tool

### Other Libraries
- **Lombok** - Boilerplate code reduction
- **Jackson** - JSON serialization/deserialization

---

## 🏗 Architecture Overview

### Layered Architecture

```
┌─────────────────────────────────────────┐
│         Controllers (REST APIs)          │
├─────────────────────────────────────────┤
│         Services (Business Logic)        │
├─────────────────────────────────────────┤
│      Adapters (External Integration)     │
├─────────────────────────────────────────┤
│      Repositories (Data Access)         │
├─────────────────────────────────────────┤
│           Database (PostgreSQL/H2)      │
└─────────────────────────────────────────┘
```

### Key Design Patterns
- **Adapter Pattern** - For supplier/owner inventory integration
- **Service Layer Pattern** - Business logic separation
- **Repository Pattern** - Data access abstraction
- **DTO Pattern** - Data transfer objects
- **State Machine Pattern** - Booking status transitions

---

## 📦 Main Components

### 1. **Controllers** (REST Endpoints)

#### Public APIs (`/v1/**`)
- **HotelController** - Get hotel by slug
- **OfferController** - Search offers, recheck offers
- **BookingController** - Create, get, confirm, cancel bookings

#### Admin APIs (`/v1/admin/**`)
- **OnwerHotelController** - Create/update hotels
- **RoomTypeController** - Create/update room types
- **AdminInventoryController** - Bulk inventory upsert, get inventory
- **AdminInventoryUpdateController** - Update individual inventory rows

#### Internal APIs (`/v1/internal/**`)
- **PricingIntelligenceInternalController** - Get pricing quotes

### 2. **Services** (Business Logic)

#### Core Services
- **BookingOrchestrationService** - Manages booking lifecycle
- **OfferService** - Handles offer search and recheck
- **HotelService** - Hotel information retrieval

#### Owner Inventory Services
- **OwnerHotelService** - Hotel CRUD operations
- **RoomTypeService** - Room type management
- **AvailabilityService** - Real-time availability calculation
- **ReservationService** - Inventory reservation management
- **PricingIntelligenceService** - Pricing calculations

### 3. **Adapters** (Integration Layer)

#### Offer Adapters
- **RealOwnerOfferAdapter** - Real database-backed offer search
- **OwnerOfferAdapterStub** - Stub implementation (deprecated)
- **SupplierOfferAdapterStub** - Supplier offer stub

#### Inventory Adapters
- **RealOwnerInventoryAdapter** - Real inventory operations
- **OwnerInventoryAdapter** - Interface for inventory operations

### 4. **Repositories** (Data Access)

- **BookingRepository** - Booking entity operations
- **HotelRepository** - Hotel entity operations
- **RoomTypeRepository** - Room type operations
- **InventoryAllotmentRepository** - Inventory allotment operations
- **InventoryReservationRepository** - Reservation operations

### 5. **Security**

- **SecurityConfig** - Spring Security configuration
- **JwtAuthenticationFilter** - JWT token extraction
- **AuthenticationService** - User authentication logic

---

## 🗄 Database Entities

### Core Entities

#### 1. **BookingEntity** (`bookings_core`)
- Primary Key: `id` (UUID, auto-generated)
- Status: DRAFT → RECHECKING → PENDING_CONFIRMATION → CONFIRMED → CANCELLED
- Source: OWNER or SUPPLIER
- Contains: Guest info, dates, room details, price snapshots

#### 2. **HotelEntity** (`hotels`)
- Primary Key: `id` (String, user-provided)
- Fields: name, active, createdAt, updatedAt
- Example IDs: "hotel-001", "hotel-07"

#### 3. **RoomTypeEntity** (`room_types`)
- Primary Key: `id` (String, user-provided)
- Foreign Key: `hotel_id`
- Fields: name, maxGuests, active
- Example IDs: "DELUXE", "SUITE", "STANDARD"

#### 4. **InventoryAllotmentEntity** (`inventory_allotments`)
- Primary Key: `id` (UUID, auto-generated)
- Unique Constraint: (hotel_id, room_type_id, date)
- Fields: allotmentQty, stopSell, date

#### 5. **InventoryReservationEntity** (`inventory_reservations`)
- Primary Key: `id` (UUID, auto-generated)
- Foreign Key: `booking_id`
- Status: RESERVED → RELEASED
- Fields: checkIn, checkOut, roomsCount

#### 6. **PricingEntity** (`pricing`)
- Primary Key: `id` (UUID, auto-generated)
- Unique Constraint: (hotel_id, room_type_id, date)
- Fields: basePrice, discountPrice, currency

---

## 🔌 API Endpoints

### Public APIs (JWT Required)

#### Hotel APIs
```
GET /v1/hotels/{slug}
```
Get hotel information by slug (auto-generated from hotel name)

#### Offer APIs
```
POST /v1/hotels/{slug}/offers:search
POST /v1/offers:recheck
```
Search available offers and recheck offer availability

#### Booking APIs
```
POST   /v1/bookings              - Create booking (DRAFT)
GET    /v1/bookings/{id}        - Get booking details
POST   /v1/bookings/{id}/confirm - Confirm booking
POST   /v1/bookings/{id}/cancel  - Cancel booking
```

### Admin APIs (JWT Required)

#### Hotel Management
```
POST   /v1/admin/hotels         - Create hotel
PATCH  /v1/admin/hotels/{id}    - Update hotel
```

#### Room Type Management
```
POST   /v1/admin/hotels/{hotelId}/room-types - Create room type
PATCH  /v1/admin/room-types/{roomTypeId}     - Update room type
```

#### Inventory Management
```
POST   /v1/admin/hotels/{hotelId}/inventory/bulk-upsert - Bulk set inventory
GET    /v1/admin/hotels/{hotelId}/inventory             - Get inventory
PATCH  /v1/admin/inventory/{inventoryRowId}             - Update inventory row
```

### Internal APIs (JWT Required)
```
POST /v1/internal/pricing/quote - Get pricing quote
```

### Health Check (No Auth)
```
GET /actuator/health - System health check
```

---

## ✨ Key Features

### 1. **Real-time Inventory Management**
- ✅ Bulk inventory upsert for date ranges
- ✅ Real-time availability calculation
- ✅ Reservation locking (PESSIMISTIC_WRITE)
- ✅ Stop-sell functionality
- ✅ Past date validation

### 2. **Booking Lifecycle Management**
- ✅ State machine-based status transitions
- ✅ Idempotency support
- ✅ Automatic expiration (15 minutes for DRAFT)
- ✅ Recheck before confirmation
- ✅ Inventory reservation on confirmation

### 3. **Offer Search & Recheck**
- ✅ Database-backed real room type search
- ✅ Real availability checking
- ✅ Pricing intelligence integration
- ✅ Guest capacity validation

### 4. **Security**
- ✅ JWT token authentication
- ✅ User ID extraction from token
- ✅ Endpoint-level authorization
- ✅ CORS configuration

### 5. **Data Validation**
- ✅ Request validation (@Valid, @NotNull, @NotBlank)
- ✅ Date validation (future dates only)
- ✅ Business rule validation (room type existence, hotel ownership)
- ✅ Guest capacity validation

### 6. **Slug Generation**
- ✅ Automatic slug generation from hotel name
- ✅ Format: lowercase, spaces → hyphens
- ✅ Example: "Grand Hotel" → "grand-hotel"

---

## 🔄 Workflow & Business Logic

### Booking Flow

```
1. CREATE BOOKING (DRAFT)
   ↓
2. CONFIRM BOOKING
   ↓
3. RECHECK (Availability Check)
   ↓
4. PENDING_CONFIRMATION
   ↓
5. RESERVE INVENTORY
   ↓
6. CONFIRMED
```

### Offer Search Flow

```
1. Search Request (hotel slug, dates, guests)
   ↓
2. Resolve Hotel by Slug
   ↓
3. Fetch Active Room Types from Database
   ↓
4. Check Availability for Each Room Type
   ↓
5. Get Pricing for Available Rooms
   ↓
6. Return Offers List
```

### Inventory Management Flow

```
1. Create Hotel
   ↓
2. Create Room Types
   ↓
3. Set Inventory (Bulk Upsert)
   ↓
4. Inventory Validated (Room Type Exists, Future Dates)
   ↓
5. Inventory Available for Bookings
```

### Availability Calculation Logic

```
For each night in date range:
  1. Check if allotment exists → if not, availability = 0
  2. Check if stopSell = true → if yes, availability = 0
  3. Calculate reservedCount (sum of RESERVED reservations)
  4. available = max(0, allotmentQty - reservedCount)
  5. Return minimum availability across all nights
```

---

## 🔐 Security

### Authentication
- **Method**: JWT Token in `Authorization: Bearer {token}` header
- **Token Format**: `user-{userId}` (stub implementation)
- **Extraction**: `JwtAuthenticationFilter` extracts userId from token

### Authorization
- **Secured Endpoints**: All `/v1/**` endpoints require JWT
- **Public Endpoints**: `/actuator/health`, `/h2-console/**`
- **User Context**: Stored in `SecurityContext` for service layer access

### Security Configuration
- **SecurityConfig**: Configures JWT filter and endpoint rules
- **CORS**: Configured for cross-origin requests
- **H2 Console**: Enabled for development (should be disabled in production)

---

## ⚙️ Configuration

### Application Profiles

#### H2 Profile (Development)
```yaml
spring:
  profiles:
    active: h2
  datasource:
    url: jdbc:h2:mem:hoteldb
    driver-class-name: org.h2.Driver
```

#### PostgreSQL Profile (Production)
```yaml
spring:
  profiles:
    active: postgres
  datasource:
    url: jdbc:postgresql://localhost:5432/hoteldb
    username: postgres
    password: postgres
```

### Key Configuration Files
- `application.yml` - Main configuration (merged from multiple profiles)
- `application-h2.yml` - H2 database config (now in main file)
- `application-postgres.yml` - PostgreSQL config (now in main file)

### Database Migration
- **Flyway** handles database schema migration
- Migration files in: `src/main/resources/db/migration/`
- Baseline: `V1__baseline.sql`

---

## 🚀 How to Run

### Prerequisites
- Java 21 JDK
- Maven 3.6+
- PostgreSQL (for production) or H2 (for development)

### Steps

1. **Clone/Download Project**
   ```bash
   cd Hotelsystems.ai
   ```

2. **Build Project**
   ```bash
   mvn clean install
   ```

3. **Run with H2 (Development)**
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=h2
   ```
   Or set in `application.yml`:
   ```yaml
   spring:
     profiles:
       active: h2
   ```

4. **Run with PostgreSQL (Production)**
   - Start PostgreSQL
   - Update `application.yml` with database credentials
   - Run:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=postgres
   ```

5. **Access Application**
   - Base URL: `http://localhost:8080`
   - Health Check: `http://localhost:8080/actuator/health`
   - H2 Console: `http://localhost:8080/h2-console` (if H2 profile active)

### Testing APIs

1. **Import Postman Collection**
   - File: `Final_Working_Postman_Collection.json`
   - Contains all APIs with proper workflow

2. **Set Base URL**
   - Variable: `{{baseUrl}}` = `http://localhost:8080`

3. **Set JWT Token**
   - Header: `Authorization: Bearer user-123`
   - (Replace `user-123` with actual userId)

4. **Follow Workflow**
   - Phase 1: Setup (Create Hotel → Room Type → Inventory)
   - Phase 2: Search & Book (Search Offers → Create Booking → Confirm)
   - Phase 3: Management (Update, Cancel, etc.)

---

## 📊 Database Schema Summary

### Tables

1. **bookings_core** - Booking records
2. **hotels** - Hotel information
3. **room_types** - Room type definitions
4. **inventory_allotments** - Daily inventory allotments
5. **inventory_reservations** - Booking reservations
6. **pricing** - Pricing data (optional)

### Key Relationships

```
hotels (1) ──→ (N) room_types
room_types (1) ──→ (N) inventory_allotments
bookings_core (1) ──→ (N) inventory_reservations
```

---

## 🔍 Important Notes

### Current Implementation Status

✅ **Fully Implemented:**
- Hotel & Room Type Management
- Inventory Management with Validation
- Real-time Availability Checking
- Booking Lifecycle Management
- Real Database-backed Offer Search
- Pricing Intelligence

⚠️ **Stub/Partial Implementation:**
- JWT Authentication (stub - extracts userId from token)
- Supplier Integration (stub adapters)
- Pricing (hardcoded rates based on room type)

### Best Practices Followed

- ✅ Layered Architecture
- ✅ Separation of Concerns
- ✅ DTO Pattern for API contracts
- ✅ Transaction Management
- ✅ Exception Handling
- ✅ Input Validation
- ✅ Logging
- ✅ Database Migration (Flyway)

### Future Enhancements

- Real JWT token validation
- Supplier API integration
- Dynamic pricing from database
- Payment integration
- Email notifications
- Booking history and analytics
- Multi-currency support
- Rate plans and promotions

---

## 📝 File Structure

```
Hotelsystems.ai/
├── src/
│   ├── main/
│   │   ├── java/com/hotelsystems/ai/
│   │   │   ├── Application.java
│   │   │   └── bookingmanagement/
│   │   │       ├── adapter/          # Integration adapters
│   │   │       ├── auth/             # Authentication
│   │   │       ├── config/           # Configuration
│   │   │       ├── controller/       # REST controllers
│   │   │       ├── domain/           # Domain entities
│   │   │       ├── dto/              # Data transfer objects
│   │   │       ├── enums/            # Enumerations
│   │   │       ├── exception/        # Exception handling
│   │   │       ├── ownerinventory/   # Owner inventory module
│   │   │       ├── repository/       # Data repositories
│   │   │       ├── service/          # Business services
│   │   │       └── util/             # Utility classes
│   │   └── resources/
│   │       ├── application.yml       # Main configuration
│   │       └── db/migration/         # Flyway migrations
│   └── test/                         # Test files
├── pom.xml                           # Maven dependencies
├── Final_Working_Postman_Collection.json  # API testing collection
└── PROJECT_OVERVIEW.md              # This file
```

---

## 📞 Support & Documentation

- **Postman Collection**: `Final_Working_Postman_Collection.json`
- **API Workflow**: See Postman collection for step-by-step flow
- **Database**: Check Flyway migrations for schema

---

**Last Updated**: 2026-01-09  
**Version**: 0.0.1-SNAPSHOT  
**Java Version**: 21  
**Spring Boot Version**: 3.5.9

