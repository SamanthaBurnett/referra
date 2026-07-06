# Changelog

## 7 - Role Onboarding
### Updates:
- Added role onboarding endpoint for authenticated users.
- Added `UserRoleService` to manage user role retrieval and assignment.
- Added support for retrieving assigned roles through `/api/users/me`.
- Restricted MVP onboarding to a single user-selected role.
-  Refactored `CurrentUserService` to delegate role management to `UserRoleService`.

## 6 - User Roles
### Updates:
- Added user role enum for `CANDIDATE`, `REFERRER`, and `ADMIN`.
- Added user role entity mapped to the `user_roles` table.
- Added repository support for user role lookup.
- Updated current user response to include roles.
- Added default `CANDIDATE` role provisioning for new users.

## 5 - User Provisioning
### Updates:
- Added `User` entity
- Added `UserStatus` enum
- Added `UserRepository`
- Added `CurrentUserService`

## 4 - Add Spring Security JWT Validation
### Updates:
- Added cognito config
- Added security config

## 3 - Initial Flyway Migration
### Updates:
- Added versioned db schema using Flyaway
- Successfully applied the initial schema migration.

## 2 - Local Database Setup
### Updates:
- Configured PostgreSQL using Docker Compose.
- Connected Spring Boot to the local PostgreSQL instance.
- Removed temporary datasource auto-configuration exclusions.
- Verified backend starts successfully with the configured database.

## 1 - Project Setup
### Updates:
- Initialized backend
- Initialized frontend
- Added temporary datasource and JPA auto-configuration exclusions until database setup is implemented.
- Verified both backend and frontend applications start successfully.