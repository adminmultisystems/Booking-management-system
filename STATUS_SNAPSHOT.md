# Booking Management Service - Status Snapshot

## 1) What's Working Now

- **App starts successfully** - Spring Boot application runs on port 8080 (configurable via `SERVER_PORT`)
- **Active profile behavior**:
  - `dev` profile: Uses H2 in-memory database with Hibernate `ddl-auto=update` (schema auto-created/evolved)
  - `prod` profile: Uses PostgreSQL with Flyway migrations (Hibernate DDL disabled)
- **Security/auth behavior**:
  - JWT authentication filter (`JwtAuthenticationFilter`) extracts `userId` from `Authorization: Bearer <token>` header
  - Stub implementation: if token starts with "user-", extracts userId; otherwise generates userId from token hash
  - All `/v1/**` endpoints require authentication; `/actuator/health` is public
  - `userId` stored in `SecurityContext` and accessible via `SecurityUtil.getCurrentUserId()`
- **Actuator health status**: `/actuator/health` endpoint exposed and accessible (public, no auth required)

## 2) Implemented APIs

**BookingController** (`/v1/bookings`):
- `POST /v1/bookings` - Create new booking (returns bookingId and status)
- `GET /v1/bookings/{id}` - Get booking by ID (requires ownership)
- `POST /v1/bookings/{id}/confirm` - Confirm booking (idempotent)
- `POST /v1/bookings/{id}/cancel` - Cancel booking (only CONFIRMED bookings)

**Actuator**:
- `GET /actuator/health` - Health check endpoint (public)

## 3) Core Domain Implemented

**Entities**:
- `BookingEntity` - Core booking entity mapped to `bookings_core` table
  - Fields: id (UUID), userId, hotelId, roomTypeId, checkIn/checkOut, status, source, guest info, supplier/owner fields, offer payload JSON, audit timestamps
  - Indexes: hotel_id, status, created_at, user_id

**Repositories**:
- `BookingRepository` - JPA repository with methods:
  - `findByUserId`, `findByHotelId`, `findByStatus`, `findBySource`
  - `findByUserIdAndStatus`
  - `findOverlappingBookings` (custom query for date range conflicts)

**Enums**:
- `BookingStatus`: `DRAFT`, `RECHECKING`, `PENDING_CONFIRMATION`, `CONFIRMED`, `FAILED`, `CANCELLED`
- `BookingSource`: `SUPPLIER`, `OWNER`
- `SupplierCode`: `HOTELBEDS`, `TRAVELLANDA`

## 4) Implemented Flows

**Booking create flow**:
- Validates check-in/check-out dates (must be future, check-out after check-in)
- Extracts `userId` from `SecurityContext` (set by JWT filter)
- Resolves `supplierCode` from request field or `offerPayloadJson` (priority: direct field → JSON parse → null)
- Creates booking entity with `DRAFT` status, saves to database

**Confirm flow**:
- Idempotent: if already `CONFIRMED`, returns existing booking
- Verifies `userId` ownership
- State transitions: `DRAFT` → `RECHECKING` → `PENDING_CONFIRMATION` → `CONFIRMED`
- Routing decision: if `supplierCode != null` → `SUPPLIER` path, else → `OWNER` path
- Calls `recheck()` on appropriate adapter (supplier or owner)
- If recheck returns `SOLD_OUT` or `PRICE_CHANGED` → transitions to `FAILED`
- If recheck `OK` → calls `createBooking()` (supplier) or `reserveAndConfirm()` (owner)
- Saves confirmation reference (`supplierBookingRef` or `internalConfirmationRef`)
- Final status: `CONFIRMED`

**Cancel flow**:
- Only `CONFIRMED` bookings can be cancelled
- Verifies `userId` ownership
- Calls `cancelBooking()` (supplier) or `release()` (owner) adapter
- Transitions to `CANCELLED` status

**Supplier routing logic**:
- Decision made during confirmation: `if (booking.getSupplierCode() != null) → SUPPLIER else → OWNER`
- `supplierCode` resolved during booking creation from:
  1. Direct `supplierCode` field in request
  2. Parsed from `offerPayloadJson.supplierCode`
  3. `null` (defaults to `OWNER` path)

## 5) Configuration Summary

**application.yml key points**:
- **Profiles**: `dev` (default) and `prod`
- **Database**:
  - Dev: H2 in-memory (`jdbc:h2:mem:booking_management;MODE=PostgreSQL`)
  - Prod: PostgreSQL (via `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`)
- **Schema management**:
  - Dev: Hibernate `ddl-auto=update`, Flyway disabled
  - Prod: Hibernate `ddl-auto=none`, Flyway enabled (`classpath:db/migration`)
- **CORS**: Configured via `CorsConfig` (allowed origins from `CORS_ALLOWED_ORIGINS`, defaults to localhost:3000,8080)
- **JWT placeholders**: `jwt.secret` and `jwt.expiration` (86400000ms = 24h) - currently using stub implementation
- **Supplier config**: Placeholder URLs and API keys for supplier booking/inventory and pricing intelligence services

**Environment variables expected**:
- `SERVER_PORT` (optional, default: 8080)
- `JWT_SECRET` (optional, placeholder default)
- `JWT_EXPIRATION` (optional, default: 86400000)
- `SUPPLIER_BOOKING_BASE_URL`, `SUPPLIER_BOOKING_API_KEY`
- `SUPPLIER_INVENTORY_BASE_URL`, `SUPPLIER_INVENTORY_API_KEY`
- `PRICING_INTELLIGENCE_BASE_URL`, `PRICING_INTELLIGENCE_API_KEY`
- `CORS_ALLOWED_ORIGINS`, `CORS_ALLOWED_CREDENTIALS`
- `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (prod profile)

## 6) What Is Still Stubbed / Not Implemented

- **Supplier real API integration**: `StubSupplierBookingAdapter` and `StubSupplierInventoryAdapter` are stubs
  - Always return success responses
  - No actual calls to Hotelbeds/Travellanda APIs
  - Mock confirmation references generated
- **Owner inventory real integration**: `StubOwnerInventoryAdapter` is a stub
  - Always returns success on recheck/reserve/release
  - Mock confirmation references generated
- **Payments**: Not started - no payment processing, payment gateway integration, or payment status tracking
- **Admin/owner inventory endpoints**: Not built - no REST endpoints for inventory management, room availability updates, or admin operations
- **JWT token validation**: Current implementation is a stub that extracts userId from token format; proper JWT parsing/validation not implemented
- **Pricing intelligence integration**: Configuration exists but no adapter/service implementation

## 7) Next Steps

1. Implement real JWT token validation in `AuthenticationService` (parse JWT, validate signature, extract userId claim)
2. Replace `StubSupplierBookingAdapter` with real Hotelbeds API integration (recheck, createBooking, cancelBooking)
3. Replace `StubSupplierBookingAdapter` with real Travellanda API integration (or create separate adapter per supplier)
4. Replace `StubOwnerInventoryAdapter` with real owner inventory service integration
5. Implement payment processing flow (payment gateway integration, payment status tracking, payment failure handling)
6. Create admin/owner inventory management endpoints (CRUD for inventory, availability updates, room type management)
7. Implement pricing intelligence service integration (if needed for dynamic pricing)
8. Add comprehensive error handling and retry logic for external API calls (supplier/owner adapters)
9. Create Flyway baseline migration (`V1__baseline.sql`) from dev database schema before production deployment
10. Add integration tests for booking lifecycle flows (create → confirm → cancel) with real adapter mocks

