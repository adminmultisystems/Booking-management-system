# API Purpose Documentation - Complete Flow Guide

## 📋 Table of Contents
1. [Overview](#overview)
2. [API Categories](#api-categories)
3. [Complete Workflow](#complete-workflow)
4. [API Details by Category](#api-details-by-category)
5. [Request/Response Examples](#requestresponse-examples)
6. [Error Handling](#error-handling)

---

## 🎯 Overview

This document explains the **purpose** of each API and the **correct flow** to use them. All APIs require JWT authentication except `/actuator/health`.

**Base URL**: `http://localhost:8080`  
**Authentication**: `Authorization: Bearer user-123` (Header)

---

## 📂 API Categories

### 1. **Admin APIs** (`/v1/admin/**`)
   - Hotel Management
   - Room Type Management
   - Inventory Management

### 2. **Public APIs** (`/v1/**`)
   - Hotel Information
   - Offer Search & Recheck
   - Booking Management

### 3. **Internal APIs** (`/v1/internal/**`)
   - Pricing Intelligence

### 4. **Health Check** (`/actuator/**`)
   - System Health

---

## 🔄 Complete Workflow

### **PHASE 1: SETUP (Admin - One Time Setup)**

```
Step 1: Create Hotel
   ↓
Step 2: Create Room Types (DELUXE, SUITE, etc.)
   ↓
Step 3: Set Inventory (Bulk Upsert for date ranges)
   ↓
Step 4: Verify Inventory (Optional)
```

### **PHASE 2: CUSTOMER SEARCH & BOOKING**

```
Step 5: Get Hotel by Slug (Public)
   ↓
Step 6: Search Offers (Available rooms with pricing)
   ↓
Step 7: Recheck Offer (Optional - verify availability before booking)
   ↓
Step 8: Create Booking (DRAFT status)
   ↓
Step 9: Confirm Booking (DRAFT → RECHECKING → PENDING_CONFIRMATION → CONFIRMED)
   ↓
Step 10: Get Booking Details (Optional)
```

### **PHASE 3: BOOKING MANAGEMENT**

```
Step 11: Cancel Booking (If needed - only CONFIRMED bookings)
```

### **PHASE 4: ADMIN MANAGEMENT (Ongoing)**

```
Step 12: Update Inventory (As needed)
Step 13: Update Hotel (As needed)
Step 14: Update Room Type (As needed)
```

---

## 📝 API Details by Category

---

## 🔧 PHASE 1: ADMIN SETUP APIs

### **1. Create Hotel**
**Endpoint**: `POST /v1/admin/hotels`  
**Purpose**: Create a new hotel in the system. This is the first step in setup.

**Request Body**:
```json
{
  "id": "hotel-001",
  "name": "Grand Hotel"
}
```

**Response**: 
```json
{
  "id": "hotel-001",
  "name": "Grand Hotel",
  "active": true,
  "createdAt": "2026-01-09T18:00:00Z",
  "updatedAt": "2026-01-09T18:00:00Z"
}
```

**Key Points**:
- ✅ Hotel `id` is user-provided (String)
- ✅ Hotel `name` automatically generates `slug` (lowercase, spaces → hyphens)
- ✅ Example: "Grand Hotel" → slug: "grand-hotel"
- ✅ Save `hotelId` from response for next steps

**When to Use**: One-time setup when adding a new hotel to the system.

---

### **2. Create Room Type**
**Endpoint**: `POST /v1/admin/hotels/{hotelId}/room-types`  
**Purpose**: Create room types (DELUXE, SUITE, STANDARD, etc.) for a hotel.

**Request Body**:
```json
{
  "id": "DELUXE",
  "name": "Deluxe Room",
  "maxGuests": 2
}
```

**Response**:
```json
{
  "id": "DELUXE",
  "hotelId": "hotel-001",
  "name": "Deluxe Room",
  "maxGuests": 2,
  "active": true,
  "createdAt": "2026-01-09T18:05:00Z",
  "updatedAt": "2026-01-09T18:05:00Z"
}
```

**Key Points**:
- ✅ Room type `id` is user-provided (String like "DELUXE", "SUITE")
- ✅ `maxGuests` validates guest capacity during booking
- ✅ Same API can be reused for different room types (just change `id` and `name`)
- ✅ Must create room type BEFORE setting inventory

**When to Use**: After creating hotel, create all room types you want to offer.

---

### **3. Set Inventory (Bulk Upsert)**
**Endpoint**: `POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert`  
**Purpose**: Set inventory allotment for a date range. Creates/updates inventory for each day in the range.

**Request Body**:
```json
{
  "roomTypeId": "DELUXE",
  "startDate": "2026-02-01",
  "endDate": "2026-02-28",
  "allotmentQty": 10,
  "stopSell": false
}
```

**Response**:
```
"Successfully upserted 28 inventory allotment(s)"
```

**Key Points**:
- ✅ **IMPORTANT**: Room type must exist before setting inventory (validation added)
- ✅ **IMPORTANT**: Dates must be future (past dates rejected)
- ✅ Creates one inventory row per day from `startDate` (inclusive) to `endDate` (exclusive)
- ✅ If inventory already exists for a date, it updates it
- ✅ `stopSell: true` makes room unavailable for booking
- ✅ Same API can be reused for different room types and date ranges

**Validations**:
- ✅ Room type must exist and belong to the hotel
- ✅ Room type must be active
- ✅ `startDate` and `endDate` must be today or future
- ✅ `startDate` must be before `endDate`
- ✅ `allotmentQty` must be >= 0

**When to Use**: After creating room types, set inventory for future dates.

---

### **4. Get Inventory (Verify)**
**Endpoint**: `GET /v1/admin/hotels/{hotelId}/inventory?roomTypeId=DELUXE&start=2026-02-01&end=2026-02-05`  
**Purpose**: View inventory allotments to verify they were set correctly.

**Query Parameters** (All Optional):
- `roomTypeId`: Filter by room type
- `start`: Start date filter
- `end`: End date filter

**Response**:
```json
[
  {
    "id": "uuid-here",
    "hotelId": "hotel-001",
    "roomTypeId": "DELUXE",
    "date": "2026-02-01",
    "allotmentQty": 10,
    "stopSell": false,
    "currency": "INR",
    "totalPriceMinor": 12000,
    "createdAt": "2026-01-09T18:10:00Z",
    "updatedAt": "2026-01-09T18:10:00Z"
  }
]
```

**When to Use**: Optional - to verify inventory was set correctly.

---

### **5. Update Inventory Row**
**Endpoint**: `PATCH /v1/admin/inventory/{inventoryRowId}`  
**Purpose**: Update a specific inventory row (single date). Use when you need to change inventory for a specific date.

**Request Body**:
```json
{
  "allotmentQty": 15,
  "stopSell": false
}
```

**Key Points**:
- ✅ Updates only provided fields
- ✅ `inventoryRowId` comes from Get Inventory response
- ✅ Use when you need to adjust inventory for specific dates

**When to Use**: Ongoing management - adjust inventory for specific dates.

---

### **6. Update Hotel**
**Endpoint**: `PATCH /v1/admin/hotels/{hotelId}`  
**Purpose**: Update hotel information (name, active status).

**Request Body**:
```json
{
  "name": "Grand Hotel Updated",
  "active": true
}
```

**Key Points**:
- ✅ If hotel name changes, slug automatically updates
- ✅ Can deactivate hotel by setting `active: false`

**When to Use**: Ongoing management - update hotel details.

---

### **7. Update Room Type**
**Endpoint**: `PATCH /v1/admin/room-types/{roomTypeId}`  
**Purpose**: Update room type details (name, maxGuests, active status).

**Request Body**:
```json
{
  "name": "Deluxe Room Updated",
  "maxGuests": 3,
  "active": true
}
```

**When to Use**: Ongoing management - update room type details.

---

## 🌐 PHASE 2: PUBLIC APIS (Customer-Facing)

### **8. Get Hotel by Slug**
**Endpoint**: `GET /v1/hotels/{slug}`  
**Purpose**: Get hotel information using slug (URL-friendly identifier).

**Path Parameter**: `slug` (e.g., "grand-hotel")

**Response**:
```json
{
  "hotelId": "hotel-001",
  "name": "Grand Hotel",
  "slug": "grand-hotel",
  "active": true
}
```

**Key Points**:
- ✅ Slug is auto-generated from hotel name (lowercase, spaces → hyphens)
- ✅ Example: "Grand Hotel" → "grand-hotel"
- ✅ Used in public-facing URLs
- ✅ Fetches from database (not hardcoded)

**When to Use**: Customer wants to view hotel information.

---

### **9. Search Offers**
**Endpoint**: `POST /v1/hotels/{slug}/offers:search`  
**Purpose**: Search for available room offers with pricing. Returns only rooms that have inventory available.

**Request Body**:
```json
{
  "checkIn": "2026-02-15",
  "checkOut": "2026-02-18",
  "guests": 2,
  "roomsCount": 1
}
```

**Response**:
```json
{
  "offers": [
    {
      "offerId": "OWN-3c70b1b3",
      "source": "OWNER",
      "hotelId": "hotel-001",
      "roomTypeId": "DELUXE",
      "checkIn": "2026-02-15",
      "checkOut": "2026-02-18",
      "totalPrice": {
        "amount": 360.00,
        "currency": "USD"
      },
      "cancellationPolicySummary": "Free cancellation up to 48 hours before check-in"
    }
  ]
}
```

**Key Points**:
- ✅ **Real database search** - fetches active room types from database
- ✅ **Real availability check** - only returns rooms with available inventory
- ✅ **Guest capacity validation** - filters rooms by `maxGuests`
- ✅ **Real pricing** - uses PricingIntelligenceClient
- ✅ Returns only available offers (no inventory = no offer)
- ✅ Save `offerId` from response for booking

**Validations**:
- ✅ Dates must be future (past dates rejected)
- ✅ `checkIn` must be before `checkOut`
- ✅ `guests` must fit room `maxGuests`

**When to Use**: Customer searches for available rooms for their dates.

---

### **10. Recheck Offer**
**Endpoint**: `POST /v1/offers:recheck`  
**Purpose**: Recheck offer availability and pricing before booking. Verifies the offer is still valid.

**Request Body**:
```json
{
  "offerId": "OWN-3c70b1b3",
  "checkIn": "2026-02-15",
  "checkOut": "2026-02-18",
  "guests": 2,
  "roomsCount": 1
}
```

**Response (OK)**:
```json
{
  "result": "OK",
  "offer": {
    "offerId": "OWN-3c70b1b3",
    "source": "OWNER",
    "hotelId": "hotel-001",
    "roomTypeId": "DELUXE",
    "checkIn": "2026-02-15",
    "checkOut": "2026-02-18",
    "totalPrice": {
      "amount": 360.00,
      "currency": "USD"
    }
  },
  "message": "Offer is still available"
}
```

**Response (SOLD_OUT)**:
```json
{
  "result": "SOLD_OUT",
  "message": "Room is sold out or no longer available for the requested dates"
}
```

**Key Points**:
- ✅ **Real availability check** - validates inventory is still available
- ✅ **Updated pricing** - returns current pricing
- ✅ Returns `OK` if available, `SOLD_OUT` if not
- ✅ Recommended to call before creating booking

**When to Use**: Before creating booking, to ensure offer is still valid.

---

### **11. Create Booking**
**Endpoint**: `POST /v1/bookings`  
**Purpose**: Create a booking in DRAFT status. Booking is not confirmed yet.

**Request Body**:
```json
{
  "hotelId": "hotel-001",
  "roomTypeId": "DELUXE",
  "checkIn": "2026-02-15",
  "checkOut": "2026-02-18",
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  "specialRequests": "Late checkout please",
  "roomsCount": 1,
  "occupancy": {
    "adults": 2,
    "children": 0
  }
}
```

**Response**:
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "DRAFT"
}
```

**Key Points**:
- ✅ Creates booking in **DRAFT** status
- ✅ **Does NOT reserve inventory** yet
- ✅ **Does NOT check availability** yet
- ✅ Expires in 15 minutes if not confirmed
- ✅ `idempotencyKey` optional (if provided, returns existing booking if same key used)
- ✅ Save `bookingId` from response for confirmation

**Validations**:
- ✅ Dates must be future
- ✅ Guest information required
- ✅ `roomsCount` must be > 0
- ✅ `adults` must be > 0

**When to Use**: Customer selects an offer and wants to book.

---

### **12. Confirm Booking**
**Endpoint**: `POST /v1/bookings/{bookingId}/confirm`  
**Purpose**: Confirm a DRAFT booking. This is when inventory is actually reserved.

**Request Body** (Optional):
```json
{
  "idempotencyKey": "confirm-key-123"
}
```

**Response (SUCCESS)**:
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CONFIRMED",
  "confirmationRef": "OWN-RES-uuid-here",
  "failureReason": null
}
```

**Response (FAILED)**:
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "FAILED",
  "confirmationRef": null,
  "failureReason": "Room is sold out or no longer available for the requested dates"
}
```

**Status Flow**:
```
DRAFT → RECHECKING → PENDING_CONFIRMATION → CONFIRMED
                    ↓
                  FAILED (if sold out)
```

**Key Points**:
- ✅ **Real availability check** happens here
- ✅ **Inventory reservation** happens here (if available)
- ✅ Returns `CONFIRMED` if successful, `FAILED` if sold out
- ✅ HTTP Status: `200 OK` for CONFIRMED, `409 Conflict` for FAILED
- ✅ Idempotent - calling multiple times returns same result

**What Happens**:
1. Status changes: DRAFT → RECHECKING
2. Availability check (real-time)
3. If available: Status → PENDING_CONFIRMATION
4. Inventory reservation
5. Status → CONFIRMED
6. If not available: Status → FAILED

**When to Use**: After creating booking, confirm it to reserve inventory.

---

### **13. Get Booking Details**
**Endpoint**: `GET /v1/bookings/{bookingId}`  
**Purpose**: Get complete booking information including status, dates, guest info, etc.

**Response**:
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CONFIRMED",
  "source": "OWNER",
  "checkIn": "2026-02-15",
  "checkOut": "2026-02-18",
  "roomTypeId": "DELUXE",
  "confirmationRef": "OWN-RES-uuid-here",
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  "roomsCount": 1,
  "occupancy": {
    "adults": 2,
    "children": 0
  }
}
```

**When to Use**: View booking details anytime.

---

### **14. Cancel Booking**
**Endpoint**: `POST /v1/bookings/{bookingId}/cancel`  
**Purpose**: Cancel a confirmed booking. Releases reserved inventory.

**Response**:
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CANCELLED",
  "message": "Booking cancelled"
}
```

**Key Points**:
- ✅ **Only CONFIRMED bookings** can be cancelled
- ✅ **Releases inventory** back to available pool
- ✅ Idempotent - can call multiple times safely

**When to Use**: Customer wants to cancel their booking.

---

## 🔧 PHASE 3: INTERNAL APIS

### **15. Get Pricing Quote**
**Endpoint**: `POST /v1/internal/pricing/quote`  
**Purpose**: Internal API to get pricing quote for a stay. Used by other services.

**Request Body**:
```json
{
  "hotelId": "hotel-001",
  "roomTypeId": "DELUXE",
  "checkIn": "2026-02-15",
  "checkOut": "2026-02-18",
  "guests": 2,
  "currency": "INR"
}
```

**Response**:
```json
{
  "currency": "INR",
  "totalPriceMinor": 36000
}
```

**Key Points**:
- ✅ Internal API (not for direct customer use)
- ✅ Returns price in minor units (paise/cents)
- ✅ Pricing based on room type and nights

**When to Use**: Internal services need pricing information.

---

## 🏥 HEALTH CHECK

### **16. Health Check**
**Endpoint**: `GET /actuator/health`  
**Purpose**: Check if the application is running and healthy.

**Response**:
```json
{
  "status": "UP"
}
```

**Key Points**:
- ✅ **No authentication required**
- ✅ Used for monitoring and load balancers

**When to Use**: System monitoring, health checks.

---

## 📊 Complete Flow Example

### **Scenario: Customer Books a Room**

```
1. Admin Setup (One Time):
   POST /v1/admin/hotels
   → Creates "Grand Hotel" (slug: "grand-hotel")
   
   POST /v1/admin/hotels/hotel-001/room-types
   → Creates "DELUXE" room type
   
   POST /v1/admin/hotels/hotel-001/inventory/bulk-upsert
   → Sets inventory for Feb 2026

2. Customer Search:
   GET /v1/hotels/grand-hotel
   → Gets hotel info
   
   POST /v1/hotels/grand-hotel/offers:search
   → Searches available rooms
   → Returns DELUXE offer with pricing

3. Customer Booking:
   POST /v1/offers:recheck
   → Rechecks offer (optional but recommended)
   
   POST /v1/bookings
   → Creates booking (DRAFT status)
   → Returns bookingId
   
   POST /v1/bookings/{bookingId}/confirm
   → Confirms booking
   → Checks availability
   → Reserves inventory
   → Returns CONFIRMED status

4. View Booking:
   GET /v1/bookings/{bookingId}
   → Gets booking details
```

---

## ⚠️ Important Notes

### **Date Validations**:
- ✅ All dates must be **future** (past dates rejected)
- ✅ `checkIn` must be before `checkOut`
- ✅ Inventory can only be set for future dates

### **Room Type Validations**:
- ✅ Room type must exist before setting inventory
- ✅ Room type must belong to the hotel
- ✅ Room type must be active

### **Booking Validations**:
- ✅ Only CONFIRMED bookings can be cancelled
- ✅ DRAFT bookings expire in 15 minutes
- ✅ Availability is checked at confirmation time

### **Idempotency**:
- ✅ Create Booking: Use `idempotencyKey` to prevent duplicates
- ✅ Confirm Booking: Idempotent (can call multiple times)
- ✅ Cancel Booking: Idempotent

### **Authentication**:
- ✅ All `/v1/**` endpoints require JWT token
- ✅ Format: `Authorization: Bearer user-123`
- ✅ `/actuator/health` does not require authentication

---

## 🔄 Status Transitions

### **Booking Status Flow**:
```
DRAFT
  ↓ (confirm)
RECHECKING
  ↓ (availability check)
PENDING_CONFIRMATION (if available)
  ↓ (reserve inventory)
CONFIRMED
  ↓ (cancel)
CANCELLED

OR

RECHECKING
  ↓ (sold out)
FAILED
```

### **Inventory Reservation Status**:
```
RESERVED (when booking confirmed)
  ↓ (when booking cancelled)
RELEASED
```

---

## 📝 Quick Reference

| API | Method | Purpose | When to Use |
|-----|--------|---------|-------------|
| Create Hotel | POST | Setup hotel | One-time setup |
| Create Room Type | POST | Setup room types | After hotel creation |
| Set Inventory | POST | Set availability | After room types |
| Search Offers | POST | Find available rooms | Customer search |
| Create Booking | POST | Start booking | Customer selects offer |
| Confirm Booking | POST | Reserve inventory | After creating booking |
| Cancel Booking | POST | Release inventory | Customer cancellation |
| Get Inventory | GET | View inventory | Admin verification |
| Update Inventory | PATCH | Adjust inventory | Ongoing management |

---

**Last Updated**: 2026-01-09  
**Version**: 1.0

