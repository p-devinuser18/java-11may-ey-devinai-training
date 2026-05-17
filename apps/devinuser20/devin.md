# devinuser20 — scoped conventions

This folder is owned by **devinuser20**. Devin sessions for this participant
must work **only** inside this folder.

## Tech stack
- Java 17, Maven, Spring Boot 3.x
- Persistence: Spring Data JPA + H2 (in-memory)
- Tests: JUnit 5 + Mockito + MockMvc + AssertJ

## Branch convention
- Feature branches: devinuser20/<short-feature-name>
- PR base: devinuser20/integration (NOT main)

## Code conventions
- Constructor injection only
- jakarta.* imports (not javax.*)
- Records for DTOs, classes for JPA entities
- @RestControllerAdvice for cross-cutting exception handling
