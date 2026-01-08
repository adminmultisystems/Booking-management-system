# TL-0 Guardrail

## Project Overview
This is a standalone Spring Boot microservice named "booking-management-service" with no dependency on the old monolith.

## Base Configuration
- **Base Package**: `com.hotelsystems.ai.bookingmanagement`
- **Database**: PostgreSQL
- **Code must compile and run in IntelliJ**

## Schema Management

### Development Environment
- **Hibernate DDL ONLY**: Hibernate automatically creates and evolves schema (`ddl-auto=update`)
- **Flyway DISABLED**: Do NOT create Flyway migrations during development
- **Rule**: Hibernate manages schema ONLY in dev. Never mix with Flyway.

### Production Environment
- **Flyway ONLY**: All schema changes managed through Flyway migration scripts
- **Hibernate DDL DISABLED**: `ddl-auto=none` - Hibernate must NOT modify schema
- **Rule**: Flyway manages schema ONLY in prod. Never mix with Hibernate DDL.

### Pre-Production Workflow
Before deploying to production:
1. **Freeze entity model** - Ensure all entity changes are complete
2. **Generate baseline schema** from dev database:
   ```bash
   pg_dump --schema-only --no-owner --no-acl -h localhost -U postgres booking_management > schema.sql
   ```
3. **Create V1__baseline.sql** - Copy generated schema into baseline migration
4. **Disable Hibernate DDL** - Verify `ddl-auto=none` in production config
5. **Enable Flyway** - Verify Flyway is enabled in production config
6. **Test migration** - Test on staging database before production

### Critical Rule
**Never mix Hibernate DDL and Flyway in the same environment.**
- Dev: Hibernate DDL only (Flyway disabled)
- Prod: Flyway only (Hibernate DDL disabled)

## Architecture Rules

### Service Ownership
- **Team Lead owns**: booking lifecycle, orchestration, security, controllers, DB schema
- **Adapters for**: supplier and owner inventory logic
- **Stub adapters**: Create stub adapters so the service runs end-to-end

### Authentication
- **Authentication**: userId-based (JWT-ready, simple skeleton)
- Authentication should be implemented with a simple skeleton that is ready for JWT integration

### Feature Scope

#### Implemented
- Owner inventory lives inside this service
- Supplier booking: confirm-first only

#### NOT Implemented
- **Payments**: NOT implemented
- **Supplier offers search**: NOT implemented

## Development Guidelines
- Use adapters pattern for supplier and owner inventory logic
- Create stub adapters to enable end-to-end service execution
- All code must compile and run successfully in IntelliJ

