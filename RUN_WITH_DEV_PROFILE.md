# Running Application with Dev Profile

## Problem
The debug controller endpoints (`/internal/suppliers/*`) are only available when the `dev` profile is active. If you run the app without specifying a profile, these endpoints will return 404.

## Solution

### Option 1: Run with Maven (Recommended)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### Option 2: Run with Java Command
```bash
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev
```

### Option 3: Set Environment Variable
**Windows:**
```cmd
set SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

**Linux/Mac:**
```bash
export SPRING_PROFILES_ACTIVE=dev
mvn spring-boot:run
```

### Option 4: IntelliJ IDEA
1. Go to **Run** → **Edit Configurations**
2. Select your Application configuration
3. In **VM options** or **Program arguments**, add:
   ```
   --spring.profiles.active=dev
   ```
   Or in **Environment variables**, add:
   ```
   SPRING_PROFILES_ACTIVE=dev
   ```

## Verify Profile is Active

After starting the application, check the logs. You should see:
```
Active profiles: dev
```

If you see:
```
No active profiles set - using default profile
```
Then the dev profile is NOT active, and debug endpoints will return 404.

## Debug Endpoints Available with Dev Profile

- `POST /internal/suppliers/offers/search` - Search offers
- `POST /internal/suppliers/offers/recheck` - Recheck offer
- `POST /internal/suppliers/bookings/create` - Create booking
- `POST /internal/suppliers/bookings/cancel` - Cancel booking

**Security**: All endpoints require `X-Debug-Key: local-debug` header (or value from `debug.key` config).

## Testing with Postman

1. Start app with dev profile: `mvn spring-boot:run -Dspring-boot.run.profiles=dev`
2. Verify logs show: `Active profiles: dev`
3. In Postman, add header: `X-Debug-Key: local-debug`
4. Test endpoint: `POST http://localhost:8080/internal/suppliers/offers/search`

## Configuration

The `application-dev.yml` file contains:
- H2 database configuration
- Debug key: `local-debug` (can be overridden with `DEBUG_KEY` env var)
- SQL logging enabled
- H2 console enabled

