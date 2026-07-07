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

### Single Role Onboarding (MVP)

Although the database supports multiple roles per user through the `user_roles` table, Referra's MVP restricts users to selecting a single role during onboarding. This keeps onboarding, permissions, and the overall user experience simple while preserving the ability to support multiple roles in the future without requiring a database redesign.

### Candidate Name Fields

Candidate profiles store `first_name` and `last_name` separately instead of a single `full_name`. This supports cleaner display formatting, personalization, sorting, and future search behavior without needing to parse a full name string later.

### Internal Company Support

Company creation is internal for now and is not exposed through a public controller.

Referrer profile creation will use `CompanyService` to find or create companies as needed. This keeps the user workflow centered on creating a referrer profile rather than managing company records directly.

### Company Metadata Ownership

For MVP, referrers provide only `companyName` when creating or updating a referrer profile.

Company records are shared reference data, so user-submitted metadata such as domain or website URL is not accepted through the referrer profile flow. This avoids conflicting company information from different users.

Company metadata can be enriched later through admin tools or a controlled data source.
