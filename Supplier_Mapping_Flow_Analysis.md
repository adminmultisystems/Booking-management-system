# Supplier Mapping APIs - Flow Analysis

## **What is Supplier Mapping?**

Supplier Mapping is **hotel-level configuration** that maps:
- Internal `hotelId` → Supplier's `supplierHotelId`
- Example: `hotel-001` → `HB-12345` (HOTELBEDS)

**Purpose:**
- Enable supplier inventory search for a hotel
- Map internal hotel to supplier's hotel system
- Only ONE ACTIVE supplier per hotel allowed

---

## **When is Supplier Mapping Needed?**

1. **Before Search Operations** - If you want to search supplier inventory
2. **Before Booking** - If you want to create supplier bookings
3. **Optional** - Only needed if using suppliers (HOTELBEDS, TRAVELLANDA)

---

## **Dependencies:**

✅ **Requires:**
- `hotelId` (hotel must exist first)

❌ **Does NOT require:**
- Room Type
- Inventory
- Any other setup

---

## **Best Position in Flow:**

### **Option 1: Right After Create Hotel (Recommended)**
```
1. Health Check
2. Create Hotel
3. Get Supplier Mapping ← Check existing
4. Create/Update Supplier Mapping ← Setup
5. Update Hotel (optional)
6. Create Room Type
7. Set Inventory
...
```

**Why?**
- Hotel-level configuration hai
- Hotel create ke baad immediately setup kar sakte hain
- Room Type/Inventory se independent hai

---

### **Option 2: After All Setup (Current Position)**
```
1. Health Check
2. Create Hotel
3. Update Hotel
4. Get Supplier Mapping ← Current position
5. Create/Update Supplier Mapping ← Current position
6. Create Room Type
7. Set Inventory
...
```

**Why?**
- Hotel setup complete hone ke baad configuration
- Also valid approach

---

### **Option 3: Before Search Operations**
```
1. Health Check
2. Create Hotel
3. Create Room Type
4. Set Inventory
5. Get Supplier Mapping ← Before search
6. Create/Update Supplier Mapping ← Before search
7. Search Offers
...
```

**Why?**
- Search se pehle setup (if using suppliers)
- Logical if supplier mapping is optional

---

## **Recommendation:**

**Best Position: Right After Create Hotel**

**Flow:**
```
1. Health Check
2. Create Hotel
3. Get Supplier Mapping (check existing)
4. Create/Update Supplier Mapping (setup)
5. Update Hotel (optional)
6. Create Room Type
7. Update Room Type (optional)
8. Set Inventory
9. Get Inventory
10. Update Inventory
11. Get Hotel (Public)
12. Search Offers
13. Recheck Offer
14. Create Booking
15. Get Booking
16. Confirm Booking
17. Cancel Booking
18. Get Pricing Quote
```

**Reason:**
- Hotel-level configuration hai
- Hotel create ke baad immediately setup karna logical hai
- Room Type/Inventory se independent hai
- Search/Booking se pehle setup ho jata hai

---

## **Current Position vs Recommended:**

**Current:**
- After Create Hotel + Update Hotel
- Before Create Room Type

**Recommended:**
- Right after Create Hotel
- Before Update Hotel

**Both are valid, but recommended position is more logical for initial setup.**

