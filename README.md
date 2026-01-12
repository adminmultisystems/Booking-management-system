# 🏨 Hotelsystems.ai - Hotel Booking Management System

A comprehensive Hotel Booking Management System built with Spring Boot that provides complete solutions for managing hotels, room types, inventory, pricing, and bookings with support for both owner-managed inventory and supplier integrations.

## 📋 Table of Contents

- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [API Documentation](#-api-documentation)
- [Project Structure](#-project-structure)
- [Database Setup](#-database-setup)
- [Security](#-security)
- [Testing](#-testing)
- [Health Check](#-health-check)
- [Troubleshooting](#-troubleshooting)

---

## ✨ Features

### Core Features
- ✅ **Hotel Management** - Create and manage hotels with automatic slug generation
- ✅ **Room Type Management** - Define and manage room types (DELUXE, SUITE, STANDARD, etc.)
- ✅ **Inventory Management** - Bulk inventory upsert with date range support
- ✅ **Real-time Availability** - Check room availability in real-time
- ✅ **Booking Lifecycle** - Complete booking flow (DRAFT → RECHECKING → PENDING_CONFIRMATION → CONFIRMED)
- ✅ **Offer Search & Recheck** - Search available offers and recheck before booking
- ✅ **Pricing Intelligence** - Dynamic pricing calculation based on room type and duration
- ✅ **JWT Authentication** - Secure API endpoints with JWT token authentication
- ✅ **Idempotency Support** - Safe retry mechanism for booking operations
- ✅ **Inventory Reservation** - Automatic inventory locking on booking confirmation

### Advanced Features
- ✅ **Date Validation** - Prevents past date bookings and inventory
- ✅ **Room Type Validation** - Ensures inventory can only be set for existing room types
- ✅ **Guest Capacity Validation** - Validates guest count against room capacity
- ✅ **Stop-Sell Functionality** - Temporarily disable bookings for specific dates
- ✅ **Booking Expiration** - DRAFT bookings expire after 15 minutes
- ✅ **Database-backed** - Real-time data from PostgreSQL/H2 database
- ✅ **Spring Profiles** - Support for H2 (development) and PostgreSQL (production)

---

## 🛠 Technology Stack

### Backend Framework
- **Spring Boot** 3.5.9
- **Java** 21
- **Maven** (Build Tool)

### Core Dependencies
- **Spring Web** - REST API development
- **Spring Security** - Authentication & Authorization
- **Spring Data JPA** - Database operations
- **Spring Validation** - Request validation
- **Spring Actuator** - Health checks and monitoring

### Database
- **PostgreSQL** - Production database
- **H2 Database** - Development/testing (in-memory)
- **Flyway** - Database migration tool

### Other Libraries
- **Lombok** - Boilerplate code reduction
- **Jackson** - JSON serialization/deserialization

---

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java Development Kit (JDK) 21** or higher
  ```bash
  java -version  # Should show version 21 or higher
  ```

- **Maven 3.6+** (or use included Maven Wrapper)
  ```bash
  mvn -version  # Should show version 3.6 or higher
  ```

- **PostgreSQL 12+** (Optional - only if using PostgreSQL profile)
  ```bash
  psql --version  # Should show version 12 or higher
  ```

- **Postman** (Optional - for API testing)

---

## 🚀 Installation

### 1. Clone the Repository
```bash
git clone <repository-url>
cd Hotelsystems.ai
```

### 2. Build the Project
```bash
# Using Maven Wrapper (Windows)
mvnw.cmd clean install

# Using Maven Wrapper (Linux/Mac)
./mvnw clean install

# Or using Maven directly
mvn clean install
```

### 3. Verify Build
```bash
# Should create target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar
ls target/*.jar
```

---

## ⚙️ Configuration

### Application Configuration

The application uses `application.yml` with Spring profiles for different environments:

#### Default Profile (H2 - Development)
- **No database setup required**
- In-memory H2 database
- Perfect for local development and testing
- H2 Console available at: `http://localhost:8080/h2-console`

#### PostgreSQL Profile (Production)
- Requires PostgreSQL database
- Configure in `application.yml` under `spring.profiles.active: postgres`
- Update database connection details:
  ```yaml
  spring:
    datasource:
      url: jdbc:postgresql://localhost:5432/hotelsystems
      username: postgres
      password: postgres
  ```

### Environment Variables

You can override configuration using environment variables:

```bash
# Server Port
export SERVER_PORT=8080

# Database (PostgreSQL)
export SPRING_PROFILES_ACTIVE=postgres
export SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/hotelsystems
export SPRING_DATASOURCE_USERNAME=postgres
export SPRING_DATASOURCE_PASSWORD=postgres

# JWT Configuration
export JWT_SECRET=your-secret-key-change-in-production-min-256-bits
export JWT_EXPIRATION=86400000

# CORS Configuration
export CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

---

## 🏃 Running the Application

### Option 1: Using Maven (Recommended for Development)
```bash
# Run with H2 profile (default)
mvnw.cmd spring-boot:run

# Or using Maven directly
mvn spring-boot:run
```

### Option 2: Using JAR File
```bash
# Build first
mvnw.cmd clean package

# Run with H2 profile (default)
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar

# Run with PostgreSQL profile
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=postgres
```

### Option 3: Using IDE
1. Open the project in your IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Run `Application.java` main method
3. Application will start on `http://localhost:8080`

### Verify Application is Running
```bash
# Check health endpoint
curl http://localhost:8080/actuator/health

# Expected response:
# {"status":"UP"}
```

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080
```

### Authentication
All `/v1/**` endpoints require JWT authentication:
```
Authorization: Bearer user-123
```

### API Categories

#### 1. Admin APIs (`/v1/admin/**`)
- **Hotel Management**
  - `POST /v1/admin/hotels` - Create hotel
  - `PATCH /v1/admin/hotels/{id}` - Update hotel

- **Room Type Management**
  - `POST /v1/admin/hotels/{hotelId}/room-types` - Create room type
  - `PATCH /v1/admin/room-types/{roomTypeId}` - Update room type

- **Inventory Management**
  - `POST /v1/admin/hotels/{hotelId}/inventory/bulk-upsert` - Bulk set inventory
  - `GET /v1/admin/hotels/{hotelId}/inventory` - Get inventory
  - `PATCH /v1/admin/inventory/{inventoryRowId}` - Update inventory row

#### 2. Public APIs (`/v1/**`)
- **Hotel Information**
  - `GET /v1/hotels/{slug}` - Get hotel by slug

- **Offer Search & Recheck**
  - `POST /v1/hotels/{slug}/offers:search` - Search available offers
  - `POST /v1/offers:recheck` - Recheck offer availability

- **Booking Management**
  - `POST /v1/bookings` - Create booking (DRAFT)
  - `GET /v1/bookings/{id}` - Get booking details
  - `POST /v1/bookings/{id}/confirm` - Confirm booking
  - `POST /v1/bookings/{id}/cancel` - Cancel booking

#### 3. Internal APIs (`/v1/internal/**`)
- **Pricing Intelligence**
  - `POST /v1/internal/pricing/quote` - Get pricing quote

#### 4. Health Check (`/actuator/**`)
- `GET /actuator/health` - System health check (No auth required)

### Complete API Workflow

1. **Setup Phase (Admin)**
   ```
   Create Hotel → Create Room Types → Set Inventory
   ```

2. **Customer Booking Flow**
   ```
   Get Hotel → Search Offers → Recheck Offer → 
   Create Booking → Confirm Booking → Get Booking Details
   ```

3. **Management Phase**
   ```
   Update Inventory → Update Hotel → Update Room Type
   ```

### Postman Collection

A Postman collection is available in the `postman/` directory:
- `Booking-Management-Service.postman_collection.json`
- `Local-Dev.postman_environment.json`

Import these files into Postman for easy API testing.

### Detailed API Documentation

For complete API documentation with request/response examples, see:
- `PROJECT_OVERVIEW.md` - Complete project overview
- API documentation files in `docs/` directory (if available)

---

## 📁 Project Structure

```
Hotelsystems.ai/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/hotelsystems/ai/
│   │   │       ├── Application.java                    # Main application class
│   │   │       └── bookingmanagement/
│   │   │           ├── adapter/                        # External integrations
│   │   │           ├── auth/                           # JWT authentication
│   │   │           ├── config/                         # Configuration classes
│   │   │           ├── controller/                     # REST controllers
│   │   │           ├── dto/                            # Data Transfer Objects
│   │   │           ├── enums/                          # Enumerations
│   │   │           ├── exception/                      # Exception handling
│   │   │           ├── ownerinventory/                 # Inventory management
│   │   │           ├── repository/                     # Data repositories
│   │   │           ├── service/                        # Business logic
│   │   │           └── util/                           # Utility classes
│   │   └── resources/
│   │       ├── application.yml                         # Application configuration
│   │       └── db/migration/                           # Flyway migrations
│   └── test/                                           # Test files
├── postman/                                            # Postman collections
├── pom.xml                                             # Maven configuration
├── README.md                                           # This file
└── PROJECT_OVERVIEW.md                                 # Project overview
```

---

## 🗄️ Database Setup

### H2 Database (Default - Development)

**No setup required!** H2 is an in-memory database that starts automatically.

**Access H2 Console:**
1. Start the application
2. Navigate to: `http://localhost:8080/h2-console`
3. JDBC URL: `jdbc:h2:mem:hotelsystems`
4. Username: `sa`
5. Password: (leave empty)

**Note:** Data is lost when application stops (in-memory).

### PostgreSQL Database (Production)

#### 1. Install PostgreSQL
```bash
# Ubuntu/Debian
sudo apt-get install postgresql postgresql-contrib

# macOS (using Homebrew)
brew install postgresql

# Windows
# Download from: https://www.postgresql.org/download/windows/
```

#### 2. Create Database
```bash
# Connect to PostgreSQL
psql -U postgres

# Create database
CREATE DATABASE hotelsystems;

# Exit
\q
```

#### 3. Configure Application
Update `application.yml` or set environment variables:
```yaml
spring:
  profiles:
    active: postgres
  datasource:
    url: jdbc:postgresql://localhost:5432/hotelsystems
    username: postgres
    password: your-password
```

#### 4. Run Migrations
Flyway will automatically run migrations on application startup.

---

## 🔒 Security

### JWT Authentication

All `/v1/**` endpoints require JWT authentication:
```
Authorization: Bearer <token>
```

### Security Configuration

- **Public Endpoints:**
  - `/actuator/health` - Health check
  - `/h2-console/**` - H2 console (development only)

- **Protected Endpoints:**
  - All `/v1/**` endpoints require authentication

### JWT Configuration

Configure JWT in `application.yml`:
```yaml
jwt:
  secret: your-secret-key-change-in-production-min-256-bits
  expiration: 86400000  # 24 hours in milliseconds
```

**⚠️ Important:** Change the JWT secret in production!

---

## 🧪 Testing

### Run Tests
```bash
# Run all tests
mvnw.cmd test

# Run with coverage
mvnw.cmd test jacoco:report
```

### Test Configuration

Test configuration is in `src/test/resources/application-test.yml`

### Manual Testing

Use the Postman collection in `postman/` directory for manual API testing.

---

## 🏥 Health Check

### Health Endpoint
```bash
GET http://localhost:8080/actuator/health
```

**Response:**
```json
{
  "status": "UP"
}
```

### Other Actuator Endpoints

- `/actuator/info` - Application information
- `/actuator/health` - Health status

---

## 🔧 Troubleshooting

### Port Already in Use
```bash
# Windows - Find process using port 8080
netstat -ano | findstr :8080

# Kill process (replace PID with actual process ID)
taskkill /PID <PID> /F

# Linux/Mac - Find and kill process
lsof -ti:8080 | xargs kill -9
```

### Java Version Issues
```bash
# Verify Java version (must be 21+)
java -version

# If wrong version, update JAVA_HOME
export JAVA_HOME=/path/to/jdk-21
```

### Database Connection Issues

**H2 (Default):**
- No action needed - H2 starts automatically

**PostgreSQL:**
- Verify PostgreSQL is running: `pg_isready`
- Check connection details in `application.yml`
- Verify database exists: `psql -U postgres -l`

### Build Issues
```bash
# Clean and rebuild
mvnw.cmd clean install

# Skip tests
mvnw.cmd clean install -DskipTests
```

### Application Won't Start
1. Check logs for errors
2. Verify Java version (must be 21+)
3. Check port availability (default: 8080)
4. Verify database connection (if using PostgreSQL)

---

## 📝 Additional Resources

- **Project Overview**: See `PROJECT_OVERVIEW.md` for detailed project documentation
- **API Documentation**: See API documentation files in `docs/` directory
- **Postman Collection**: Import `postman/Booking-Management-Service.postman_collection.json`

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

---

## 📄 License

This project is licensed under the MIT License.

---

## 👥 Support

For issues, questions, or contributions, please open an issue in the repository.

---

**Built with ❤️ using Spring Boot 3.5.9 and Java 21**

