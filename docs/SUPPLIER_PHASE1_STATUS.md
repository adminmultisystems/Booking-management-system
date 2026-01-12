# Supplier Phase-1 Status

## ✅ Phase-1 Compliance

This document confirms that the repository matches mentors Phase-1 requirements strictly.

## Implemented Endpoints

### Supplier Mapping Admin APIs

- **GET** `/v1/admin/hotels/{hotelId}/supplier-mapping`
  - Returns all supplier mappings for a hotel
  - Returns 404 if no mappings exist

- **POST** `/v1/admin/hotels/{hotelId}/supplier-mapping`
  - Creates or updates a supplier mapping
  - Request body: `{ "supplierCode": "HOTELBEDS|TRAVELLANDA", "supplierHotelId": "...", "status": "ACTIVE|NOT_FOUND|DISABLED" }`
  - Returns 201 Created on success
  - Returns 409 Conflict on validation/conflict errors
  - Returns 400 Bad Request on validation errors

## Business Rules

### ACTIVE Status Requirement
- **Rule**: When `status` is `ACTIVE`, `supplierHotelId` must be non-blank
- **Enforcement**: Service validates and returns 409 Conflict if violated
- **Error Message**: "supplierHotelId is required when status is ACTIVE"

### Single ACTIVE Supplier Per Hotel
- **Rule**: Only one supplier can be ACTIVE per hotel at a time
- **Enforcement**: Service checks for existing ACTIVE mappings and returns 409 Conflict if trying to set a different supplier as ACTIVE
- **Error Message**: "Cannot set supplier {code} as ACTIVE: hotel already has ACTIVE supplier {existingCode}"

## Stub Lifecycle Behavior

### Stub Adapters
Phase-1 uses stub implementations for all supplier adapters:

- **HotelbedsStubAdapters**: Implements all three adapter interfaces
- **TravellandaStubAdapters**: Implements all three adapter interfaces

### Stub Behavior

#### `searchOffers()`
- Returns 1-3 offers deterministically based on `hotelId + checkIn` date
- Same inputs always produce same outputs (stable for testing)
- Includes per-night breakdown, cancellation summary, and taxes/fees placeholder

#### `recheck()`
- Returns `OK` by default
- Returns `SOLD_OUT` if `offerPayloadJson` contains `"forceSoldOut": true`
- Returns `PRICE_CHANGED` with `newTotalPrice = oldPrice + 10` if `offerPayloadJson` contains `"forcePriceChange": true`

#### `createBooking()`
- Returns supplier booking reference:
  - HOTELBEDS → `"HB-BOOK-" + shortRandom`
  - TRAVELLANDA → `"TL-BOOK-" + shortRandom`
- Status: `CONFIRMED`
- Includes raw payload JSON

#### `cancelBooking()`
- No-op success (always succeeds)

## How to Run Phase-1

### Default Profile (Recommended for Phase-1)
```bash
# No profile needed - Phase-1 is active by default
mvn spring-boot:run
```

### Dev Profile (Also Phase-1 Compliant)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### What's Active in Phase-1
✅ **Active Components:**
- `SupplierMappingAdminController` - Admin APIs for mapping management
- `SupplierMappingService` - Mapping business logic
- `SupplierHotelMappingRepository` - Data access
- `SupplierAdapterRegistry` - Registry for accessing adapters
- `HotelbedsStubAdapters` - Stub implementation for Hotelbeds
- `TravellandaStubAdapters` - Stub implementation for Travellanda
- All adapter interfaces (`SupplierOfferSearchAdapter`, `SupplierRecheckAdapter`, `SupplierBookingAdapter`)

❌ **Disabled Components (Behind `supplier-real` Profile):**
- `CompositeSupplierBookingAdapter` - Connects suppliers to BookingEntity/booking flow
- `RealSupplierBookingAdapter` - Real supplier booking adapter
- `HotelBedsBookingService` - Real Hotelbeds API integration
- `TravelLandaBookingService` - Real Travellanda API integration
- `HotelbedsClient` - Real Hotelbeds WebClient
- `HotelbedsWebClientConfig` - Real Hotelbeds WebClient configuration
- `HotelbedsDebugController` - Debug endpoints (also requires "dev" or "live-hotelbeds" profile)

## How to Enable Real Supplier Integration

### Enable Real Supplier Integration
```bash
# Enable real supplier integration
mvn spring-boot:run -Dspring-boot.run.profiles=supplier-real

# Or with JAR
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=supplier-real
```

### What Gets Enabled with `supplier-real` Profile
- `CompositeSupplierBookingAdapter` - Routes bookings to real supplier services
- `RealSupplierBookingAdapter` - Real supplier booking adapter
- `HotelBedsBookingService` - Real Hotelbeds API calls
- `TravelLandaBookingService` - Real Travellanda API calls
- `HotelbedsClient` - Real Hotelbeds WebClient
- `HotelbedsWebClientConfig` - Real Hotelbeds WebClient configuration

**Note**: `HotelbedsDebugController` requires both `supplier-real` AND (`dev` OR `live-hotelbeds`) profiles.

## Testing

### Run Phase-1 Tests
```bash
mvn test
```

### Test Coverage
- ✅ `SupplierMappingServiceTest` - Validates business rules (ACTIVE requires supplierHotelId, single ACTIVE enforcement)
- ✅ `SupplierMappingAdminControllerTest` - Validates API endpoints (GET/POST mapping, 409 conflict)

### Test Configuration
- Uses H2 in-memory database (configured in `src/test/resources/application.yml`)
- Flyway migrations run automatically
- No real supplier API calls are made

## Phase-1 Compliance Checklist

- ✅ Supplier mapping persistence (entity, repository, Flyway migration)
- ✅ Admin APIs for mapping management (GET/POST endpoints)
- ✅ Business rules enforced (ACTIVE requires supplierHotelId, single ACTIVE per hotel)
- ✅ Stub adapter implementations (deterministic, testable)
- ✅ Adapter registry for accessing stubs by SupplierCode
- ✅ Real supplier integration isolated behind `supplier-real` profile
- ✅ Booking flow integration isolated behind `supplier-real` profile
- ✅ No real supplier API calls in default profile
- ✅ Tests pass without real supplier dependencies
- ✅ Documentation complete

## Next Steps (Future Phases)

- **Phase-2**: Wire real supplier integration (enable `supplier-real` profile)
- **Phase-3**: Integrate supplier mapping with booking orchestration
- **Phase-4**: Add inventory and pricing integration

