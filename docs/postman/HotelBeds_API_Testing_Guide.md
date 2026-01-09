# HotelBeds API Testing Guide

## What You Need from HotelBeds

To test the HotelBeds API, you need the following credentials from your HotelBeds account:

### Required Credentials:
1. **API Key** - Your HotelBeds API key
2. **Secret** - Your HotelBeds API secret (for signature generation)
3. **Base URL** - The HotelBeds API endpoint:
   - **Test Environment**: `https://api.test.hotelbeds.com/hotel-api/1.0`
   - **Production Environment**: `https://api.hotelbeds.com/hotel-api/1.0`

### How to Get Credentials:
1. Log in to your HotelBeds Partner Portal
2. Navigate to **API Access** or **Developer Tools**
3. Generate or retrieve your **API Key** and **Secret**
4. Note the API version and base URL

---

## Setup Instructions

### Step 1: Configure Environment Variables

Set these environment variables before starting the application:

```bash
# Windows (PowerShell)
$env:HOTELBEDS_BASE_URL="https://api.test.hotelbeds.com/hotel-api/1.0"
$env:HOTELBEDS_API_KEY="your-api-key-here"
$env:HOTELBEDS_SECRET="your-secret-here"
$env:HOTELBEDS_TIMEOUT_MS="30000"
$env:DEBUG_KEY="your-debug-key-here"  # For debug controller security

# Linux/Mac
export HOTELBEDS_BASE_URL="https://api.test.hotelbeds.com/hotel-api/1.0"
export HOTELBEDS_API_KEY="your-api-key-here"
export HOTELBEDS_SECRET="your-secret-here"
export HOTELBEDS_TIMEOUT_MS="30000"
export DEBUG_KEY="your-debug-key-here"
```

### Step 2: Update application.yml (Optional)

Alternatively, you can set defaults in `application.yml`:

```yaml
suppliers:
  hotelbeds:
    baseUrl: ${HOTELBEDS_BASE_URL:https://api.test.hotelbeds.com/hotel-api/1.0}
    apiKey: ${HOTELBEDS_API_KEY:}
    secret: ${HOTELBEDS_SECRET:}
    timeoutMs: ${HOTELBEDS_TIMEOUT_MS:30000}
```

### Step 3: Start Application with Profile

For testing with the debug controller, use the `dev` or `live-hotelbeds` profile:

```bash
# Using dev profile (points to configured baseUrl)
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# Or using live-hotelbeds profile (explicitly for live testing)
java -jar target/Hotelsystems.ai-0.0.1-SNAPSHOT.jar --spring.profiles.active=live-hotelbeds
```

### Step 4: Verify Debug Controller is Active

The debug controller will be available at:
- Base URL: `http://localhost:8080/internal/hotelbeds`
- All endpoints require `X-Debug-Key` header

---

## Postman Collection Setup

### Import the Collection

1. Open Postman
2. Click **Import**
3. Select `HotelBeds_API.postman_collection.json`
4. Select `HotelBeds_API.postman_environment.json`

### Configure Environment Variables

In Postman, set these environment variables:

| Variable | Value | Description |
|----------|-------|-------------|
| `baseUrl` | `http://localhost:8080` | Your application base URL |
| `debugKey` | `your-debug-key-here` | Must match `DEBUG_KEY` env var |
| `hotelbedsBookingRef` | (auto-filled) | Booking reference from create booking |

---

## Testing Flow

### 1. Health Check
Test if HotelBeds API is accessible:
- **Endpoint**: `GET /internal/hotelbeds/health`
- **Headers**: `X-Debug-Key: {{debugKey}}`

### 2. Search Hotels
Search for available hotels:
- **Endpoint**: `POST /internal/hotelbeds/hotels`
- **Headers**: `X-Debug-Key: {{debugKey}}`
- **Body**: See sample in collection

### 3. Check Rates
Check rates for specific rateKeys:
- **Endpoint**: `POST /internal/hotelbeds/checkrates`
- **Headers**: `X-Debug-Key: {{debugKey}}`
- **Body**: See sample in collection

### 4. Create Booking
Create a booking with HotelBeds:
- **Endpoint**: `POST /internal/hotelbeds/bookings`
- **Headers**: `X-Debug-Key: {{debugKey}}`
- **Body**: See sample in collection
- **Note**: Save the booking reference for later operations

### 5. Get Booking
Retrieve booking details:
- **Endpoint**: `GET /internal/hotelbeds/bookings/{{hotelbedsBookingRef}}`
- **Headers**: `X-Debug-Key: {{debugKey}}`

### 6. Cancel Booking (Simulation)
Test cancellation without actually canceling:
- **Endpoint**: `DELETE /internal/hotelbeds/bookings/{{hotelbedsBookingRef}}/simulate`
- **Headers**: `X-Debug-Key: {{debugKey}}`

### 7. Cancel Booking (Actual)
Actually cancel the booking:
- **Endpoint**: `DELETE /internal/hotelbeds/bookings/{{hotelbedsBookingRef}}`
- **Headers**: `X-Debug-Key: {{debugKey}}`

---

## Sample Request Bodies

### Search Hotels Request
```json
{
  "stay": {
    "checkIn": "2024-12-01",
    "checkOut": "2024-12-05"
  },
  "occupancies": [
    {
      "rooms": 1,
      "adults": 2,
      "children": 0
    }
  ],
  "destination": {
    "code": "BCN"
  }
}
```

### Check Rates Request
```json
{
  "rateKeys": [
    "your-rate-key-here"
  ]
}
```

### Create Booking Request
```json
{
  "holder": {
    "name": "John",
    "surname": "Doe"
  },
  "rooms": [
    {
      "rateKey": "your-rate-key-here",
      "paxes": [
        {
          "roomId": 1,
          "type": "AD",
          "name": "John",
          "surname": "Doe"
        }
      ]
    }
  ],
  "clientReference": "TEST-BOOKING-001",
  "remark": "Test booking",
  "tolerance": 0
}
```

---

## Troubleshooting

### 401/403 Errors
- **Issue**: Invalid API key or secret
- **Solution**: Verify `HOTELBEDS_API_KEY` and `HOTELBEDS_SECRET` are correct
- **Check**: Signature is generated correctly (check logs)

### 403 on Debug Endpoints
- **Issue**: Missing or invalid `X-Debug-Key` header
- **Solution**: Set `DEBUG_KEY` env var and use it in Postman header

### Connection Errors
- **Issue**: Wrong base URL or network issues
- **Solution**: Verify `HOTELBEDS_BASE_URL` is correct and accessible

### Timeout Errors
- **Issue**: API is slow or timeout too short
- **Solution**: Increase `HOTELBEDS_TIMEOUT_MS` (default: 30000ms)

---

## Security Notes

⚠️ **Important Security Reminders:**
- Never commit API keys or secrets to version control
- Use environment variables for credentials
- The debug controller is only enabled in `dev` or `live-hotelbeds` profiles
- Always use `X-Debug-Key` header for debug endpoints
- In production, disable debug controller

---

## Next Steps

1. Get your HotelBeds credentials
2. Set environment variables
3. Start the application with `dev` profile
4. Import Postman collection
5. Test endpoints starting with health check
6. Use the booking reference from create booking for subsequent operations
