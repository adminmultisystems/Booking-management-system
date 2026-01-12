# Updated API Collection - Summary

## ✨ **New Features Added**

### 1. **Idempotency Key Support** ✅

**What it does:**
- Prevents duplicate bookings if the same request is sent multiple times
- If you use the same `idempotencyKey`, you'll get the existing booking instead of creating a new one

**How to use:**
```json
POST /v1/bookings
{
  "hotelId": "hotel-001",
  "roomTypeId": "DELUXE",
  "checkIn": "2025-01-20",
  "checkOut": "2025-01-23",
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  "idempotencyKey": "booking-123"  // ← Optional field
}
```

**Response includes:**
```json
{
  "bookingId": "uuid-here",
  "status": "DRAFT",
  "idempotencyKey": "booking-123",  // ← Returns the key
  "source": "OWNER"  // ← New field
}
```

---

### 2. **Source Field (OWNER/SUPPLIER)** ✅

**What it does:**
- Automatically determines if booking is from OWNER inventory or SUPPLIER
- Set at booking creation time (not just at confirmation)

**Logic:**
- **OWNER**: When `supplierCode` is `null` (direct owner inventory)
- **SUPPLIER**: When `supplierCode` is provided

**Response includes:**
```json
{
  "bookingId": "uuid-here",
  "status": "DRAFT",
  "source": "OWNER",  // ← Automatically set
  "idempotencyKey": "booking-123"
}
```

---

## 📋 **Updated APIs**

### **Create Booking API (API 12/16)**

**Request:**
- ✅ Added `idempotencyKey` field (optional)

**Response:**
- ✅ Added `idempotencyKey` field
- ✅ Added `source` field (OWNER or SUPPLIER)

---

### **Get Booking API (API 13/16)**

**Response:**
- ✅ Includes `idempotencyKey` (if provided during creation)
- ✅ Includes `source` field (OWNER or SUPPLIER)

---

### **Confirm Booking API (API 14/16)**

**Response:**
- ✅ Includes `idempotencyKey` (if provided during creation)
- ✅ Includes `source` field (OWNER or SUPPLIER)
- ✅ Includes `confirmationRef` (confirmation reference number)

---

### **Cancel Booking API (API 15/16)**

**Response:**
- ✅ Includes `idempotencyKey` (if provided during creation)
- ✅ Includes `source` field (OWNER or SUPPLIER)
- ✅ Updated status: CANCELLED

---

## 🔄 **Complete Flow Example**

### **Step 1: Create Booking with Idempotency Key**
```json
POST /v1/bookings
{
  "hotelId": "hotel-001",
  "roomTypeId": "DELUXE",
  "checkIn": "2025-01-20",
  "checkOut": "2025-01-23",
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  "idempotencyKey": "unique-booking-123"
}
```

**Response:**
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "DRAFT",
  "idempotencyKey": "unique-booking-123",
  "source": "OWNER"
}
```

### **Step 2: Get Booking**
```json
GET /v1/bookings/550e8400-e29b-41d4-a716-446655440000
```

**Response:**
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "DRAFT",
  "source": "OWNER",
  "idempotencyKey": "unique-booking-123",
  "checkIn": "2025-01-20",
  "checkOut": "2025-01-23",
  "roomTypeId": "DELUXE",
  "guestName": "John Doe",
  "guestEmail": "john@example.com",
  "guestPhone": "+1234567890",
  ...
}
```

### **Step 3: Confirm Booking**
```json
POST /v1/bookings/550e8400-e29b-41d4-a716-446655440000/confirm
```

**Response:**
```json
{
  "bookingId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "CONFIRMED",
  "source": "OWNER",
  "idempotencyKey": "unique-booking-123",
  "confirmationRef": "CONF-12345",
  ...
}
```

---

## 🎯 **Key Benefits**

1. **Idempotency**: Safe to retry requests without creating duplicates
2. **Source Tracking**: Know immediately if booking is OWNER or SUPPLIER
3. **Better Debugging**: Idempotency key helps track duplicate requests
4. **Consistent Responses**: All booking responses include source and idempotency key

---

## 📁 **Files Updated**

1. ✅ `CreateBookingResponse.java` - Added `idempotencyKey` and `source` fields
2. ✅ `BookingResponse.java` - Added `idempotencyKey` field
3. ✅ `BookingOrchestrationService.java` - Added source logic and idempotency key return
4. ✅ `BookingMapper.java` - Added idempotency key mapping
5. ✅ `Complete_API_Workflow_Postman.json` - Updated collection with new fields

---

## 🚀 **How to Use**

1. Import `Complete_API_Workflow_Postman.json` into Postman
2. Use `idempotencyKey` in Create Booking request (optional)
3. Check `source` field in all booking responses
4. Use `idempotencyKey` for duplicate prevention

---

**Total APIs: 16** ✅  
**All APIs Updated** ✅  
**Ready to Test** ✅

