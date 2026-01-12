# Postman Collection for Booking Management API

This directory contains Postman collections and environments for testing the Booking Management Microservice.

## Files

1. **Booking_Management_API.postman_collection.json** - Main Postman collection with all API endpoints
2. **Booking_Management_API.postman_environment.json** - Environment variables for local testing

## Import Instructions

### Option 1: Import via Postman UI
1. Open Postman
2. Click **Import** button
3. Select both JSON files:
   - `Booking_Management_API.postman_collection.json`
   - `Booking_Management_API.postman_environment.json`
4. Click **Import**

### Option 2: Import via File System
1. Copy both JSON files to your Postman collections directory
2. In Postman, go to **File → Import**
3. Select the files and import

## Environment Setup

1. After importing, select the **"Booking Management - Local"** environment
2. Update `baseUrl` if your server runs on a different port:
   - Default: `http://localhost:8080`
   - Change to match your server configuration

## Available Requests

### Bookings Collection

#### 1. **Create Booking - HotelBeds**
- **Method:** POST
- **Endpoint:** `/api/bookings`
- **Description:** Creates a booking with HotelBeds supplier
- **Request Body:** Includes `offerPayloadJson` with `supplierCode: "HOTELBEDS"`
- **Auto-saves:** Booking ID to `{{bookingId}}` variable

#### 2. **Create Booking - TravelLanda**
- **Method:** POST
- **Endpoint:** `/api/bookings`
- **Description:** Creates a booking with TravelLanda supplier
- **Request Body:** Includes `offerPayloadJson` with `supplierCode: "TRAVELLANDA"`
- **Auto-saves:** Booking ID to `{{travelLandaBookingId}}` variable

#### 3. **Create Booking - Invalid JSON Payload**
- **Method:** POST
- **Endpoint:** `/api/bookings`
- **Description:** Tests error handling for invalid JSON
- **Expected:** 400 Bad Request

#### 4. **Create Booking - Missing Supplier Code**
- **Method:** POST
- **Endpoint:** `/api/bookings`
- **Description:** Tests error when supplier code is missing
- **Expected:** 400 Bad Request

#### 5. **Get Booking Status**
- **Method:** GET
- **Endpoint:** `/api/bookings/{{bookingId}}/status`
- **Description:** Retrieves booking status
- **Uses:** `{{bookingId}}` from previous create booking request

#### 6. **Cancel Booking**
- **Method:** DELETE
- **Endpoint:** `/api/bookings/{{bookingId}}`
- **Description:** Cancels a booking
- **Expected:** 204 No Content

#### 7. **Cancel Booking - Not Found**
- **Method:** DELETE
- **Endpoint:** `/api/bookings/INVALID-BOOKING-ID`
- **Description:** Tests error handling for invalid booking ID
- **Expected:** 400 or 404

### Supplier Operations (Direct)

#### 8. **Create Booking with Fallback Supplier Code**
- **Method:** POST
- **Endpoint:** `/api/bookings`
- **Description:** Tests fallback supplier code when not in payload
- **Request Body:** Includes `supplierCode` field directly (not in offerPayloadJson)

## Testing Workflow

### Basic Flow
1. **Create Booking - HotelBeds** → Saves booking ID
2. **Get Booking Status** → Verifies booking was created
3. **Cancel Booking** → Cancels the booking

### Error Testing Flow
1. **Create Booking - Invalid JSON Payload** → Verify 400 error
2. **Create Booking - Missing Supplier Code** → Verify 400 error
3. **Cancel Booking - Not Found** → Verify 400/404 error

## Request Examples

### Create Booking Request Body
```json
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

### offerPayloadJson Structure
```json
{
  "supplierCode": "HOTELBEDS" | "TRAVELLANDA",
  "supplierHotelId": "string",
  "rateKey": "string",
  "roomCode": "string",
  "currency": "string (optional)",
  "expectedTotalPriceMinor": "number (optional)"
}
```

## Environment Variables

The collection uses the following variables:
- `{{baseUrl}}` - Base URL for the API (default: `http://localhost:8080`)
- `{{bookingId}}` - Auto-populated from create booking response
- `{{travelLandaBookingId}}` - Auto-populated from TravelLanda booking response

## Notes

- All requests include automatic test scripts that verify response status codes
- Booking IDs are automatically saved to environment variables for chained requests
- The collection includes both success and error test cases
- Update the `baseUrl` variable if testing against a different server

## Troubleshooting

### Connection Refused
- Ensure the Spring Boot application is running
- Check that the server is listening on the correct port (default: 8080)
- Verify `baseUrl` in environment variables

### 404 Not Found
- Verify the endpoint path matches your controller mapping
- Check that the application context path is correct

### 500 Internal Server Error
- Check application logs for detailed error messages
- Verify supplier configuration in `application.yml`
- Ensure supplier services are properly configured

