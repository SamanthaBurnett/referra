### Monorepo

- Chose a monorepo structure to keep backend, frontend, documentation, and infrastructure together while allowing independent deployment.

### Build Tool

- Selected Maven to broaden experience beyond Gradle.

### Java Version

- Selected Java 21 LTS for long-term support and broad industry adoption.

### Database

- Selected PostgreSQL for exposure beyond MySQL.

### Temporary Startup Configuration

- Temporarily excluded datasource and JPA auto-configuration during initial project setup. These exclusions will be removed when database infrastructure is introduced.

### Role Assignment

- Roles are not automatically assigned during authentication. Instead, roles will be granted when users complete workflows such as creating a candidate profile or a referrer profile.
