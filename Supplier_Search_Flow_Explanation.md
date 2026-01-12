# Supplier Search Flow - Complete Explanation

## Overview

This document explains the SUPPLIER SEARCH FLOW when a hotel page is opened and offers are searched using a hotel slug. The focus is on the SUPPLIER path (Hotelbeds / Travellanda), not the owner inventory path.

---

## 1. GET /v1/hotels/{slug} - Hotel Resolution

### Where the Backend Resolves the Slug

The slug resolution happens in the `HotelCatalog` component, which queries the database:

- The system fetches all active hotels from the `hotels` table
- For each hotel, it generates a slug from the hotel name using this algorithm:
  - Convert to lowercase
  - Replace spaces with hyphens
  - Remove special characters (except hyphens)
  - Collapse multiple hyphens into one
  - Remove leading/trailing hyphens
- It matches the provided slug against these generated slugs
- If a match is found, it returns the hotel entity

### Internal Identifiers Available After This Step

After slug resolution, the following identifiers are available:

- **hotelId**: The internal hotel ID from the database (e.g., "hotel-001")
- **slug**: The hotel slug used in the URL (e.g., "grand-hotel-paris")
- **name**: The hotel name (e.g., "Grand Hotel Paris")

### Supplier-Related Information at This Step

**Currently, NO supplier-related information is resolved at this step.**

The `HotelResponse` DTO only contains:
- slug
- hotelId
- name
- city, country, addressLine (if available)
- images, amenities, policiesSummary (if available)

**Important Gap**: The system does NOT fetch supplier mappings (supplierCode, supplierHotelId) during hotel resolution. This information is only available later during offer search routing.

**Recommendation**: If supplier information is needed for display purposes, you could optionally enrich the HotelResponse with supplier mapping data, but this is not currently implemented.

---

## 2. POST /v1/hotels/{slug}/offers:search - Supplier Path Decision

### How the System Decides SUPPLIER vs OWNER

The routing decision happens in `OfferRoutingService.decideSourceForHotel()`:

**Current Implementation (Stub)**:
- If slug contains "supplier" → SUPPLIER
- If hotelId ends with "S" → SUPPLIER
- Otherwise → OWNER (safe default)

**Future Implementation (Expected)**:
- Query `supplier_hotel_mappings` table for the hotelId
- Check if there's an ACTIVE mapping with status = ACTIVE
- If ACTIVE mapping exists → SUPPLIER
- If no ACTIVE mapping → OWNER

**Key Point**: Only ONE supplier can be ACTIVE per hotel at a time. The mapping table enforces this constraint.

### Minimum Inputs Required for Supplier Search

When the system routes to SUPPLIER path, the following inputs are required:

**Hotel Identifier Mapping**:
- **hotelId**: Internal hotel ID (already resolved from slug)
- **supplierHotelId**: Supplier's hotel identifier (from supplier_hotel_mappings table)
- **supplierCode**: HOTELBEDS or TRAVELLANDA (from supplier_hotel_mappings table)

**Search Parameters**:
- **checkIn**: Check-in date (LocalDate)
- **checkOut**: Check-out date (LocalDate)
- **guests**: Number of guests (Integer) - used to derive adults/children
- **roomsCount**: Number of rooms (Integer, defaults to 1)

**Optional Parameters (Not Currently Used)**:
- **currency**: Currency code (e.g., "USD", "EUR") - may be required by supplier API
- **nationality**: Guest nationality code (e.g., "US", "IN") - may be required by supplier API
- **language**: Language code (e.g., "en", "es") - may be required by supplier API
- **childrenAges**: Array of children ages - required if children > 0

**Internal vs Supplier-Specific Identifiers**:

- **Internal**: hotelId (our database ID)
- **Supplier-Specific**: supplierHotelId (supplier's hotel code), supplierCode (which supplier)

---

## 3. Supplier Search Request

### Exact Logical Data Sent to Supplier Search Endpoint

When calling the supplier search adapter, the following data is sent:

**Method Signature**:
```
searchOffers(hotelId, supplierHotelId, checkIn, checkOut, adults, children, rooms)
```

**Data Flow**:
1. **hotelId**: Internal hotel ID (for logging/tracking)
2. **supplierHotelId**: Retrieved from supplier_hotel_mappings table where hotelId matches and status = ACTIVE
3. **checkIn**: From request
4. **checkOut**: From request
5. **adults**: Derived from `guests` parameter (currently guests = adults, children = 0)
6. **children**: Currently defaults to 0 (not extracted from request)
7. **rooms**: From request (defaults to 1 if not provided)

**What Gets Sent to Actual Supplier API** (conceptual):

The supplier adapter will transform this into supplier-specific API calls:

**Hotelbeds-Style Search**:
- Hotel code: supplierHotelId
- Check-in date
- Check-out date
- Number of rooms
- Number of adults per room
- Number of children per room (if applicable)
- Children ages (if children > 0)
- Currency (if required)
- Nationality (if required)
- Language (if required)

**Travellanda-Style Search**:
- Similar structure but may have different field names
- May require different authentication headers
- May have different rate types or board types

### Differences Between Hotelbeds and Travellanda

**Conceptual Differences**:

1. **API Structure**:
   - Hotelbeds: Typically uses REST APIs with JSON payloads
   - Travellanda: May use different endpoint structure or authentication

2. **Rate Types**:
   - Hotelbeds: May return multiple rate types (bookable, non-refundable, etc.)
   - Travellanda: May have different rate categorization

3. **Pricing Model**:
   - Hotelbeds: May return prices in minor units (cents) or major units
   - Travellanda: May use different currency precision

4. **Cancellation Policies**:
   - Hotelbeds: Structured cancellation policy objects
   - Travellanda: May have different policy representation

5. **Room Codes**:
   - Hotelbeds: Uses specific room codes and rate keys
   - Travellanda: May use different identifier schemes

**Important**: The adapter layer abstracts these differences, so the normalization step receives a consistent `SupplierOfferDto` regardless of supplier.

---

## 4. Supplier Search Response

### What the Supplier Typically Returns

A supplier search response typically contains:

**Room Types**:
- Room name/description
- Room code (supplier-specific identifier)
- Board type (breakfast, half-board, full-board, room-only)

**Rate Information**:
- Rate key / Rate ID (CRITICAL for booking)
- Rate type (bookable, non-refundable, etc.)
- Total price
- Currency
- Per-night breakdown (optional)
- Taxes and fees breakdown (optional)

**Policies**:
- Cancellation policy (free cancellation deadline, penalty structure)
- Payment type (pay now, pay at hotel)
- Meal plan details
- Special conditions

**Availability**:
- Number of rooms available
- Minimum/maximum stay requirements

### Fields That MUST Be Preserved for Later Steps

**For Recheck**:
- **supplierCode**: Which supplier (HOTELBEDS or TRAVELLANDA)
- **supplierHotelId**: Supplier's hotel identifier
- **rateKey / rateId**: The rate identifier (CRITICAL - without this, you cannot recheck or book)
- **roomCode**: Room type identifier

**For Create Booking**:
- **rateKey / rateId**: Required to create booking
- **supplierCode**: Required to route to correct supplier adapter
- **supplierHotelId**: Required for booking API call
- **roomCode**: May be required for booking
- **price breakdown**: Should be stored for price validation

**For Confirm Booking**:
- **rateKey / rateId**: MUST be present in offerPayloadJson
- **cancellation policy**: Should be stored in policySnapshotJson
- **price breakdown**: Should be stored in priceSnapshotJson
- **payment type**: Should be stored (pay now vs pay at hotel affects booking flow)

**Critical Fields That Must NEVER Be Lost**:
1. **rateKey**: Without this, you cannot recheck or confirm the booking
2. **supplierCode**: Without this, you cannot route to the correct supplier
3. **supplierHotelId**: Without this, you cannot make supplier API calls
4. **roomCode**: May be required for booking confirmation

---

## 5. Normalization Step

### How Supplier Response is Normalized

The supplier adapter receives `SupplierOfferDto` from the supplier API and normalizes it into internal `OfferDto`:

**Normalization Process**:

1. **offerId Generation**:
   - Format: "SUP-{deterministic-hash}"
   - Generated from: hotelId + roomType + checkIn + checkOut
   - This allows the system to identify supplier offers

2. **source Field**:
   - Set to `OfferSource.SUPPLIER`

3. **hotelId**:
   - Set to internal hotelId (not supplierHotelId)

4. **roomTypeId**:
   - Mapped from supplier's room code or room name
   - May need mapping table if supplier room codes don't match internal room types

5. **totalPrice**:
   - Converted to `MoneyDto` with amount and currency
   - Handles currency conversion if needed

6. **cancellationPolicySummary**:
   - Extracted from supplier's cancellation policy
   - Human-readable summary (e.g., "Free cancellation up to 24 hours before check-in")

7. **payload Field (CRITICAL)**:
   - Stores the COMPLETE supplier response as JSON
   - This MUST include:
     - supplierCode
     - supplierHotelId
     - rateKey / rateId
     - roomCode
     - Any other fields needed for recheck/booking

### What an Internal Offer MUST Contain

**To Support Recheck**:
- **offerId**: To identify which offer to recheck
- **payload**: Must contain rateKey and supplierCode
- **source**: Must be SUPPLIER
- **hotelId**: Internal hotel ID
- **checkIn/checkOut**: Dates for recheck

**To Support Create Booking**:
- **offerId**: To reference the offer
- **payload**: Must contain ALL fields needed for booking:
  - supplierCode
  - supplierHotelId
  - rateKey
  - roomCode
  - currency
  - expectedTotalPriceMinor (for price validation)

**To Support Confirm Booking**:
- **payload**: Must be stored in booking.offerPayloadJson
- **priceSnapshot**: Should be stored in booking.priceSnapshotJson
- **policySnapshot**: Should be stored in booking.policySnapshotJson

### Fields That Should NEVER Be Lost

**During Normalization**:
1. **rateKey**: If lost, booking is impossible
2. **supplierCode**: If lost, routing fails
3. **supplierHotelId**: If lost, API calls fail
4. **roomCode**: If lost, booking may fail
5. **Raw supplier response**: Should be preserved in payload for debugging

**Storage Strategy**:
- Store complete supplier response in `OfferDto.payload` (JsonNode)
- Extract critical fields into top-level OfferDto fields for easy access
- Keep payload intact for later use in booking flow

---

## 6. Final API Response to Frontend

### What Frontend Receives from /offers:search

The frontend receives `OffersSearchResponse` containing a list of `OfferDto`:

**Frontend-Facing Fields**:
- **offerId**: Unique identifier (e.g., "SUP-abc123")
- **source**: "SUPPLIER" or "OWNER"
- **hotelId**: Internal hotel ID
- **roomTypeId**: Room type identifier
- **checkIn**: Check-in date
- **checkOut**: Check-out date
- **totalPrice**: Money object with amount and currency
- **cancellationPolicySummary**: Human-readable policy text

**Backend-Only Fields (Should NOT Be Exposed)**:
- **payload**: Contains raw supplier data including rateKey, supplierHotelId, etc.
  - **Why hidden**: Frontend should not see supplier-specific identifiers
  - **Why needed**: Backend needs this for recheck and booking

### Why Frontend Should NOT See Raw Supplier Payloads

1. **Security**: Supplier-specific identifiers (rateKey, supplierHotelId) should not be exposed to frontend
2. **Abstraction**: Frontend should work with normalized offers, not supplier-specific formats
3. **Flexibility**: If supplier changes, frontend code doesn't need updates
4. **Data Integrity**: Prevents frontend from manipulating supplier data
5. **Privacy**: Some supplier data may be sensitive

**How Frontend Uses Offers**:
- Frontend displays offers to user
- User selects an offer
- Frontend sends `offerId` (not payload) when creating booking
- Backend looks up the offer and retrieves payload internally

---

## 7. Common Mistakes to Avoid

### Mistake 1: Losing Supplier rateKey During Search

**Problem**: If rateKey is not stored in the offer payload, you cannot recheck or book.

**Solution**:
- Always store complete supplier response in `OfferDto.payload`
- Extract rateKey and store it explicitly in payload
- Validate that rateKey exists before returning offer to frontend

**Validation**: After normalization, verify payload contains rateKey field.

### Mistake 2: Not Storing Cancellation/Payment Policy Snapshots

**Problem**: If policy is not stored, you cannot show accurate cancellation terms during booking or handle disputes.

**Solution**:
- Store cancellation policy in `OfferDto.cancellationPolicySummary` (human-readable)
- Store full policy details in `OfferDto.payload` (structured data)
- When creating booking, copy policy to `booking.policySnapshotJson`

**Validation**: Ensure policy information is preserved through the entire flow.

### Mistake 3: Mixing Owner and Supplier Logic in the Same Flow

**Problem**: If owner and supplier logic are mixed, code becomes hard to maintain and bugs occur.

**Solution**:
- Use adapter pattern: `OfferSearchAdapter` interface with separate implementations
- Use routing service: `OfferRoutingService` decides SUPPLIER vs OWNER
- Keep adapters separate: `SupplierOfferAdapter` vs `OwnerOfferAdapter`
- Use source field: `OfferSource.SUPPLIER` vs `OfferSource.OWNER` to route correctly

**Validation**: Ensure no supplier-specific code exists in owner adapter and vice versa.

### Mistake 4: Not Handling Supplier-Specific Fields

**Problem**: Different suppliers may require different fields (currency, nationality, language).

**Solution**:
- Make adapter configurable per supplier
- Store supplier-specific requirements in configuration
- Handle missing fields gracefully (defaults or errors)

### Mistake 5: Not Validating Supplier Response

**Problem**: If supplier returns invalid data, booking will fail later.

**Solution**:
- Validate that rateKey exists and is not null/empty
- Validate that supplierCode matches expected values
- Validate that prices are positive numbers
- Validate that dates are in the future

### Mistake 6: Exposing Supplier Payload to Frontend

**Problem**: Frontend sees supplier-specific data, creating coupling.

**Solution**:
- Never return `payload` field in API response (or mark it as internal)
- Use `offerId` as the only identifier frontend needs
- Backend retrieves payload internally when needed

---

## Summary

The supplier search flow follows this pattern:

1. **Hotel Resolution**: Slug → hotelId (no supplier info yet)
2. **Routing Decision**: Check supplier mappings → SUPPLIER or OWNER
3. **Supplier Search**: Call supplier API with supplierHotelId, dates, occupancy
4. **Normalization**: Convert supplier response → internal OfferDto (preserve rateKey in payload)
5. **Response**: Return normalized offers to frontend (hide payload)
6. **Booking**: Frontend sends offerId → Backend retrieves payload → Create booking

**Key Principle**: The payload field is the bridge between search and booking. It must contain everything needed for recheck and confirmation, especially the rateKey.

