# Flyway Migrations

## Schema Management Strategy

### Development Environment
- **Hibernate DDL**: Enabled (`ddl-auto=update`)
- **Flyway**: Disabled
- **Rule**: Hibernate automatically creates and evolves the schema based on entity models

### Production Environment
- **Hibernate DDL**: Disabled (`ddl-auto=none`)
- **Flyway**: Enabled
- **Rule**: Flyway manages all schema changes through migration scripts

### ⚠️ Critical Rule
**Never mix Hibernate DDL and Flyway in the same environment.**

## Pre-Production Workflow

Before deploying to production:

1. **Freeze Entity Model**
   - Ensure all entity changes are complete
   - Review and finalize the data model

2. **Generate Baseline Schema**
   ```bash
   pg_dump --schema-only --no-owner --no-acl \
     -h localhost -U postgres booking_management > schema.sql
   ```

3. **Create Baseline Migration**
   - Copy the generated schema into `V1__baseline.sql`
   - Remove any development-specific objects if needed
   - Review and test the migration script

4. **Update Production Configuration**
   - Verify `spring.jpa.hibernate.ddl-auto=none` in `application-prod.yml`
   - Verify `spring.flyway.enabled=true` in `application-prod.yml`
   - Verify `spring.flyway.validate-on-migrate=true`

5. **Test Migration**
   - Test the migration on a staging database
   - Verify all tables, indexes, and constraints are created correctly

## Migration Naming Convention

- `V1__baseline.sql` - Initial schema baseline
- `V2__description.sql` - Subsequent migrations
- Use descriptive names for migrations after baseline

## Important Notes

- Do NOT create Flyway migrations during development
- Do NOT enable Flyway in development environment
- Always generate baseline from actual dev database schema
- Review and test all migrations before production deployment

