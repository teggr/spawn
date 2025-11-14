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

The project follows a layered architecture with clear separation of concerns. Recently the code has been reorganized so domain-specific classes live in their own packages (`apps`, `mcp`, `models`) while non-domain web and utility code lives under `web` and `utils`.

```
src/main/java/dev/rebelcraft/ai/spawn/
├── SpawnApplication.java          # Main Spring Boot application
├── apps/                          # Domain: applications (controllers, services, repos, dto, views)
├── mcp/                           # Domain: MCP servers (controllers, services, repos, dto, views)
├── models/                        # Domain: models (controllers, services, dto, views - READ-ONLY from CSV)
├── web/                           # Non-domain web concerns (IndexController, GlobalExceptionHandler, site-wide controllers)
├── utils/                          # Utilities (Docker integration helpers, templates, common helpers)
├── config/                        # Configuration classes (Docker, View)
└── docker/                        # Docker integration (DockerTemplate) - may also be referenced from utils
```

### Layer Responsibilities

- **Domain packages (apps / mcp / models)**: Each domain package contains its own controllers, services, repositories, DTOs, and view classes related to that domain. Keeping domain code together makes it easier to reason about features and tests. **Note**: The `models` domain is read-only and loads data from CSV, so it has no repository or entity class.
- **Web**: Cross-cutting web concerns that are not specific to a single domain (index, global exception handling, site-wide controllers) live in `web`.
- **Utils**: Shared utilities, helper classes, and Docker helper wrappers that are reused across domains live in `utils`.
- **Views**: J2HTML-based server-side rendered HTML pages may be colocated inside each domain package (e.g., `apps.view`) or kept in a shared `view` package if pages are shared across domains.
- **Services**: Contain business logic and orchestrate repository operations and should remain focused and domain-local when possible. For read-only domains like models, services load data from external sources (e.g., CSV files).
- **Repositories**: Provide data access using Spring Data JPA and live inside the corresponding domain package. Not applicable for read-only CSV-based domains.
- **Config**: Configuration beans for Docker and view resolution.

## Core Domain Concepts

1. **Models**: Represent AI model providers (e.g., OpenAI, Anthropic Claude, Azure OpenAI) loaded from a static CSV file (`src/main/resources/models/models.csv`). Models are **read-only** and include information about capabilities like multimodality, tools/functions support, streaming, retry, observability, built-in JSON, local deployment, and OpenAI API compatibility.
2. **MCP Servers**: Model Context Protocol servers that provide additional capabilities (e.g., file system access, database operations)
3. **Applications**: AI application configurations that combine a model provider with zero or more MCP servers

### Entity Relationships

- Application references Model by provider name (String) - not a database relationship
- Application *many-to-many* McpServer (applications can have multiple MCP servers)

## API Design Patterns

The application uses server-side rendered HTML with Spring MVC patterns:

### Standard CRUD Resources (MCP Servers, Applications)
- `GET /{resource}` - Display list page (returns HTML view)
- `GET /{resource}/new` - Display creation form (returns HTML view)
- `POST /{resource}` - Process form submission to create resource (redirect on success)
- `GET /{resource}/{id}/edit` - Display edit form (returns HTML view)
- `POST /{resource}/{id}` - Process form submission to update resource (redirect on success)
- `POST /{resource}/{id}/delete` - Delete resource (redirect on success)

### Read-Only Resources (Models)
- `GET /models` - Display list page with models loaded from CSV (read-only, no create/edit/delete operations)

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

## Database

- **H2 in-memory database** is used for development and testing
- Database is automatically created on startup
- Database stores: Applications, MCP Servers, and their relationships
- **Models are NOT stored in the database** - they are loaded from `src/main/resources/models/models.csv`
- Access H2 console at `http://localhost:8080/h2-console`
  - JDBC URL: `jdbc:h2:mem:spawndb`
  - Username: `sa`
  - Password: (empty)

## Models Domain (CSV-Based, Read-Only)

The models domain has a unique architecture compared to other domains:

- **Data Source**: Models are loaded from `src/main/resources/models/models.csv` on application startup
- **Read-Only**: No create, update, or delete operations - models can only be listed
- **No Database**: Model data is not persisted in the database
- **CSV Structure**: Provider, Multimodality, Tools/Functions, Streaming, Retry, Observability, Built-in JSON, Local, OpenAI API Compatible
- **Model Class**: Plain Java POJO (not a JPA entity)
- **No Repository**: ModelService reads directly from CSV file using ClassPathResource
- **Controller**: Only has `GET /models` endpoint to list all models
- **View**: ModelsListPage displays all model information in a read-only table
- **Application Integration**: Applications reference models by provider name (String field), not by database ID

### Working with Models

When creating or editing applications:
- The form displays a dropdown of all available model providers from the CSV
- Applications store the provider name as a String field (`model_provider`)
- ApplicationService validates that the selected provider exists in the CSV
- ApplicationResponse includes the full ModelResponse object by looking up the provider name

## Development Workflow

1. Create or modify entities and corresponding controllers, services, repositories, DTOs and views inside the appropriate domain package: `apps/`, `mcp/`, or `models/`.
   - **Exception**: For `models/`, remember it's read-only and CSV-based with no repository or entity.
2. Place cross-cutting web controllers and site-wide handlers in `web/` (e.g. `IndexController`, `GlobalExceptionHandler`).
3. Add shared utilities and Docker helpers in `utils/` (or `docker/` if you prefer a dedicated docker package).
4. Keep business logic in the service layer inside the domain packages.
5. Use J2HTML to create view classes; prefer colocating views with their domain for clarity.
6. Write integration tests in `src/test/java/` mirroring the domain package layout.

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
