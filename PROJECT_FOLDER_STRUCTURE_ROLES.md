# Project Folder Structure & Roles - Complete Guide

---

## 📁 Root Level Folders

### **`src/`** - Source Code Directory
**Role:** Main source code aur test code ka container.  
**Contains:** `main/` (application code) aur `test/` (test code)

---

### **`target/`** - Build Output Directory
**Role:** Maven build process ka output directory.  
**Contains:** Compiled classes, JAR files, generated sources.  
**Note:** Auto-generated, gitignore mein hota hai usually.

---

### **`docs/`** - Documentation Directory
**Role:** Project documentation files store karta hai.  
**Contains:** Additional documentation files (if any).

---

### **`pom.xml`** - Maven Configuration
**Role:** Maven project configuration file.  
**Purpose:** Dependencies, build settings, plugins define karta hai.

---

### **`mvnw` / `mvnw.cmd`** - Maven Wrapper
**Role:** Maven wrapper scripts (Linux/Mac aur Windows).  
**Purpose:** Maven install kiye bina project build/run karne ke liye.

---

## 📁 Source Code Structure (`src/`)

### **`src/main/`** - Main Application Code
**Role:** Production application code ka container.  
**Contains:** `java/` (Java source files) aur `resources/` (configuration files)

---

### **`src/test/`** - Test Code
**Role:** Unit tests aur integration tests ka container.  
**Contains:** `java/` (test classes) aur `resources/` (test configuration)

---

## 📁 Java Source Code (`src/main/java/`)

### **`com/hotelsystems/ai/`** - Root Package
**Role:** Main application package.  
**Contains:** `Application.java` (Spring Boot main class)

---

### **`com/hotelsystems/ai/bookingmanagement/`** - Booking Management Package
**Role:** Booking management microservice ka main package.  
**Contains:** All business logic, adapters, entities, owner inventory modules

---

## 📁 Booking Management Sub-Packages

### **`bookingmanagement/adapter/`** - Adapter Pattern Implementation
**Role:** External systems ke saath integration ke liye adapter pattern.  
**Purpose:** Booking orchestrator ko inventory services se decouple karta hai.

**Files:**
- `OwnerInventoryAdapter.java` - Interface for inventory operations
- `OwnerInventoryAdapterImpl.java` - Primary implementation
- `impl/RealOwnerInventoryAdapter.java` - Alternative implementation
- `RecheckStatus.java` - Enum for recheck status

**Why Needed:**
- Different inventory implementations switch karne ke liye
- Testing ke liye mock adapters use kar sakte hain
- Clean architecture maintain karta hai

---

---

### **`bookingmanagement/ownerinventory/`** - Owner Inventory Module
**Role:** Hotel owner inventory management ka complete module.  
**Purpose:** Hotels, room types, inventory allotments, pricing management.

**Sub-folders:**
- `controller/` - REST API endpoints
- `service/` - Business logic
- `repository/` - Database access
- `entity/` - Database entities
- `dto/` - Data transfer objects
- `exception/` - Exception handling
- `pricing/` - Pricing logic
- `availability/` - Availability calculation
- `reservation/` - Reservation management
- `config/` - Configuration classes

---

## 📁 Owner Inventory Sub-Folders

### **`ownerinventory/controller/`** - REST Controllers
**Role:** HTTP request handling aur response generation.  
**Purpose:** REST API endpoints expose karta hai.

**Files:**
- `HotelController.java` - Hotel management APIs (POST, PATCH)
- `RoomTypeController.java` - Room type management APIs (POST, PATCH)
- `AdminInventoryController.java` - Inventory bulk operations (POST bulk-upsert, GET)
- `AdminInventoryUpdateController.java` - Single inventory row update (PATCH)

**Responsibilities:**
- Request validation
- Response formatting
- HTTP status codes
- Error handling delegation

---

### **`ownerinventory/service/`** - Business Logic Services
**Role:** Business logic implementation.  
**Purpose:** Controllers aur repositories ke beech business rules enforce karta hai.

**Files:**
- `HotelService.java` - Hotel business logic (create, update, validation)
- `RoomTypeService.java` - Room type business logic (create, update, duplicate checks)

**Responsibilities:**
- Business rule validation
- Data transformation
- Transaction management
- Exception handling

---

### **`ownerinventory/repository/`** - Data Access Layer
**Role:** Database operations (JPA repositories).  
**Purpose:** Database queries aur CRUD operations.

**Files:**
- `HotelRepository.java` - Hotel database operations
- `RoomTypeRepository.java` - Room type database operations
- `InventoryAllotmentRepository.java` - Inventory allotment operations
- `InventoryReservationRepository.java` - Reservation operations
- `PricingRepository.java` - Pricing operations (unused)

**Responsibilities:**
- Database queries
- Custom query methods
- Data persistence
- Spring Data JPA features

---

### **`ownerinventory/entity/`** - Database Entities
**Role:** JPA entities (database table mappings).  
**Purpose:** Database tables ko Java objects mein map karta hai.

**Files:**
- `HotelEntity.java` → `hotels` table
- `RoomTypeEntity.java` → `room_types` table
- `InventoryAllotmentEntity.java` → `inventory_allotments` table
- `InventoryReservationEntity.java` → `inventory_reservations` table
- `PricingEntity.java` → `pricing` table (unused)
- `ReservationStatus.java` - Enum for reservation status

**Responsibilities:**
- Table structure definition
- Relationships (foreign keys)
- Constraints (unique, indexes)
- Timestamp management

---

### **`ownerinventory/dto/`** - Data Transfer Objects
**Role:** API request/response objects.  
**Purpose:** Controllers aur external systems ke beech data transfer.

**Request DTOs:**
- `HotelRequest.java` - Create hotel request
- `HotelUpdateRequest.java` - Update hotel request
- `RoomTypeRequest.java` - Create room type request
- `RoomTypeUpdateRequest.java` - Update room type request
- `BulkUpsertInventoryRequest.java` - Bulk inventory upsert request
- `InventoryUpdateRequest.java` - Update inventory request
- `PricingQuoteRequest.java` - Pricing quote request
- `AvailabilityRequest.java` - Availability check request

**Response DTOs:**
- `HotelResponse.java` - Hotel response
- `RoomTypeResponse.java` - Room type response
- `InventoryAllotmentResponse.java` - Inventory response (with pricing)
- `PricingQuote.java` - Pricing quote response
- `AvailabilityResponse.java` - Availability response

**Responsibilities:**
- Request validation annotations
- Response formatting
- Data serialization/deserialization
- API contract definition

---

### **`ownerinventory/exception/`** - Exception Handling
**Role:** Custom exceptions aur global exception handler.  
**Purpose:** Consistent error responses provide karta hai.

**Files:**
- `NotFoundException.java` - 404 Not Found exception
- `DuplicateException.java` - 409 Conflict (duplicate resource)
- `ConflictException.java` - 409 Conflict (business rule violation)
- `GlobalExceptionHandler.java` - Global exception handler (@ControllerAdvice)

**Responsibilities:**
- Custom exception definitions
- HTTP status code mapping
- Error message formatting
- Centralized error handling

---

### **`ownerinventory/pricing/`** - Pricing Logic
**Role:** Pricing calculation aur pricing intelligence.  
**Purpose:** Room type-based pricing calculation.

**Files:**
- `PricingIntelligenceService.java` - Internal pricing service (room type-based calculation)
- `PricingIntelligenceClient.java` - Client for pricing service (delegates to service)
- `PricingIntelligenceInternalController.java` - Internal REST endpoint for pricing
- `PricingService.java` - Legacy pricing service (uses database, currently unused)

**Responsibilities:**
- Pricing calculation logic
- Room type-based rates (DELUXE, SUITE, STANDARD)
- Currency handling
- Nights calculation

---

### **`ownerinventory/availability/`** - Availability Calculation
**Role:** Room availability calculation.  
**Purpose:** Check karta hai ki rooms available hain ya nahi.

**Files:**
- `AvailabilityService.java` - Availability calculation service

**Responsibilities:**
- Availability calculation (allotment - reserved)
- Stop sell check
- Missing allotment handling
- Bookable status determination

---

### **`ownerinventory/reservation/`** - Reservation Management
**Role:** Inventory reservation operations.  
**Purpose:** Booking ke liye inventory reserve/release karta hai.

**Files:**
- `ReservationService.java` - Reservation management service

**Responsibilities:**
- Inventory reservation (lock mechanism)
- Inventory release
- Reservation status management
- Concurrency handling

---

### **`ownerinventory/config/`** - Configuration Classes
**Role:** Spring configuration classes.  
**Purpose:** Beans, HTTP clients, properties configuration.

**Files:**
- `OwnerInventoryConfig.java` - Owner inventory configuration
- `OwnerPricingHttpConfig.java` - HTTP client configuration (legacy, unused)
- `PricingIntelligenceProperties.java` - Pricing properties (legacy, unused)

**Responsibilities:**
- Bean definitions
- Configuration properties
- HTTP client setup
- Component scanning

---

## 📁 Resources (`src/main/resources/`)

### **`resources/application.yml`** - Main Configuration
**Role:** Default application configuration.  
**Purpose:** Database, JPA, H2 console settings.

**Key Settings:**
- Default profile: H2
- Database connection
- JPA/Hibernate settings
- H2 console enabled

---

### **`resources/application-h2.yml`** - H2 Profile
**Role:** H2 in-memory database configuration.  
**Purpose:** Development/testing ke liye H2 database settings.

**Key Settings:**
- H2 database URL
- H2 console path
- Hibernate dialect

---

### **`resources/application-postgres.yml`** - PostgreSQL Profile
**Role:** PostgreSQL database configuration.  
**Purpose:** Production environment ke liye PostgreSQL settings.

**Key Settings:**
- PostgreSQL connection
- Database credentials
- Hibernate dialect

---

### **`resources/static/`** - Static Resources
**Role:** Static files (HTML, CSS, JS, images).  
**Purpose:** Web resources serve karta hai (if needed).

**Status:** Currently empty

---

### **`resources/templates/`** - Template Files
**Role:** Template files (Thymeleaf, etc.).  
**Purpose:** Server-side rendering ke liye templates.

**Status:** Currently empty

---

## 📁 Test Code (`src/test/`)

### **`test/java/`** - Test Source Code
**Role:** Unit tests aur integration tests.  
**Purpose:** Code quality aur functionality verify karta hai.

**Structure:** Same package structure as main code

**Test Files:**
- `availability/AvailabilityServiceTest.java` - Availability service tests
- `pricing/PricingIntelligenceClientTest.java` - Pricing client tests
- `pricing/PricingIntelligenceInternalControllerTest.java` - Pricing API tests
- `reservation/ReservationServiceTest.java` - Reservation service tests

---

### **`test/resources/`** - Test Configuration
**Role:** Test-specific configuration files.  
**Purpose:** Test environment ke liye separate settings.

**Files:**
- `application-test.yml` - Test database configuration

---

## 📁 Documentation Files (Root)

### **`APIs_AND_TABLES_PURPOSE.md`**
**Role:** APIs aur tables ka quick reference.  
**Purpose:** APIs ka purpose aur tables ka usage explain karta hai.

---

### **`PROJECT_COMPLETE_OVERVIEW.md`**
**Role:** Complete project overview.  
**Purpose:** Detailed project documentation, APIs, database design.

---

### **`PROJECT_FOLDER_STRUCTURE_ROLES.md`** (This File)
**Role:** Folder structure aur roles ka guide.  
**Purpose:** Har folder ka role aur responsibility explain karta hai.

---

### **`Complete_APIs_Postman_Collection.json`**
**Role:** Postman collection for API testing.  
**Purpose:** All APIs ko Postman mein import karne ke liye ready collection.

---

## 📊 Folder Summary

### **Main Application Folders:**
1. **`controller/`** - REST API endpoints (4 controllers)
2. **`service/`** - Business logic (2 services)
3. **`repository/`** - Database access (5 repositories)
4. **`entity/`** - Database entities (6 entities)
5. **`dto/`** - Request/Response objects (15 DTOs)
6. **`exception/`** - Exception handling (4 exception classes)
7. **`pricing/`** - Pricing logic (4 pricing classes)
8. **`availability/`** - Availability calculation (1 service)
9. **`reservation/`** - Reservation management (1 service)
10. **`config/`** - Configuration classes (3 config classes)
11. **`adapter/`** - Adapter pattern (4 adapter classes)

### **Configuration Folders:**
- **`resources/`** - Application configuration files
- **`test/resources/`** - Test configuration files

### **Test Folders:**
- **`test/java/`** - Test classes (4 test files)

---

## 🎯 Key Architecture Patterns

### **Layered Architecture:**
```
Controller Layer (REST APIs)
    ↓
Service Layer (Business Logic)
    ↓
Repository Layer (Data Access)
    ↓
Entity Layer (Database)
```

### **Package Organization:**
- **Feature-based:** Owner inventory module sab kuch ek jagah
- **Layer-based:** Controllers, services, repositories separate
- **Separation of Concerns:** Each folder has specific responsibility

---

## 📝 Important Notes

1. **Active Folders:**
   - `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `exception/`, `pricing/`, `availability/`, `reservation/`

2. **Unused/Legacy Folders:**
   - `config/` - Some config classes unused (OwnerPricingHttpConfig, PricingIntelligenceProperties)
   - `pricing/PricingService.java` - Legacy service (unused)

3. **Empty Folders:**
   - `static/`, `templates/` - Currently empty

4. **Test Coverage:**
   - Tests exist for: availability, pricing, reservation services
   - More tests can be added for controllers, services

---

**Last Updated:** January 2024

