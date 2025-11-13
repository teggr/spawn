# Spawn - GitHub Copilot Instructions

## Project Overview

Spawn is a Spring Boot application that enables configuration, building, and deployment of new AI applications built on the Spring Boot / Spring AI framework. The application provides a web-based UI with server-side rendered HTML pages to manage applications, AI models, and MCP (Model Context Protocol) servers. It also includes Docker integration for container management.

## Technology Stack

- **Java 17** - Primary programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence layer
- **H2 Database** - In-memory database for development
- **J2HTML 1.6.0** - Java HTML builder for server-side rendering
- **Docker Java 3.3.4** - Docker client integration
- **Maven** - Build and dependency management
- **JUnit 5** - Testing framework

## Architecture and Project Structure

The project follows a layered architecture with clear separation of concerns:

```
src/main/java/com/teggr/spawn/
├── SpawnApplication.java          # Main Spring Boot application
├── controller/                    # MVC controllers (see controller agent)
├── view/                          # J2HTML view classes for server-side rendering
├── dto/                           # Data Transfer Objects for internal data passing
├── model/                         # JPA entities for database persistence
├── repository/                    # Spring Data JPA repositories
├── service/                       # Business logic layer
├── config/                        # Configuration classes (Docker, View)
└── docker/                        # Docker integration (DockerTemplate)
```

### Layer Responsibilities

- **Controllers**: Handle HTTP requests, render views, process form submissions
- **Views**: J2HTML-based server-side rendered HTML pages
- **Services**: Contain business logic and orchestrate repository operations
- **Repositories**: Provide data access using Spring Data JPA
- **Models (Entities)**: JPA entities representing database tables
- **DTOs**: Transfer data internally (still used by services)
- **Config**: Configuration beans for Docker and view resolution
- **Docker**: Docker client integration for container management

## Core Domain Concepts

1. **Models**: Represent AI models (e.g., OpenAI GPT-4, Claude) that can be used in applications
2. **MCP Servers**: Model Context Protocol servers that provide additional capabilities (e.g., file system access, database operations)
3. **Applications**: AI application configurations that combine a model with zero or more MCP servers

### Entity Relationships

- Application *many-to-one* Model (each application uses one model)
- Application *many-to-many* McpServer (applications can have multiple MCP servers)

## API Design Patterns

The application uses server-side rendered HTML with Spring MVC patterns:

- `GET /{resource}` - Display list page (returns HTML view)
- `GET /{resource}/new` - Display creation form (returns HTML view)
- `POST /{resource}` - Process form submission to create resource (redirect on success)
- `GET /{resource}/{id}/edit` - Display edit form (returns HTML view)
- `POST /{resource}/{id}` - Process form submission to update resource (redirect on success)
- `POST /{resource}/{id}/delete` - Delete resource (redirect on success)

All endpoints return HTML views rendered using J2HTML, not JSON responses.

## Coding Standards

### General Conventions

- Use constructor-based dependency injection (preferred over field injection)
- Follow Spring Boot best practices and conventions
- Use meaningful variable and method names
- Keep methods focused and single-purpose
- Use Java 17 features where appropriate

### Error Handling

- Use `ResourceNotFoundException` for entities not found
- `GlobalExceptionHandler` provides centralized exception handling
- Controllers catch exceptions and add error messages to the view model
- Return to the form page with error messages displayed to the user

### Form Validation

- Use `@RequestParam` with `required` attribute for form fields
- Validate at the controller layer before calling service methods
- Display validation errors in the view using Bootstrap alert components
- Return to the form with pre-filled data when validation fails

## Building and Testing

### Build Commands

```bash
mvn clean install    # Full build with tests
mvn clean package    # Package without tests
mvn spring-boot:run  # Run the application
```

### Running Tests

```bash
mvn test                           # Run all tests
mvn test -Dtest=ClassName          # Run specific test class
mvn test -Dtest=ClassName#method   # Run specific test method
```

### Test Structure

- Tests use `@SpringBootTest` with `@AutoConfigureMockMvc` for integration testing
- Tests use `MockMvc` for testing MVC controllers
- Database is automatically reset between tests
- Tests verify HTML responses and page rendering
- See unit-testing agent for detailed testing guidance

## Docker Integration

- **DockerTemplate** class provides generic Docker operations
- Configuration via `DockerConfiguration` and `DockerConfigurationProperties`
- Operations: list containers, run containers, stop containers, remove containers
- Configurable Docker host, TLS settings, and timeouts
- Used for managing containerized AI applications

## Database

- **H2 in-memory database** is used for development and testing
- Database is automatically created on startup
- Access H2 console at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:spawndb`
  - Username: `sa`
  - Password: (empty)

## Development Workflow

1. Create or modify entities in `model/` package
2. Create or update repositories in `repository/` package
3. Implement business logic in `service/` package
4. Create DTOs for internal data passing in `dto/` package (if needed)
5. Create view classes in `view/` package using J2HTML
6. Implement MVC controllers in `controller/` package
7. Write integration tests in `src/test/java/`

## Important Notes for Code Generation

- Always follow the existing code structure and patterns
- Use the same package structure as existing code
- Match the coding style of surrounding code
- Ensure all new endpoints have corresponding tests
- Controllers return view names, not JSON responses
- Use J2HTML for building HTML in view classes
- Forms submit via POST with `@RequestParam` for form fields
- Use redirects after successful POST operations (Post-Redirect-Get pattern)
- Keep business logic in the service layer, not in controllers
