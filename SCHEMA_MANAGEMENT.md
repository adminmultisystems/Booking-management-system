# Schema Management Guide

## Overview

This service uses a strict separation between development and production schema management:

- **Development**: Hibernate DDL manages schema automatically
- **Production**: Flyway migrations manage schema changes

## Development Workflow

### During Development

1. **Modify Entity Models**
   - Update `@Entity` classes in `domain/entity` package
   - Add/remove fields, change types, update relationships
   - Hibernate will automatically update the database schema

2. **Configuration**
   - Profile: `dev` (or default)
   - `spring.jpa.hibernate.ddl-auto=update` (enabled)
   - `spring.flyway.enabled=false` (disabled)

3. **No Migrations**
   - Do NOT create Flyway migration files during development
   - Do NOT enable Flyway in development environment

### Example Development Session

```java
// 1. Modify entity
@Entity
public class Booking {
    @Column(name = "new_field")
    private String newField; // Hibernate will add this column automatically
}

// 2. Run application with dev profile
// 3. Hibernate automatically creates/updates schema
// 4. No manual SQL or migrations needed
```

## Pre-Production Workflow

### Step 1: Freeze Entity Model

- Review all entity changes
- Ensure model is stable and complete
- Document any breaking changes

### Step 2: Generate Baseline Schema

Generate the schema from your development database:

```bash
# Generate schema-only dump
pg_dump --schema-only \
        --no-owner \
        --no-acl \
        -h localhost \
        -U postgres \
        booking_management > schema.sql

# Review the generated schema
cat schema.sql
```

### Step 3: Create Baseline Migration

1. Open `src/main/resources/db/migration/V1__baseline.sql`
2. Replace the placeholder with the generated schema
3. Clean up any development-specific objects if needed
4. Ensure proper formatting and comments

Example:
```sql
-- V1__baseline.sql
-- Generated from dev database on 2024-01-15

CREATE TABLE bookings (
    id BIGSERIAL PRIMARY KEY,
    booking_reference VARCHAR(50) UNIQUE NOT NULL,
    -- ... rest of schema
);

CREATE INDEX idx_bookings_booking_reference ON bookings(booking_reference);
-- ... rest of indexes
```

### Step 4: Update Production Configuration

Verify `application-prod.yml`:
```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # Must be 'none'
  flyway:
    enabled: true     # Must be 'true'
    validate-on-migrate: true
```

### Step 5: Test Migration

1. Create a clean test database
2. Run application with `prod` profile
3. Verify Flyway creates all tables and indexes
4. Verify application starts successfully
5. Run integration tests

```bash
# Test on clean database
createdb booking_management_test
export SPRING_PROFILES_ACTIVE=prod
mvn spring-boot:run

# Check Flyway logs for successful migration
```

## Production Deployment

### Configuration

- Profile: `prod`
- Hibernate DDL: `none` (disabled)
- Flyway: `enabled: true`

### Migration Execution

Flyway will automatically:
1. Check migration status
2. Apply pending migrations
3. Validate migration checksums
4. Fail if validation errors detected

### Adding New Migrations (After Baseline)

After baseline, all schema changes must be Flyway migrations:

1. **Modify Entity** (in dev with Hibernate)
2. **Generate Schema Diff**
   ```bash
   # Compare old and new schema
   pg_dump --schema-only old_db > old_schema.sql
   pg_dump --schema-only new_db > new_schema.sql
   diff old_schema.sql new_schema.sql
   ```
3. **Create Migration**
   - Create `V2__add_new_table.sql` (or next version)
   - Include only the changes (ALTER, CREATE, etc.)
   - Test on staging database
4. **Deploy to Production**
   - Flyway will apply the new migration automatically

## Troubleshooting

### Issue: Flyway tries to run in development

**Solution**: Verify `spring.flyway.enabled=false` in `application-dev.yml`

### Issue: Hibernate modifies schema in production

**Solution**: Verify `spring.jpa.hibernate.ddl-auto=none` in `application-prod.yml`

### Issue: Migration fails in production

**Solution**: 
1. Check migration file syntax
2. Verify database permissions
3. Review Flyway logs
4. Test migration on staging first

## Best Practices

1. ✅ Always freeze entity model before creating baseline
2. ✅ Generate baseline from actual dev database (not from memory)
3. ✅ Test all migrations on staging before production
4. ✅ Use descriptive migration names (V2__add_user_table.sql)
5. ✅ Review generated SQL before committing
6. ❌ Never create migrations during development
7. ❌ Never enable Flyway in development
8. ❌ Never enable Hibernate DDL in production
9. ❌ Never mix Hibernate DDL and Flyway in same environment

