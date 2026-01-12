# Supplier Booking Confirmation Flow

This document describes the step-by-step process for confirming a booking with a supplier using the booking management API.

## Overview

The supplier confirmation flow allows you to:
1. Create a booking with supplier offer details
2. Confirm the booking with the supplier
3. Verify the booking status and supplier reference
4. Cancel the booking if needed

## Prerequisites

- Valid supplier credentials configured (HotelBeds or TravelLanda)
- Supplier API endpoints accessible
- Valid `SupplierOfferPayload` JSON structure

## Step-by-Step Process

### Step 1: Create Booking with Supplier Offer Payload

Create a booking via `POST /v1/bookings` with `offerPayloadJson` containing a valid `SupplierOfferPayload` JSON.

**Request:**
```http
POST /v1/bookings
Content-Type: application/json

{
  "hotelId": "hotel-123",
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-05",
  "numberOfGuests": 2,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "offerPayloadJson": "{\"supplierCode\":\"HOTELBEDS\",\"supplierHotelId\":\"HB-HOTEL-123\",\"rateKey\":\"RATE-KEY-456\",\"roomCode\":\"ROOM-789\",\"currency\":\"USD\",\"expectedTotalPriceMinor\":10000}"
}
```

**Required Fields in `offerPayloadJson`:**
- `supplierCode`: Either `"HOTELBEDS"` or `"TRAVELLANDA"`
- `supplierHotelId`: Supplier-specific hotel identifier
- `rateKey`: Rate key for the booking
- `roomCode`: Room code identifier
- `currency`: (Optional) Currency code (e.g., "USD")
- `expectedTotalPriceMinor`: (Optional) Expected total price in minor units (e.g., cents)

**Example `offerPayloadJson` for HotelBeds:**
```json
{
  "supplierCode": "HOTELBEDS",
  "supplierHotelId": "HB-HOTEL-123",
  "rateKey": "RATE-KEY-456",
  "roomCode": "ROOM-789",
  "currency": "USD",
  "expectedTotalPriceMinor": 10000
}
```

**Example `offerPayloadJson` for TravelLanda:**
```json
{
  "supplierCode": "TRAVELLANDA",
  "supplierHotelId": "TL-HOTEL-456",
  "rateKey": "RATE-KEY-789",
  "roomCode": "ROOM-012",
  "currency": "INR",
  "expectedTotalPriceMinor": 50000
}
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "supplierBookingReference": null,
  "hotelId": "hotel-123",
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-05",
  "numberOfGuests": 2,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "status": "PENDING",
  "offerPayloadJson": "{\"supplierCode\":\"HOTELBEDS\",\"supplierHotelId\":\"HB-HOTEL-123\",\"rateKey\":\"RATE-KEY-456\",\"roomCode\":\"ROOM-789\",\"currency\":\"USD\",\"expectedTotalPriceMinor\":10000}",
  "supplierCode": null,
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:30:00"
}
```

### Step 2: Set Booking Supplier Code (if needed)

If the `supplierCode` is not present in the `offerPayloadJson`, you can set it directly on the booking entity as a fallback.

**Request:**
```http
PATCH /v1/bookings/{id}
Content-Type: application/json

{
  "supplierCode": "HOTELBEDS"
}
```

**Note:** The `supplierCode` in the `offerPayloadJson` takes precedence. The booking's `supplierCode` field is only used as a fallback if the payload doesn't contain a supplier code.

### Step 3: Confirm Booking with Supplier

Call `POST /v1/bookings/{id}/confirm` to confirm the booking with the supplier.

**Request:**
```http
POST /v1/bookings/550e8400-e29b-41d4-a716-446655440000/confirm
```

**Response:**
```json
{
  "supplierBookingId": "HB-REF-12345",
  "bookingStatus": "CONFIRMED",
  "confirmationDateTime": "2024-11-15T10:35:00",
  "confirmationNumber": "CONF-HB-REF-12345",
  "message": "Booking created successfully"
}
```

**What happens during confirmation:**
1. The system parses the `offerPayloadJson` to extract `SupplierOfferPayload`
2. Determines the supplier from `payload.supplierCode` (or falls back to `booking.supplierCode`)
3. Routes to the appropriate `SupplierBookingService` (HotelBeds or TravelLanda)
4. Calls the supplier's booking API to create the booking
5. Saves the supplier booking reference (`supplierBookingRef`) to the booking entity
6. Updates the booking status to `CONFIRMED`

### Step 4: Verify Booking Status and Supplier Reference

Retrieve the booking to verify it has been confirmed and the supplier reference has been saved.

**Request:**
```http
GET /v1/bookings/550e8400-e29b-41d4-a716-446655440000
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "supplierBookingReference": "HB-REF-12345",
  "hotelId": "hotel-123",
  "checkInDate": "2024-12-01",
  "checkOutDate": "2024-12-05",
  "numberOfGuests": 2,
  "customerName": "John Doe",
  "customerEmail": "john.doe@example.com",
  "customerPhone": "+1234567890",
  "status": "CONFIRMED",
  "offerPayloadJson": "{\"supplierCode\":\"HOTELBEDS\",\"supplierHotelId\":\"HB-HOTEL-123\",\"rateKey\":\"RATE-KEY-456\",\"roomCode\":\"ROOM-789\",\"currency\":\"USD\",\"expectedTotalPriceMinor\":10000}",
  "supplierCode": null,
  "createdAt": "2024-11-15T10:30:00",
  "updatedAt": "2024-11-15T10:35:00"
}
```

**Verification Checklist:**
- ✅ `status` is `"CONFIRMED"`
- ✅ `supplierBookingReference` is populated with the supplier's booking reference
- ✅ `updatedAt` timestamp reflects the confirmation time

### Step 5: Cancel Booking (Optional)

If needed, cancel the booking using the supplier booking reference.

**Request:**
```http
DELETE /v1/bookings/550e8400-e29b-41d4-a716-446655440000
```

**Response:**
```http
204 No Content
```

**What happens during cancellation:**
1. The system retrieves the booking by ID
2. Extracts the `supplierBookingReference` from the booking
3. Parses the `offerPayloadJson` to determine the supplier
4. Routes to the appropriate `SupplierBookingService`
5. Calls the supplier's cancellation API
6. Updates the booking status to `CANCELLED`

**Verify Cancellation:**
```http
GET /v1/bookings/550e8400-e29b-41d4-a716-446655440000
```

**Response:**
```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "supplierBookingReference": "HB-REF-12345",
  "status": "CANCELLED",
  ...
}
```

## Error Handling

### Missing or Invalid Offer Payload

If `offerPayloadJson` is missing, empty, or contains invalid JSON:

**Error Response:**
```json
{
  "error": "BadRequestException",
  "message": "Missing/invalid supplier offer payload",
  "status": 400
}
```

### Supplier Not Specified

If the supplier code cannot be resolved from the payload or booking:

**Error Response:**
```json
{
  "error": "BadRequestException",
  "message": "Supplier not specified for booking",
  "status": 400
}
```

### Supplier Unavailable

If the supplier API is unavailable (timeout, 5xx error):

**Error Response:**
```json
{
  "error": "ConflictException",
  "message": "Supplier temporarily unavailable",
  "status": 409
}
```

### Rate Sold Out

If the rate is no longer available:

**Error Response:**
```json
{
  "error": "ConflictException",
  "message": "Rate is no longer available",
  "status": 409
}
```

## Best Practices

1. **Always include supplierCode in offerPayloadJson**: While fallback to `booking.supplierCode` is supported, it's better to include it in the payload for clarity.

2. **Validate offerPayloadJson before sending**: Ensure the JSON is valid and contains all required fields before making the API call.

3. **Handle errors gracefully**: Implement retry logic for transient errors (timeouts, 5xx responses).

4. **Store supplierBookingReference**: Always save the supplier booking reference returned during confirmation for future operations.

5. **Verify booking status**: After confirmation, always verify that the booking status is `CONFIRMED` and the supplier reference is saved.

6. **Idempotency**: The confirmation endpoint should be idempotent - calling it multiple times with the same booking should not create duplicate bookings.

## Example Complete Flow

```bash
# Step 1: Create booking
curl -X POST http://localhost:8080/v1/bookings \
  -H "Content-Type: application/json" \
  -d '{
    "hotelId": "hotel-123",
    "checkInDate": "2024-12-01",
    "checkOutDate": "2024-12-05",
    "numberOfGuests": 2,
    "customerName": "John Doe",
    "customerEmail": "john.doe@example.com",
    "customerPhone": "+1234567890",
    "offerPayloadJson": "{\"supplierCode\":\"HOTELBEDS\",\"supplierHotelId\":\"HB-HOTEL-123\",\"rateKey\":\"RATE-KEY-456\",\"roomCode\":\"ROOM-789\"}"
  }'

# Response: {"id": "550e8400-e29b-41d4-a716-446655440000", ...}

# Step 2: Confirm booking
curl -X POST http://localhost:8080/v1/bookings/550e8400-e29b-41d4-a716-446655440000/confirm

# Response: {"supplierBookingId": "HB-REF-12345", "bookingStatus": "CONFIRMED", ...}

# Step 3: Verify booking
curl http://localhost:8080/v1/bookings/550e8400-e29b-41d4-a716-446655440000

# Response: {"status": "CONFIRMED", "supplierBookingReference": "HB-REF-12345", ...}

# Step 4: Cancel booking (optional)
curl -X DELETE http://localhost:8080/v1/bookings/550e8400-e29b-41d4-a716-446655440000

# Step 5: Verify cancellation
curl http://localhost:8080/v1/bookings/550e8400-e29b-41d4-a716-446655440000

# Response: {"status": "CANCELLED", ...}
```

## Notes

- The `offerPayloadJson` must be a valid JSON string containing a `SupplierOfferPayload` object.
- The supplier code in the payload determines which supplier service will handle the booking.
- The confirmation process is asynchronous in nature - the supplier API is called synchronously, but the response may take time.
- If a booking is already confirmed, calling the confirm endpoint again should be idempotent.
- Cancellation will fail if the booking doesn't have a `supplierBookingReference` set.

