# Supplier Phase-1 Postman Testing Guide

## Setup Instructions

### 1. Import Collection and Environment

1. Open Postman
2. Click **Import** button
3. Import these files:
   - `Supplier_Phase1_Collection.json` - Main collection with all requests
   - `Supplier_Phase1_Environment.json` - Environment variables

### 2. Select Environment

1. Click on **Environments** in the left sidebar
2. Select **Supplier Phase-1 Environment** from the dropdown (top right)

### 3. Start Application

Make sure your Spring Boot application is running:

```bash
# For Phase-1 (default profile)
mvn spring-boot:run

# OR for dev profile (to test debug endpoints)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**Note**: Debug endpoints (Section 2) require `dev` profile to be active.

## Test Scenarios

### Section 1: Mapping APIs (Always Available)

These endpoints work in any profile (default, dev, etc.).

#### 1.1 POST mapping (ACTIVE)
- **Endpoint**: `POST /v1/admin/hotels/123/supplier-mapping`
- **Expected**: 201 Created
- **Response**: Mapping details with hotelId, supplierCode, status

#### 1.2 GET mapping
- **Endpoint**: `GET /v1/admin/hotels/123/supplier-mapping`
- **Expected**: 200 OK
- **Response**: Array of mappings (should contain HOTELBEDS ACTIVE)

#### 1.3 Conflict test (2nd ACTIVE not allowed)
- **Endpoint**: `POST /v1/admin/hotels/123/supplier-mapping`
- **Body**: Try to set TRAVELLANDA as ACTIVE
- **Expected**: 409 Conflict
- **Response**: Error message about existing ACTIVE supplier

#### 1.4 Validation test (ACTIVE without supplierHotelId)
- **Endpoint**: `POST /v1/admin/hotels/124/supplier-mapping`
- **Body**: ACTIVE status without supplierHotelId
- **Expected**: 400/409 Bad Request
- **Response**: Error message about supplierHotelId required

#### 1.5 GET non-existing mapping
- **Endpoint**: `GET /v1/admin/hotels/99999/supplier-mapping`
- **Expected**: 404 Not Found
- **Response**: Error message about no mappings found

### Section 2: Stub Lifecycle (Debug Endpoints - Dev Profile Only)

**Important**: These endpoints require:
- `dev` profile active
- `X-Debug-Key: local-debug` header (already set in collection)

#### 2.A Search offers
- **Endpoint**: `POST /internal/suppliers/offers/search`
- **Expected**: 200 OK
- **Response**: Array of 1-3 offers
- **Verify**: Each offer contains:
  - `cancellationSummary`
  - `perNightBreakdown`
  - `taxesAndFees` (placeholder)

#### 2.B Recheck SOLD OUT
- **Endpoint**: `POST /internal/suppliers/offers/recheck`
- **Body**: `{ "supplierCode": "HOTELBEDS", "offerPayload": { "forceSoldOut": true } }`
- **Expected**: 200 OK
- **Response**: `{ "status": "SOLD_OUT" }`

#### 2.C Recheck PRICE CHANGE
- **Endpoint**: `POST /internal/suppliers/offers/recheck`
- **Body**: `{ "supplierCode": "HOTELBEDS", "offerPayload": { "forcePriceChange": true } }`
- **Expected**: 200 OK
- **Response**: `{ "status": "PRICE_CHANGED", "newTotalPriceNullable": <new_price> }`

#### 2.D Create booking
- **Endpoint**: `POST /internal/suppliers/bookings/create`
- **Expected**: 200 OK
- **Response**: `{ "supplierBookingRef": "HB-BOOK-xxxx", "status": "CONFIRMED" }`
- **Note**: Booking ref is saved to environment variable for cancel test

#### 2.E Cancel booking
- **Endpoint**: `POST /internal/suppliers/bookings/cancel`
- **Expected**: 200 OK
- **Response**: `{ "status": "CANCELLED", "supplierBookingRef": "..." }`

## Environment Variables

The collection uses these variables (set in environment):

- `baseUrl`: `http://localhost:8080` (default)
- `debugKey`: `local-debug` (default)

You can override these in the environment or collection variables.

## Troubleshooting

### 403 Forbidden on Debug Endpoints
- **Cause**: Missing or invalid `X-Debug-Key` header
- **Solution**: Ensure header is set to `local-debug` (or match `debug.key` in `application-dev.yml`)

### 404 Not Found on Debug Endpoints
- **Cause**: Application not running with `dev` profile
- **Solution**: Start with `mvn spring-boot:run -Dspring-boot.run.profiles=dev`

### 500 Internal Server Error
- **Cause**: Application not running or database not initialized
- **Solution**: 
  - Ensure application is running
  - Check Flyway migrations ran successfully
  - Check application logs for errors

## Running All Tests

### Option 1: Run Collection
1. Select **Supplier Phase-1 APIs** collection
2. Click **Run** button (top right)
3. Click **Run Supplier Phase-1 APIs**
4. All tests will execute in sequence

### Option 2: Run Individual Requests
1. Expand collection folders
2. Click on individual request
3. Click **Send** button

## Expected Results Summary

| Test | Endpoint | Expected Status | Key Validation |
|------|----------|----------------|----------------|
| 1.1 | POST mapping | 201 | Mapping created |
| 1.2 | GET mapping | 200 | Array with HOTELBEDS ACTIVE |
| 1.3 | POST 2nd ACTIVE | 409 | Conflict error |
| 1.4 | POST ACTIVE no ID | 400/409 | Validation error |
| 1.5 | GET non-existing | 404 | Not found error |
| 2.A | Search offers | 200 | 1-3 offers with all fields |
| 2.B | Recheck SOLD_OUT | 200 | status = SOLD_OUT |
| 2.C | Recheck PRICE_CHANGED | 200 | status = PRICE_CHANGED + price |
| 2.D | Create booking | 200 | supplierBookingRef like HB-BOOK-* |
| 2.E | Cancel booking | 200 | status = CANCELLED |

## Notes

- All mapping tests work in default profile
- Debug endpoint tests require `dev` profile
- Stub adapters return deterministic results (same inputs = same outputs)
- Booking refs are generated with format: `HB-BOOK-<random>` or `TL-BOOK-<random>`

