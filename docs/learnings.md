## Milestone 1 - Project Setup

### Flyway - Versioned Database Schema

Flyway manages database changes as ordered, versioned SQL migrations. Each migration is applied exactly once and recorded in the `flyway_schema_history` table.

### Migration Immutability

Once a migration has been committed or applied to a shared database, it should be treated as immutable. Future schema changes should be introduced through new migrations rather than modifying existing ones.

## Milestone 2 - Authentication

### Cognito User Pools vs Identity Pools

A Cognito User Pool is responsible for authenticating users and issuing JWTs.

An Identity Pool provides temporary AWS credentials so authenticated users can directly access AWS services such as S3 or DynamoDB.

Referra currently uses a User Pool only because the backend handles AWS access on behalf of users.

---

### Cognito Subject (`sub`)

The `sub` claim is the stable identifier for a Cognito user.

Unlike an email address, the `sub` never changes, making it the appropriate key for linking a Cognito account to a local Referra user record.

---

### Spring Security OAuth2 Resource Server

Spring Security can validate Cognito-issued JWTs by configuring the Cognito issuer URI.

The backend only trusts requests that contain a valid JWT signed by Cognito.
