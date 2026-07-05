## Flyway

### Versioned Database Schema

Flyway manages database changes as ordered, versioned SQL migrations. Each migration is applied exactly once and recorded in the `flyway_schema_history` table.

### Migration Immutability

Once a migration has been committed or applied to a shared database, it should be treated as immutable. Future schema changes should be introduced through new migrations rather than modifying existing ones.