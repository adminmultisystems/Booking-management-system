# Booking Management Service - Phase 1 (Engineer 1)

## 📋 Overview

This is a Spring Boot microservice for booking management with supplier integration. **Phase 1** implements supplier mapping APIs and stub supplier adapters with correct shapes for normalized offers.

**Technology Stack:**
- Java 21+
- Spring Boot 3.x
- PostgreSQL (production) / H2 (development)
- Flyway (database migrations)
- JPA/Hibernate






## 🎯 Phase 1 Scope

Phase 1 implements **ONLY** the following:
- ✅ Supplier mapping APIs (Admin endpoints)
- ✅ Supplier adapter interfaces (clean contracts)
- ✅ Stub supplier implementations
- ✅ Normalized offer DTOs with realistic shapes
- ✅ Configuration properties for suppliers



**DO NOT** touch:
- ❌ BookingController
- ❌ BookingOrchestrationService
- ❌ Owner inventory, pricing, payments
- ❌ Any TL-owned orchestration logic

## 📁 Project Structure

```
src/main/java/com/hotelsystems/ai/
├── Application.java                          # Main Spring Boot application
└── bookingmanagement/
    └── supplier/                             # Phase 1: Supplier package
        ├── adapter/
        │   ├── stub/
        │   │   ├── HotelbedsStubAdapters.java
        │   │   └── TravellandaStubAdapters.java
        │   ├── SupplierAdapterRegistry.java
        │   ├── SupplierBookingAdapter.java
        │   ├── SupplierOfferSearchAdapter.java
        │   └── SupplierRecheckAdapter.java
        ├── config/
        │   └── SupplierProperties.java       # @ConfigurationProperties(prefix="supplier")
        ├── controller/
        │   ├── admin/
        │   │   └── SupplierMappingAdminController.java
        │   ├── debug/
        │   │   └── SupplierStubDebugController.java  # Dev profile only
        │   └── SupplierExceptionHandler.java
        ├── dto/
        │   ├── SupplierCode.java             # Enum: HOTELBEDS, TRAVELLANDA
        │   ├── SupplierMappingResponse.java
        │   ├── SupplierOfferDto.java          # Normalized offer
        │   ├── SupplierRecheckResultDto.java
        │   ├── SupplierBookRequest.java
        │   ├── SupplierBookResponse.java
        │   ├── UpsertSupplierMappingRequest.java
        │   ├── PerNightRateDto.java
        │   └── TaxesAndFeesPlaceholderDto.java
        ├── entity/
        │   ├── SupplierHotelMappingEntity.java
        │   ├── SupplierHotelMappingId.java   # Composite key
        │   └── SupplierMappingStatus.java    # Enum: ACTIVE, NOT_FOUND, DISABLED
        ├── repo/
        │   └── SupplierHotelMappingRepository.java
        ├── service/
        │   └── SupplierMappingService.java
        └── error/
            ├── BadRequestException.java
            ├── ConflictException.java
            └── NotFoundException.java

src/main/resources/
├── application.yml                           # Main configuration (includes dev profile)
└── db/migration/
    └── V2__create_supplier_hotel_mapping.sql # Flyway migration

src/test/java/com/hotelsystems/ai/bookingmanagement/supplier/
├── controller/admin/
│   └── SupplierMappingAdminControllerTest.java
└── service/
    └── SupplierMappingServiceTest.java
```

## 🚀 Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6+
- PostgreSQL (for production) or H2 (for development - default)

### Running the Application

**Default (dev profile):**
```bash
mvn spring-boot:run
```

**With specific profile:**
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Using JAR:**
```bash
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Configuration

The application uses `application.yml` with environment variable support:

```yaml
supplier:
  hotelbeds:
    base-url: ${HOTELBEDS_BASE_URL:https://api.hotelbeds.com}
    api-key: ${HOTELBEDS_API_KEY:}
  travellanda:
    base-url: ${TRAVELLANDA_BASE_URL:https://api.travellanda.com}
    api-key: ${TRAVELLANDA_API_KEY:}
```

**Environment Variables:**
- `HOTELBEDS_BASE_URL` - Hotelbeds API base URL
- `HOTELBEDS_API_KEY` - Hotelbeds API key
- `TRAVELLANDA_BASE_URL` - Travellanda API base URL
- `TRAVELLANDA_API_KEY` - Travellanda API key
- `SPRING_PROFILES_ACTIVE` - Active Spring profile (default: dev)

## 📡 API Endpoints

### Admin APIs (Phase 1)

#### 1. Get Supplier Mappings
```http
GET /v1/admin/hotels/{hotelId}/supplier-mapping
```

**Response:**
```json
[
  {
    "hotelId": "hotel-123",
    "supplierCode": "HOTELBEDS",
    "supplierHotelId": "HB-123",
    "status": "ACTIVE",
    "createdAt": "2024-01-01T00:00:00Z",
    "updatedAt": "2024-01-01T00:00:00Z"
  }
]
```

**Status Codes:**
- `200 OK` - Mappings found
- `404 Not Found` - No mappings exist for hotel

#### 2. Create/Update Supplier Mapping
```http
POST /v1/admin/hotels/{hotelId}/supplier-mapping
Content-Type: application/json
```

**Request Body:**
```json
{
  "supplierCode": "HOTELBEDS",
  "supplierHotelId": "HB-123",
  "status": "ACTIVE"
}
```

**Response:**
```json
{
  "hotelId": "hotel-123",
  "supplierCode": "HOTELBEDS",
  "supplierHotelId": "HB-123",
  "status": "ACTIVE",
  "createdAt": "2024-01-01T00:00:00Z",
  "updatedAt": "2024-01-01T00:00:00Z"
}
```

**Status Codes:**
- `201 Created` - Mapping created/updated
- `400 Bad Request` - Validation failed
- `409 Conflict` - Business rule violation (e.g., second ACTIVE supplier)

**Business Rules:**
- `supplierHotelId` is **required** when `status=ACTIVE`
- Only **one ACTIVE** supplier per hotel (rejects with 409 if trying to set second ACTIVE)
- `supplierHotelId` is optional when `status` is NOT_FOUND or DISABLED

### Debug APIs (Dev Profile Only)

Available only when `dev` profile is active. Requires `X-Debug-Key: local-debug` header.

#### 1. Search Offers
```http
POST /internal/suppliers/offers/search
X-Debug-Key: local-debug
Content-Type: application/json
```

**Request:**
```json
{
  "supplierCode": "HOTELBEDS",
  "hotelId": "hotel-123",
  "supplierHotelId": "HB-123",
  "checkIn": "2024-06-01",
  "checkOut": "2024-06-03",
  "adults": 2,
  "children": 0,
  "rooms": 1
}
```

#### 2. Recheck Offer
```http
POST /internal/suppliers/offers/recheck
X-Debug-Key: local-debug
Content-Type: application/json
```

**Request:**
```json
{
  "supplierCode": "HOTELBEDS",
  "offerPayload": {
    "supplierCode": "HOTELBEDS",
    "supplierHotelId": "HB-123",
    "rateKey": "rate-123",
    "roomCode": "room-456"
  }
}
```

#### 3. Create Booking
```http
POST /internal/suppliers/bookings/create
X-Debug-Key: local-debug
Content-Type: application/json
```

#### 4. Cancel Booking
```http
POST /internal/suppliers/bookings/cancel
X-Debug-Key: local-debug
Content-Type: application/json
```

## 🔧 Supplier Adapter Interfaces

### SupplierOfferSearchAdapter
```java
List<SupplierOfferDto> searchOffers(
    String hotelId, 
    String supplierHotelId, 
    LocalDate checkIn, 
    LocalDate checkOut, 
    int adults, 
    int children, 
    int rooms
);
```

### SupplierRecheckAdapter
```java
SupplierRecheckResultDto recheck(String offerPayloadJson);
```

### SupplierBookingAdapter
```java
SupplierBookResponse createBooking(String offerPayloadJson, String guestPayloadJson);
void cancelBooking(String supplierBookingRef);
```

## 📦 Stub Implementations

### HotelbedsStubAdapters & TravellandaStubAdapters

**Features:**
- ✅ Deterministic offers (1-3 offers based on hotelId hash)
- ✅ Per-night rate breakdown
- ✅ Cancellation policy summary
- ✅ Taxes/fees placeholder
- ✅ Booking ref format: `HB-BOOK-{randomShort}` or `TL-BOOK-{randomShort}`
- ✅ Recheck supports `forceSoldOut` and `forcePriceChange` flags

**Example Offer Response:**
```json
{
  "offerId": "HB-OFFER-hotel-123-2024-06-01-0",
  "supplierCode": "HOTELBEDS",
  "supplierHotelId": "HB-123",
  "roomName": "Standard Room 1",
  "board": "Breakfast Included",
  "totalPrice": 250.00,
  "currency": "USD",
  "perNightBreakdown": [
    {"date": "2024-06-01", "amount": 125.00},
    {"date": "2024-06-02", "amount": 125.00}
  ],
  "cancellationSummary": "Free cancellation until 24 hours before check-in",
  "taxesAndFees": {
    "included": true,
    "amountNullable": 25.00,
    "note": "Taxes and fees included"
  },
  "rawPayloadJson": "{...}"
}
```

## 🗄️ Database Schema

### supplier_hotel_mapping

```sql
CREATE TABLE supplier_hotel_mapping (
    hotel_id VARCHAR(255) NOT NULL,
    supplier_code VARCHAR(255) NOT NULL,
    supplier_hotel_id VARCHAR(255),
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    PRIMARY KEY (hotel_id, supplier_code)
);
```

**Composite Primary Key:** `(hotel_id, supplier_code)` - Prevents duplicate mappings

**Status Values:**
- `ACTIVE` - Supplier is active for this hotel
- `NOT_FOUND` - Supplier hotel not found
- `DISABLED` - Supplier mapping disabled

## 🧪 Testing

### Run All Tests
```bash
mvn test
```

### Phase 1 Test Coverage

1. **SupplierMappingServiceTest**
   - ✅ ACTIVE requires supplierHotelId validation
   - ✅ Single ACTIVE supplier per hotel enforcement
   - ✅ 404 when no mappings exist

2. **SupplierMappingAdminControllerTest**
   - ✅ GET mapping endpoint
   - ✅ POST mapping endpoint
   - ✅ 409 conflict on second ACTIVE supplier
   - ✅ 404 when no mappings exist

### Test Configuration

- Uses H2 in-memory database
- Flyway migrations run automatically
- No real supplier API calls

## 📝 Postman Testing

### Setup

1. Import Postman collection: `docs/postman/Supplier_Phase1_Collection.json`
2. Import environment: `docs/postman/Supplier_Phase1_Environment.json`
3. Set `baseUrl` variable to `http://localhost:8080`

### Test Scenarios

1. **Create ACTIVE Mapping**
   ```http
   POST /v1/admin/hotels/123/supplier-mapping
   {
     "supplierCode": "HOTELBEDS",
     "supplierHotelId": "HB-123",
     "status": "ACTIVE"
   }
   ```

2. **Get Mappings**
   ```http
   GET /v1/admin/hotels/123/supplier-mapping
   ```

3. **Try Second ACTIVE (Should Fail with 409)**
   ```http
   POST /v1/admin/hotels/123/supplier-mapping
   {
     "supplierCode": "TRAVELLANDA",
     "supplierHotelId": "TL-456",
     "status": "ACTIVE"
   }
   ```

4. **Validation Test (ACTIVE without supplierHotelId)**
   ```http
   POST /v1/admin/hotels/123/supplier-mapping
   {
     "supplierCode": "HOTELBEDS",
     "status": "ACTIVE"
   }
   ```
   Should return `400 Bad Request`

## 🔍 Error Handling

All errors follow standard HTTP status codes:

- `400 Bad Request` - Validation errors
- `404 Not Found` - Resource not found
- `409 Conflict` - Business rule violations

**Error Response Format:**
```json
{
  "error": "Conflict",
  "message": "Cannot set supplier TRAVELLANDA as ACTIVE: hotel already has ACTIVE supplier HOTELBEDS"
}
```

## 📋 Phase 1 Checklist

- ✅ Supplier mapping persistence (entity, repository, Flyway migration)
- ✅ Admin APIs for mapping management (GET/POST endpoints)
- ✅ Business rules enforced (ACTIVE requires supplierHotelId, single ACTIVE per hotel)
- ✅ Stub adapter implementations (deterministic, testable)
- ✅ Adapter registry for accessing stubs by SupplierCode
- ✅ Normalized offer DTOs with all required fields
- ✅ Configuration properties with environment variable support
- ✅ Exception handling with proper HTTP status codes
- ✅ Unit and integration tests
- ✅ Documentation complete

## 🚫 What's NOT in Phase 1

- ❌ Real supplier integration (behind `supplier-real` profile)
- ❌ BookingController integration
- ❌ BookingOrchestrationService
- ❌ Payment processing
- ❌ Owner inventory management

## 📚 Additional Documentation

- `docs/SUPPLIER_PHASE1_README.md` - Detailed Phase 1 documentation
- `docs/SUPPLIER_PHASE1_STATUS.md` - Implementation status
- `docs/postman/SUPPLIER_PHASE1_POSTMAN_GUIDE.md` - Postman testing guide
- `RUN_WITH_DEV_PROFILE.md` - How to run with dev profile

## 🛠️ Development

### H2 Console (Dev Profile)

Access at: `http://localhost:8080/h2-console`

- JDBC URL: `jdbc:h2:mem:hotelsystems`
- Username: `sa`
- Password: (empty)

### Kill Port 8080 (Windows)

```bash
netstat -ano | findstr :8080
taskkill /PID <PID> /F
```

## 📞 Support

For Phase 1 implementation questions, refer to:
- Task requirements document
- Code comments in supplier package
- Test files for usage examples

---

**Phase 1 Implementation Complete** ✅

All components are isolated in `com.hotelsystems.ai.bookingmanagement.supplier` package and ready for TL integration.

