# APIs & Tables Purpose - Quick Reference

---

## 📡 APIs Purpose (8 APIs)

### **1. Hotel Management APIs (2)**

#### **POST /v1/admin/hotels** - Create Hotel
**Purpose:** Naya hotel create karna system mein.  
**Use Case:** Admin panel se naya hotel add karna.  
**Frontend:** "Add New Hotel" form se hotel ID aur name submit karna.

---

#### **PATCH /v1/admin/hotels/{hotelId}** - Update Hotel
**Purpose:** Existing hotel ki information update karna (name, active status).  
**Use Case:** Hotel ka name change karna ya hotel ko inactive karna.  
**Frontend:** Edit hotel form se changes submit karna.

---

### **2. Room Type Management APIs (2)**

#### **POST /v1/admin/hotels/{hotelId}/room-types** - Create Room Type
**Purpose:** Hotel ke liye naya room type add karna (DELUXE, SUITE, STANDARD, etc.).  
**Use Case:** Hotel setup ke time different room types define karna.  
**Frontend:** Hotel details page par "Add Room Type" form.  
**Important:** Room type ID (DELUXE, SUITE, STANDARD) pricing calculation ke liye use hota hai.

---

#### **PATCH /v1/admin/room-types/{roomTypeId}** - Update Room Type
**Purpose:** Room type ki information update karna (name, max guests, active status).  
**Use Case:** Room type details modify karna.  
**Frontend:** Edit room type form.

---

### **3. Inventory Management APIs (3)**

#### **POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert** - Bulk Upsert Inventory
**Purpose:** Date range ke liye ek saath multiple dates par inventory allotment create/update karna.  
**Use Case:** Ek mahine ya ek hafte ke liye same inventory set karna.  
**Frontend:** Inventory calendar par date range select karke bulk update.  
**Example:** 1 Feb se 5 Feb tak 10 rooms available set karna.

---

#### **GET /v1/admin/hotels/{hotelId}/inventory** - Get Inventory
**Purpose:** Hotel ki inventory allotments fetch karna with optional filters (room type, date range).  
**Use Case:** Inventory calendar/grid view mein data display karna.  
**Frontend:** Inventory management page par filters apply karke data fetch.  
**Response:** Inventory details ke saath pricing information bhi included.

---

#### **PATCH /v1/admin/inventory/{inventoryRowId}** - Update Single Inventory Row
**Purpose:** Ek specific date ke liye inventory allotment update karna (allotment quantity, stop sell).  
**Use Case:** Specific date par inventory change karna ya stop sell enable/disable karna.  
**Frontend:** Inventory calendar mein specific date par click karke edit modal.

---

### **4. Internal Pricing Intelligence API (1)**

#### **POST /v1/internal/pricing/quote** - Get Pricing Quote
**Purpose:** Stay ke liye pricing quote calculate karna (room type-based pricing).  
**Use Case:** Booking flow mein pricing display karna ya inventory responses mein pricing add karna.  
**Frontend:** Internal service calls ke liye.  
**Pricing Logic:**
- DELUXE: 12000 per night
- SUITE: 20000 per night
- STANDARD: 8000 per night
- Default: 10000 per night

**Calculation:** Nights × Base Rate (room type based)

---

## 🗄️ Tables Purpose

### **Active Tables (4)**

#### **1. `hotels` Table**
**Purpose:** Hotel master data store karta hai.  
**Contains:**
- Hotel ID (unique identifier)
- Hotel name
- Active status (soft delete)
- Timestamps

**Used By:**
- HotelController (create/update hotel)
- RoomTypeService (hotel existence validation)

**Why Needed:**
- Hotel setup aur management
- Room types aur inventory ke liye parent reference
- Soft delete support (inactive hotels ko disable karna)

---

#### **2. `room_types` Table**
**Purpose:** Room type master data store karta hai (hotel ke saath linked).  
**Contains:**
- Room type ID (DELUXE, SUITE, STANDARD, etc.)
- Hotel ID (foreign key)
- Room type name
- Max guests
- Active status (soft delete)
- Timestamps

**Used By:**
- RoomTypeController (create/update room types)
- AdminInventoryController (inventory operations)
- PricingIntelligenceService (room type-based pricing)

**Why Needed:**
- Different room types define karna (DELUXE, SUITE, STANDARD)
- Inventory allotments ke liye reference
- Pricing calculation ke liye room type ID use hota hai
- Hotel ke saath relationship maintain karna

---

#### **3. `inventory_allotments` Table**
**Purpose:** Daily inventory allotment store karta hai (kitne rooms available hain har date ko).  
**Contains:**
- Hotel ID
- Room type ID
- Date (specific date)
- Allotment quantity (available rooms)
- Stop sell flag (bookings block karna)
- Timestamps

**Used By:**
- AdminInventoryController (bulk upsert, get inventory)
- AdminInventoryUpdateController (single row update)
- AvailabilityService (availability calculation)
- ReservationService (inventory locking for reservations)

**Why Needed:**
- Daily inventory tracking (har date ko kitne rooms available)
- Availability calculation ke liye base data
- Stop sell functionality (specific dates par bookings block)
- Bulk operations support (date range ke liye)

---

#### **4. `inventory_reservations` Table**
**Purpose:** Booking ke liye inventory reservations track karta hai (inventory hold/release).  
**Contains:**
- Booking ID (for future booking flow)
- Hotel ID
- Room type ID
- Check-in date
- Check-out date
- Rooms count (kitne rooms reserved)
- Status (RESERVED or RELEASED)
- Timestamps

**Used By:**
- ReservationService (reserve/release inventory)
- AvailabilityService (availability calculation - reserved rooms minus karke)

**Why Needed:**
- Booking flow mein inventory reserve karna (temporary hold)
- Availability calculation: Available = Allotment - Reserved
- Booking cancel hone par inventory release karna
- **Note:** Abhi directly public API se use nahi ho raha, lekin future booking flow ke liye ready hai

---

### **Unused/Legacy Tables (Not Currently Used)**

#### **5. `pricing` Table**
**Status:** ❌ Not Used  
**Reason:** Internal pricing intelligence service use hota hai (no database storage needed)

---

### **Removed Tables**

#### **6. `room_inventory` Table**
**Status:** ❌ Removed  
**Reason:** `inventory_allotments` table use ho raha hai instead, isliye table remove kar diya gaya

---

#### **7. `bookings` Table**
**Status:** ❌ Removed  
**Reason:** Abhi booking flow implement nahi hai, isliye table remove kar diya gaya

---

## 📊 Quick Summary

### **APIs (8 Total):**
- **Hotel Management:** 2 APIs (Create, Update)
- **Room Type Management:** 2 APIs (Create, Update)
- **Inventory Management:** 3 APIs (Bulk Upsert, Get, Update Single)
- **Internal Pricing:** 1 API (Get Quote)

### **Active Tables (4 Total):**
- **`hotels`** - Hotel master data
- **`room_types`** - Room type master data
- **`inventory_allotments`** - Daily inventory tracking
- **`inventory_reservations`** - Inventory reservations (future booking flow)

---

**Last Updated:** January 2024

