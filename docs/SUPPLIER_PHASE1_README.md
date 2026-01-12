# Supplier Phase-1 Implementation

## Overview

This document describes the Phase-1 implementation of supplier mapping and adapter plumbing for the booking management service. This phase implements ONLY Engineer-1 scope: Supplier mapping + adapter plumbing + STUB lifecycle.

## What Was Implemented

### 1. Supplier Mapping Persistence

- **Enums:**
  - `SupplierCode`: HOTELBEDS, TRAVELLANDA
  - `SupplierMappingStatus`: ACTIVE, NOT_FOUND, DISABLED

- **Entity:** `SupplierHotelMappingEntity`
  - Composite primary key (hotelId + supplierCode)
  - Fields: hotelId, supplierCode, supplierHotelId, status, createdAt, updatedAt

- **Repository:** `SupplierHotelMappingRepository`
  - Methods for finding mappings by hotelId, supplierCode, and status

- **Flyway Migration:** `V2__create_supplier_hotel_mapping.sql`
  - Creates `supplier_hotel_mapping` table with composite primary key

### 2. Admin APIs

**Endpoints:**
- `GET /v1/admin/hotels/{hotelId}/supplier-mapping` - Get all mappings for a hotel
- `POST /v1/admin/hotels/{hotelId}/supplier-mapping` - Create or update a mapping

**Service:** `SupplierMappingService`
- Validates that ACTIVE status requires supplierHotelId
- Enforces single ACTIVE supplier per hotel (returns 409 Conflict if violated)
- Returns 404 if no mappings exist

**Exception Handler:** `SupplierExceptionHandler`
- Maps NotFoundException → 404
- Maps BadRequestException → 400
- Maps ConflictException → 409
- Maps Validation errors → 400

### 3. Supplier Adapter Contracts

**Interfaces:**
- `SupplierOfferSearchAdapter`: Search for offers
- `SupplierRecheckAdapter`: Recheck offer availability/price
- `SupplierBookingAdapter`: Create and cancel bookings

**DTOs:**
- `SupplierOfferDto`: Normalized offer with per-night breakdown, taxes/fees placeholder
- `SupplierRecheckResultDto`: Recheck result (OK/PRICE_CHANGED/SOLD_OUT)
- `SupplierBookResponse`: Booking response with supplier booking reference
- `PerNightRateDto`: Per-night rate breakdown
- `TaxesAndFeesPlaceholderDto`: Taxes and fees placeholder

### 4. Stub Implementations

**Stub Adapters:**
- `HotelbedsStubAdapters`: Implements all three adapter interfaces
- `TravellandaStubAdapters`: Implements all three adapter interfaces

**Stub Behavior:**
- `searchOffers()`: Returns 1-3 offers with deterministic values derived from hotelId + checkIn
- `recheck()`: Returns OK unless offerPayloadJson contains:
  - `"forceSoldOut": true` → SOLD_OUT
  - `"forcePriceChange": true` → PRICE_CHANGED with newTotalPrice = old + 10
- `createBooking()`: Returns supplier booking reference:
  - HOTELBEDS → "HB-BOOK-" + shortRandom
  - TRAVELLANDA → "TL-BOOK-" + shortRandom
- `cancelBooking()`: No-op success

**Registry:** `SupplierAdapterRegistry`
- Provides access to stub implementations by SupplierCode
- Uses Spring injection of lists/maps

### 5. Configuration Properties

**SupplierProperties:**
- Nested `HotelbedsConfig` and `TravellandaConfig`
- Binds from `application.yml` under `supplier.hotelbeds` and `supplier.travellanda`
- Does NOT hardcode secrets

### 6. Real Hotelbeds Integration Isolation

All real Hotelbeds integration code is now behind `@Profile("supplier-real")`:
- `HotelbedsWebClientConfig`
- `HotelbedsClient`
- `HotelBedsBookingService`
- `HotelbedsDebugController` (also includes "dev" and "live-hotelbeds" profiles)

**Note:** The existing `suppliers.hotelbeds` properties used by real integration remain as-is but are only active when `supplier-real` profile is enabled.

## Testing

### Running Tests

```bash
mvn test
```

### Test Coverage

- **SupplierMappingServiceTest:**
  - ACTIVE requires supplierHotelId
  - Conflict 409 when setting second ACTIVE supplier for same hotel

- **SupplierMappingAdminControllerTest:**
  - POST mapping then GET mappings returns it
  - POST second ACTIVE returns 409

## Postman Testing

### GET Mappings

**Request:**
```
GET http://localhost:8080/v1/admin/hotels/hotel-123/supplier-mapping
```

**Response (200 OK):**
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

**Response (404 Not Found):**
```json
{
  "error": "Not Found",
  "message": "No supplier mappings found for hotel: hotel-123"
}
```

### POST Mapping

**Request:**
```
POST http://localhost:8080/v1/admin/hotels/hotel-123/supplier-mapping
Content-Type: application/json

{
  "supplierCode": "HOTELBEDS",
  "supplierHotelId": "HB-123",
  "status": "ACTIVE"
}
```

**Response (201 Created):**
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

**Response (409 Conflict) - When trying to set second ACTIVE supplier:**
```json
{
  "error": "Conflict",
  "message": "Cannot set supplier TRAVELLANDA as ACTIVE: hotel already has ACTIVE supplier HOTELBEDS"
}
```

**Response (400 Bad Request) - When ACTIVE without supplierHotelId:**
```json
{
  "error": "Conflict",
  "message": "supplierHotelId is required when status is ACTIVE"
}
```

## Important Notes

1. **Stub Adapters are Deterministic:**
   - Offers are generated deterministically based on hotelId + checkIn date
   - Same inputs will always produce same outputs (useful for testing)

2. **Real Hotelbeds Integration is Behind Profile:**
   - Real integration code is disabled by default
   - Enable with `--spring.profiles.active=supplier-real` to use real Hotelbeds API
   - Phase-1 works without enabling this profile

3. **Database:**
   - Uses H2 for development/testing
   - Uses PostgreSQL for production
   - Flyway migrations run automatically on startup

4. **What Was NOT Touched:**
   - BookingController
   - BookingOrchestrationService
   - Owner inventory, pricing, payments
   - TL orchestration logic

## Next Steps (Future Phases)

- Phase-2: Real supplier integration wiring
- Phase-3: Booking orchestration with supplier mapping
- Phase-4: Inventory and pricing integration

