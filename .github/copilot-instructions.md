# Spawn - GitHub Copilot Instructions

## Project Overview

Spawn is a Spring Boot application that enables configuration, building, and deployment of new AI applications built on the Spring Boot / Spring AI framework. The application provides a RESTful API to manage applications, AI models, and MCP (Model Context Protocol) servers.

## Technology Stack

- **Java 17** - Primary programming language
- **Spring Boot 3.2.0** - Application framework
- **Spring Data JPA** - Data persistence layer
- **H2 Database** - In-memory database for development
- **Maven** - Build and dependency management
- **JUnit 5** - Testing framework

## Architecture and Project Structure

The project follows a layered architecture with clear separation of concerns:

```
src/main/java/com/teggr/spawn/
├── SpawnApplication.java          # Main Spring Boot application
├── controller/                    # REST API controllers (see controller agent)
├── dto/                           # Data Transfer Objects for API requests/responses
├── model/                         # JPA entities for database persistence
├── repository/                    # Spring Data JPA repositories
└── service/                       # Business logic layer
```

### Layer Responsibilities

- **Controllers**: Handle HTTP requests, validation, and response formatting
- **Services**: Contain business logic and orchestrate repository operations
- **Repositories**: Provide data access using Spring Data JPA
- **Models (Entities)**: JPA entities representing database tables
- **DTOs**: Transfer data between client and server, separate from entities

## Core Domain Concepts

1. **Models**: Represent AI models (e.g., OpenAI GPT-4, Claude) that can be used in applications
2. **MCP Servers**: Model Context Protocol servers that provide additional capabilities (e.g., file system access, database operations)
3. **Applications**: AI application configurations that combine a model with zero or more MCP servers

### Entity Relationships

- Application *many-to-one* Model (each application uses one model)
- Application *many-to-many* McpServer (applications can have multiple MCP servers)

## API Design Patterns

All REST endpoints follow consistent patterns:

- `POST /api/{resource}` - Create new resource (returns 201 Created)
- `GET /api/{resource}` - List all resources (returns 200 OK)
- `GET /api/{resource}/{id}` - Get single resource by ID (returns 200 OK or 404 Not Found)
- `PUT /api/{resource}/{id}` - Update resource (returns 200 OK or 404 Not Found)
- `DELETE /api/{resource}/{id}` - Delete resource (returns 204 No Content or 404 Not Found)

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
- Return appropriate HTTP status codes (404 for not found, 400 for validation errors, etc.)

### Validation

- Use Jakarta Bean Validation annotations (`@Valid`, `@NotBlank`, etc.)
- Validate at the controller layer using `@Valid` on request DTOs
- Return meaningful error messages to clients

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

- All tests are integration tests using `@SpringBootTest`
- Tests use `MockMvc` for testing REST endpoints
- Database is automatically reset between tests
- See unit-testing agent for detailed testing guidance

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
4. Create DTOs for API requests/responses in `dto/` package
5. Implement REST endpoints in `controller/` package
6. Write integration tests in `src/test/java/`

## Important Notes for Code Generation

- Always follow the existing code structure and patterns
- Use the same package structure as existing code
- Match the coding style of surrounding code
- Ensure all new endpoints have corresponding tests
- Use appropriate HTTP status codes and error handling
- Validate input at the controller layer
- Keep business logic in the service layer, not in controllers
