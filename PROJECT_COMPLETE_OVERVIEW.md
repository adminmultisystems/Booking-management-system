# Hotelsystems.ai - Complete Project Overview
## Booking Management System - Full Documentation

---

## 📋 Table of Contents
1. [Project Introduction](#project-introduction)
2. [Technology Stack](#technology-stack)
3. [Database Tables & Usage](#database-tables--usage)
4. [APIs Documentation (8 APIs)](#apis-documentation)
5. [Project Structure](#project-structure)
6. [Setup & Configuration](#setup--configuration)

---

## 🎯 Project Introduction

**Hotelsystems.ai** ek comprehensive booking management system hai jo hotel owners ke liye complete inventory aur room management solution provide karta hai.

### Main Purpose:
- Hotel setup aur management
- Room types configuration
- Inventory allotment management
- Booking availability tracking
- **Internal Pricing Intelligence** (room type-based pricing)

### Project Type:
- **Backend API Service** (Spring Boot REST API)
- **Microservice Architecture** ready
- **Database:** H2 (in-memory) for development, PostgreSQL for production

---

## 🛠️ Technology Stack

### Backend Framework:
- **Spring Boot 3.5.9** - Main framework
- **Java 17+** - Programming language

### Database:
- **H2 Database** - In-memory database for development/testing (default)
- **PostgreSQL** - Production database (configured but optional)

### ORM & Data Access:
- **JPA/Hibernate** - ORM for database operations
- **Spring Data JPA** - Repository pattern implementation

### Build Tool:
- **Maven** - Dependency management and build
- **Maven Wrapper** (mvnw.cmd) - No Maven installation required

---

## 🗄️ Database Tables & Usage

### **Active Tables (Currently Used):**

#### **1. `hotels` Table**
**Entity:** `HotelEntity`  
**Table Name:** `hotels`

**Fields:**
- `id` (String, PK) - Hotel identifier
- `name` (String) - Hotel name
- `active` (Boolean) - Soft delete flag (true = active, false = inactive)
- `created_at` (Instant) - Auto-set on creation
- `updated_at` (Instant) - Auto-updated on modification

**Where Used:**
- ✅ **HotelController** - `POST /v1/admin/hotels` (create hotel)
- ✅ **HotelController** - `PATCH /v1/admin/hotels/{hotelId}` (update hotel)
- ✅ **HotelService** - Business logic for hotel operations
- ✅ **HotelRepository** - Database operations
- ✅ **RoomTypeService** - Validates hotel existence before creating room types

**Purpose:**
- Hotel master data store karta hai
- Soft delete support (active flag)
- Room types aur inventory ke liye parent entity

---

#### **2. `room_types` Table**
**Entity:** `RoomTypeEntity`  
**Table Name:** `room_types`

**Fields:**
- `id` (String, PK) - Room type identifier
- `hotel_id` (String, FK) - Foreign key to hotels table
- `name` (String) - Room type name
- `max_guests` (Integer) - Maximum guests allowed (optional)
- `active` (Boolean) - Soft delete flag
- `created_at` (Instant) - Auto-set on creation
- `updated_at` (Instant) - Auto-updated on modification

**Unique Constraints:**
- `(hotel_id, id)` - Same roomTypeId cannot exist twice for same hotel
- `(hotel_id, name)` - Same room type name cannot exist twice for same hotel

**Where Used:**
- ✅ **RoomTypeController** - `POST /v1/admin/hotels/{hotelId}/room-types` (create room type)
- ✅ **RoomTypeController** - `PATCH /v1/admin/room-types/{roomTypeId}` (update room type)
- ✅ **RoomTypeService** - Business logic for room type operations
- ✅ **RoomTypeRepository** - Database operations
- ✅ **AdminInventoryController** - Inventory operations ke liye room type validation
- ✅ **PricingIntelligenceService** - Room type-based pricing calculation (DELUXE, SUITE, STANDARD)

**Purpose:**
- Room type master data store karta hai
- Hotel ke saath relationship maintain karta hai
- Inventory allotments ke liye reference
- Pricing calculation ke liye room type ID use hota hai

---

#### **3. `inventory_allotments` Table**
**Entity:** `InventoryAllotmentEntity`  
**Table Name:** `inventory_allotments`

**Fields:**
- `id` (UUID, PK) - Auto-generated UUID
- `hotel_id` (String, FK) - Foreign key to hotels table
- `room_type_id` (String, FK) - Foreign key to room_types table
- `date` (LocalDate) - Specific date for inventory
- `allotment_qty` (Integer) - Number of rooms available
- `stop_sell` (Boolean) - If true, bookings blocked for this date
- `created_at` (Instant) - Auto-set on creation
- `updated_at` (Instant) - Auto-updated on modification

**Unique Constraint:**
- `(hotel_id, room_type_id, date)` - One inventory row per hotel+roomType+date combination

**Where Used:**
- ✅ **AdminInventoryController** - `POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert` (bulk create/update)
- ✅ **AdminInventoryController** - `GET /v1/admin/hotels/{hotelId}/inventory` (get inventory with filters)
- ✅ **AdminInventoryUpdateController** - `PATCH /v1/admin/inventory/{inventoryRowId}` (update single row)
- ✅ **InventoryAllotmentRepository** - Database operations
- ✅ **AvailabilityService** - Availability calculation ke liye
- ✅ **ReservationService** - Inventory reservation ke liye (locking mechanism)

**Purpose:**
- Daily inventory allotment store karta hai
- Availability calculation ke liye base data
- Stop sell functionality provide karta hai
- Bulk operations support karta hai

---

#### **4. `inventory_reservations` Table**
**Entity:** `InventoryReservationEntity`  
**Table Name:** `inventory_reservations`

**Fields:**
- `id` (UUID, PK) - Auto-generated UUID
- `booking_id` (UUID) - Booking identifier (for future booking flow)
- `hotel_id` (String) - Hotel identifier
- `room_type_id` (String) - Room type identifier
- `check_in` (LocalDate) - Check-in date
- `check_out` (LocalDate) - Check-out date
- `rooms_count` (Integer) - Number of rooms reserved
- `status` (ReservationStatus enum) - RESERVED or RELEASED
- `created_at` (Instant) - Auto-set on creation
- `updated_at` (Instant) - Auto-updated on modification

**Indexes:**
- `idx_booking_id` - For quick lookup by booking ID
- `idx_hotel_roomtype` - For availability queries

**Where Used:**
- ✅ **ReservationService** - `reserve()` method - Inventory reserve karta hai
- ✅ **ReservationService** - `releaseByBookingId()` method - Inventory release karta hai
- ✅ **AvailabilityService** - Availability calculation mein reserved rooms count karta hai
- ✅ **InventoryReservationRepository** - Database operations

**Purpose:**
- Booking ke liye inventory reservations track karta hai
- Status: RESERVED (inventory hold) ya RELEASED (inventory free)
- Availability calculation mein use hota hai (allotment - reserved = available)
- **Note:** Abhi directly public API se use nahi ho raha, lekin future booking flow ke liye ready hai

---

### **Legacy/Unused Tables (Not Currently Used in APIs):**

#### **5. `pricing` Table**
**Entity:** `PricingEntity`  
**Table Name:** `pricing`

**Status:** ❌ **Not Used**  
**Reason:** Internal pricing intelligence service use hota hai (no database storage)

---

---

### **Removed Tables:**

#### **6. `room_inventory` Table**
**Entity:** `RoomInventoryEntity` (DELETED)  
**Table Name:** `room_inventory`

**Status:** ❌ **Removed**  
**Reason:** `inventory_allotments` table use ho raha hai instead, isliye table remove kar diya gaya

---

#### **7. `bookings` Table**
**Entity:** `BookingEntity` (DELETED)

**Status:** ❌ **Removed**  
**Reason:** Abhi booking flow implement nahi hai, isliye table remove kar diya gaya

---

## 📡 APIs Documentation (8 APIs)

### **Total APIs: 8**

---

### **1. Hotel Management APIs (2)**

#### **1.1 POST /v1/admin/hotels** - Create Hotel

**Purpose:** Naya hotel create karna

**Request:**
```json
{
  "id": "hotel-123",
  "name": "Grand Hotel"
}
```

**Response (201 Created):**
```json
{
  "id": "hotel-123",
  "name": "Grand Hotel",
  "active": true,
  "createdAt": "2024-01-07T10:00:00Z",
  "updatedAt": "2024-01-07T10:00:00Z"
}
```

**Table Used:** `hotels` (INSERT)

**Frontend Usage:**
- Admin panel mein "Add New Hotel" form
- User hotel ka name aur ID enter karega
- Success par hotel list mein naya hotel dikhega

**Backend Logic:**
- Duplicate ID check (HotelService)
- HotelEntity create with auto timestamps
- Save to `hotels` table

---

#### **1.2 PATCH /v1/admin/hotels/{hotelId}** - Update Hotel

**Purpose:** Hotel ki information update karna

**Request (All fields optional):**
```json
{
  "name": "Grand Hotel Updated",
  "active": true
}
```

**Response (200 OK):**
```json
{
  "id": "hotel-123",
  "name": "Grand Hotel Updated",
  "active": true,
  "createdAt": "2024-01-07T10:00:00Z",
  "updatedAt": "2024-01-07T11:00:00Z"
}
```

**Table Used:** `hotels` (UPDATE)

**Frontend Usage:**
- Edit hotel form mein user changes karega
- Partial update support - sirf changed fields bhej sakte hain

**Backend Logic:**
- Hotel find karna from `hotels` table
- Only provided fields update karna
- Auto-update `updated_at` timestamp

---

### **2. Room Type Management APIs (2)**

#### **2.1 POST /v1/admin/hotels/{hotelId}/room-types** - Create Room Type

**Purpose:** Hotel ke liye naya room type add karna

**Request:**
```json
{
  "id": "DELUXE",
  "name": "Deluxe Room",
  "maxGuests": 2
}
```

**Response (201 Created):**
```json
{
  "id": "DELUXE",
  "hotelId": "hotel-123",
  "name": "Deluxe Room",
  "maxGuests": 2,
  "active": true,
  "createdAt": "2024-01-07T10:30:00Z",
  "updatedAt": "2024-01-07T10:30:00Z"
}
```

**Tables Used:**
- `hotels` (SELECT - hotel existence check)
- `room_types` (INSERT)

**Frontend Usage:**
- Hotel details page par "Add Room Type" form
- Room type ID, name, max guests enter karega
- **Important:** Use DELUXE, SUITE, or STANDARD as roomTypeId for pricing

**Backend Logic:**
- Hotel existence check from `hotels` table
- Duplicate ID check (same hotel) from `room_types` table
- Duplicate name check (same hotel) from `room_types` table
- RoomTypeEntity create with auto timestamps
- Save to `room_types` table

---

#### **2.2 PATCH /v1/admin/room-types/{roomTypeId}** - Update Room Type

**Purpose:** Room type ki information update karna

**Request (All fields optional):**
```json
{
  "name": "Deluxe Suite",
  "maxGuests": 3,
  "active": true
}
```

**Response (200 OK):**
```json
{
  "id": "DELUXE",
  "hotelId": "hotel-123",
  "name": "Deluxe Suite",
  "maxGuests": 3,
  "active": true,
  "createdAt": "2024-01-07T10:30:00Z",
  "updatedAt": "2024-01-07T11:00:00Z"
}
```

**Table Used:** `room_types` (SELECT, UPDATE)

**Frontend Usage:**
- Edit room type form
- Partial update support

**Backend Logic:**
- Room type find karna from `room_types` table
- Name update par duplicate check
- Only provided fields update karna

---

### **3. Inventory Management APIs (3)**

#### **3.1 POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert** - Bulk Upsert Inventory

**Purpose:** Date range ke liye ek saath multiple inventory rows create/update karna

**Request:**
```json
{
  "roomTypeId": "DELUXE",
  "startDate": "2024-02-01",
  "endDate": "2024-02-05",
  "allotmentQty": 10,
  "stopSell": false
}
```

**Response (200 OK):**
```
Successfully upserted 4 inventory allotment(s)
```

**Tables Used:**
- `inventory_allotments` (SELECT, INSERT, UPDATE)
- `room_types` (implicit validation)

**Note:** Creates/updates one row per date from startDate (inclusive) to endDate (exclusive)

**Frontend Usage:**
- Inventory management page par bulk update form
- Date range select karke same values apply karna

**Backend Logic:**
- Date range validate (startDate < endDate)
- Loop har date ke liye
- Check if row exists in `inventory_allotments` table
- Create or update inventory rows
- Return success message with count

---

#### **3.2 GET /v1/admin/hotels/{hotelId}/inventory** - Get Inventory

**Purpose:** Hotel ki inventory allotments fetch karna (with optional filters)

**Query Parameters:**
- `roomTypeId` (optional) - Filter by room type
- `start` (optional) - Start date filter (YYYY-MM-DD)
- `end` (optional) - End date filter (YYYY-MM-DD)

**Example:**
```
GET /v1/admin/hotels/hotel-123/inventory?roomTypeId=DELUXE&start=2024-02-01&end=2024-02-05
```

**Response (200 OK):**
```json
[
  {
    "id": "770e8400-e29b-41d4-a716-446655440000",
    "hotelId": "hotel-123",
    "roomTypeId": "DELUXE",
    "date": "2024-02-01",
    "allotmentQty": 10,
    "stopSell": false,
    "currency": "INR",
    "totalPriceMinor": 12000,
    "createdAt": "2024-01-07T10:00:00Z",
    "updatedAt": "2024-01-07T10:00:00Z"
  }
]
```

**Tables Used:**
- `inventory_allotments` (SELECT with filters)
- `inventory_reservations` (indirect - availability calculation ke liye)

**Note:** Includes pricing information (internal pricing service se)

**Frontend Usage:**
- Inventory calendar/grid view
- Filters apply karke data fetch karna
- Pricing information display karna

**Backend Logic:**
- Query parameters check
- Database query from `inventory_allotments` table with filters
- Internal pricing service se pricing add karna (room type-based)
- List return karna

---

#### **3.3 PATCH /v1/admin/inventory/{inventoryRowId}** - Update Single Inventory Row

**Purpose:** Ek specific inventory row ko update karna

**Request (All fields optional):**
```json
{
  "allotmentQty": 15,
  "stopSell": false
}
```

**Response (200 OK):**
```json
{
  "id": "770e8400-e29b-41d4-a716-446655440000",
  "hotelId": "hotel-123",
  "roomTypeId": "DELUXE",
  "date": "2024-02-01",
  "allotmentQty": 15,
  "stopSell": false,
  "currency": "INR",
  "totalPriceMinor": 12000,
  "createdAt": "2024-01-07T10:00:00Z",
  "updatedAt": "2024-01-07T11:00:00Z"
}
```

**Table Used:** `inventory_allotments` (SELECT, UPDATE)

**Frontend Usage:**
- Inventory calendar mein specific date par click
- Edit modal/form se update karna

**Backend Logic:**
- Inventory row find karna from `inventory_allotments` table (UUID se)
- Partial update support
- Validation (allotmentQty >= 0)
- Auto-update timestamp

---

### **4. Internal Pricing Intelligence API (1)**

#### **4.1 POST /v1/internal/pricing/quote** - Get Pricing Quote

**Purpose:** Internal pricing quote calculation (NO external HTTP calls, NO database storage)

**Request:**
```json
{
  "hotelId": "hotel-123",
  "roomTypeId": "DELUXE",
  "checkIn": "2024-02-01",
  "checkOut": "2024-02-03",
  "guests": 2,
  "currency": "INR"
}
```

**Response (200 OK):**
```json
{
  "currency": "INR",
  "totalPriceMinor": 24000
}
```

**Tables Used:** ❌ **NO DATABASE TABLE** - Pure calculation based

**Room Type Based Pricing:**
- **DELUXE** → 12000 per night
- **SUITE** → 20000 per night
- **STANDARD** → 8000 per night
- **Default** → 10000 per night (for unknown room types)

**Calculation:**
- Nights = `ChronoUnit.DAYS.between(checkIn, checkOut)` (checkIn inclusive, checkOut exclusive)
- Total Price = Nights × Base Rate (room type based)
- Currency defaults to "INR" if not provided

**Example Calculations:**
- DELUXE, 2 nights: 2 × 12000 = 24000
- SUITE, 2 nights: 2 × 20000 = 40000
- STANDARD, 2 nights: 2 × 8000 = 16000

**Frontend Usage:**
- Internal service calls ke liye
- Booking flow mein pricing calculation
- Inventory responses mein pricing add karna

**Backend Logic:**
- Validation (required fields, date range)
- Room type-based rate determination (switch statement)
- Nights calculation
- Currency resolution (default to INR)
- Total price calculation
- INFO level logging with "PRICING_INTERNAL quote" prefix

---

## 📁 Project Structure

```
Hotelsystems.ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotelsystems/ai/
│   │   │       ├── Application.java                    # Main Spring Boot app
│   │   │       └── bookingmanagement/
│   │   │           ├── ownerinventory/
│   │   │           │   ├── controller/                 # REST Controllers
│   │   │           │   │   ├── HotelController.java
│   │   │           │   │   ├── RoomTypeController.java
│   │   │           │   │   ├── AdminInventoryController.java
│   │   │           │   │   ├── AdminInventoryUpdateController.java
│   │   │           │   │   └── PricingIntelligenceInternalController.java
│   │   │           │   ├── service/                    # Business Logic
│   │   │           │   │   ├── HotelService.java
│   │   │           │   │   └── RoomTypeService.java
│   │   │           │   ├── repository/                 # Data Access
│   │   │           │   │   ├── HotelRepository.java
│   │   │           │   │   ├── RoomTypeRepository.java
│   │   │           │   │   ├── InventoryAllotmentRepository.java
│   │   │           │   │   └── InventoryReservationRepository.java
│   │   │           │   ├── entity/                     # Database Entities
│   │   │           │   │   ├── HotelEntity.java        → hotels table
│   │   │           │   │   ├── RoomTypeEntity.java     → room_types table
│   │   │           │   │   ├── InventoryAllotmentEntity.java → inventory_allotments table
│   │   │           │   │   └── InventoryReservationEntity.java → inventory_reservations table
│   │   │           │   ├── dto/                        # DTOs
│   │   │           │   │   ├── HotelRequest.java
│   │   │           │   │   ├── HotelUpdateRequest.java
│   │   │           │   │   ├── HotelResponse.java
│   │   │           │   │   ├── RoomTypeRequest.java
│   │   │           │   │   ├── RoomTypeUpdateRequest.java
│   │   │           │   │   ├── RoomTypeResponse.java
│   │   │           │   │   ├── InventoryUpdateRequest.java
│   │   │           │   │   ├── PricingQuoteRequest.java
│   │   │           │   │   └── PricingQuote.java
│   │   │           │   ├── exception/                  # Exception Handling
│   │   │           │   │   ├── NotFoundException.java
│   │   │           │   │   ├── DuplicateException.java
│   │   │           │   │   ├── ConflictException.java
│   │   │           │   │   └── GlobalExceptionHandler.java
│   │   │           │   ├── pricing/                    # Pricing Logic
│   │   │           │   │   ├── PricingIntelligenceService.java
│   │   │           │   │   ├── PricingIntelligenceClient.java
│   │   │           │   │   └── PricingIntelligenceInternalController.java
│   │   │           │   ├── availability/               # Availability Service
│   │   │           │   │   └── AvailabilityService.java
│   │   │           │   └── reservation/                # Reservation Service
│   │   │           │       └── ReservationService.java
│   │   └── resources/
│   │       ├── application.yml                        # Main config (H2 default)
│   │       ├── application-h2.yml                     # H2 profile
│   │       └── application-postgres.yml              # PostgreSQL profile
│   └── test/
│       └── java/
│           └── com/hotelsystems/ai/bookingmanagement/
│               └── ownerinventory/
│                   └── pricing/
│                       └── PricingIntelligenceInternalControllerTest.java
├── pom.xml                                             # Maven dependencies
├── mvnw.cmd                                            # Maven wrapper
└── Documentation:
    ├── PROJECT_COMPLETE_OVERVIEW.md                   # This file
    └── Complete_APIs_Postman_Collection.json            # Postman collection
```

---

## ⚙️ Setup & Configuration

### Prerequisites:
- Java 17 or higher
- Maven 3.6+ (or use mvnw wrapper)
- H2 Database (included) or PostgreSQL (for production)

### Configuration Files:

#### application.yml (Default - H2)
```yaml
spring:
  application:
    name: Hotelsystems.ai
  profiles:
    active: h2
  datasource:
    url: jdbc:h2:mem:hotelsystems;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1
    driver-class-name: org.h2.Driver
    username: sa
    password: ""
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.H2Dialect
        format_sql: true
```

### Running the Application:

**Option 1: Maven Wrapper (Recommended)**
```bash
cd Hotelsystems.ai
.\mvnw.cmd spring-boot:run
```

**Option 2: With H2 Profile (Explicit)**
```bash
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=h2
```

**Option 3: Build JAR and Run**
```bash
.\mvnw.cmd clean package
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar
```

### Access Points:
- **API Base URL:** http://localhost:8080
- **H2 Console:** http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:hotelsystems`
  - Username: `sa`
  - Password: (empty)

---

## 📊 Summary

### **Total APIs: 8**
- Hotel Management: 2 APIs
- Room Type Management: 2 APIs
- Inventory Management: 3 APIs
- Internal Pricing Intelligence: 1 API

### **Active Database Tables: 4**
1. **`hotels`** - Hotel master data
2. **`room_types`** - Room type master data
3. **`inventory_allotments`** - Daily inventory allotments
4. **`inventory_reservations`** - Inventory reservations (for future booking flow)

### **Key Features:**
- ✅ Hotel aur room type management
- ✅ Inventory management with bulk operations
- ✅ Internal Pricing Intelligence with room type-based pricing
- ✅ Proper error handling aur validation
- ✅ Clean architecture with separation of concerns
- ✅ Ready for production deployment

**Status:** ✅ **Fully Functional and Ready for Testing**

---

**Last Updated:** January 2024  
**Version:** 1.0.0  
**Maintained By:** Development Team

