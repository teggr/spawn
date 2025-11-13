# Spawn

Application for building AI capable applications on the fly with UI, models and mcp servers.

## Overview

Spawn is a Spring Boot application that enables you to configure, build, and deploy new AI applications built on the Spring Boot / Spring AI framework. The application provides a RESTful API to manage applications, AI models, and MCP (Model Context Protocol) servers.

## Main Workflow

1. **Create a new application** with a name (you can create many)
2. **Decide which model you want to use** (e.g., OpenAI)
3. **Choose a set of MCP servers** you want to bundle with the model
4. **Build and deploy** a new application based on the configuration

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- Maven

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6 or higher

### Build and Run

1. Clone the repository
2. Build the application:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

### H2 Console

Access the H2 database console at: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:spawndb`
- Username: `sa`
- Password: (empty)

## API Documentation

### Models API

#### Create a Model
```bash
POST /api/models
Content-Type: application/json

{
  "name": "GPT-4",
  "type": "OpenAI",
  "description": "OpenAI GPT-4 Model"
}
```

#### Get All Models
```bash
GET /api/models
```

#### Get Model by ID
```bash
GET /api/models/{id}
```

#### Update Model
```bash
PUT /api/models/{id}
Content-Type: application/json

{
  "name": "GPT-4",
  "type": "OpenAI",
  "description": "Updated description"
}
```

#### Delete Model
```bash
DELETE /api/models/{id}
```

### MCP Servers API

#### Create an MCP Server
```bash
POST /api/mcp-servers
Content-Type: application/json

{
  "name": "FileSystem MCP",
  "url": "http://localhost:8080/mcp/filesystem",
  "description": "MCP Server for file system operations"
}
```

#### Get All MCP Servers
```bash
GET /api/mcp-servers
```

#### Get MCP Server by ID
```bash
GET /api/mcp-servers/{id}
```

#### Update MCP Server
```bash
PUT /api/mcp-servers/{id}
Content-Type: application/json

{
  "name": "FileSystem MCP",
  "url": "http://localhost:8080/mcp/filesystem",
  "description": "Updated description"
}
```

#### Delete MCP Server
```bash
DELETE /api/mcp-servers/{id}
```

### Applications API

#### Create an Application
```bash
POST /api/applications
Content-Type: application/json

{
  "name": "My AI Application",
  "modelId": 1
}
```

#### Get All Applications
```bash
GET /api/applications
```

#### Get Application by ID
```bash
GET /api/applications/{id}
```

#### Update Application
```bash
PUT /api/applications/{id}
Content-Type: application/json

{
  "name": "Updated Application Name",
  "modelId": 1
}
```

#### Delete Application
```bash
DELETE /api/applications/{id}
```

#### Add MCP Server to Application
```bash
POST /api/applications/{applicationId}/mcp-servers/{mcpServerId}
```

#### Remove MCP Server from Application
```bash
DELETE /api/applications/{applicationId}/mcp-servers/{mcpServerId}
```

## Example Workflow

Here's a complete example of creating an AI application with a model and MCP servers:

```bash
# 1. Create a model
curl -X POST http://localhost:8080/api/models \
  -H "Content-Type: application/json" \
  -d '{
    "name": "GPT-4",
    "type": "OpenAI",
    "description": "OpenAI GPT-4 Model"
  }'

# Response: {"id":1,"name":"GPT-4","type":"OpenAI","description":"OpenAI GPT-4 Model"}

# 2. Create MCP servers
curl -X POST http://localhost:8080/api/mcp-servers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "FileSystem MCP",
    "url": "http://localhost:8080/mcp/filesystem",
    "description": "File system operations"
  }'

# Response: {"id":1,"name":"FileSystem MCP","url":"http://localhost:8080/mcp/filesystem","description":"File system operations"}

curl -X POST http://localhost:8080/api/mcp-servers \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Database MCP",
    "url": "http://localhost:8080/mcp/database",
    "description": "Database operations"
  }'

# Response: {"id":2,"name":"Database MCP","url":"http://localhost:8080/mcp/database","description":"Database operations"}

# 3. Create an application with the model
curl -X POST http://localhost:8080/api/applications \
  -H "Content-Type: application/json" \
  -d '{
    "name": "My AI Assistant",
    "modelId": 1
  }'

# Response: {"id":1,"name":"My AI Assistant","createdAt":"2025-11-12T23:49:43.334897","model":{...},"mcpServers":[]}

# 4. Add MCP servers to the application
curl -X POST http://localhost:8080/api/applications/1/mcp-servers/1

curl -X POST http://localhost:8080/api/applications/1/mcp-servers/2

# 5. Get the complete application configuration
curl http://localhost:8080/api/applications/1
```

## Testing

Run the tests:
```bash
mvn test
```

The test suite includes:
- Unit tests for all controllers
- Integration tests for the complete workflow
- End-to-end workflow test demonstrating the full application lifecycle

## Project Structure

```
src/
├── main/
│   ├── java/com/teggr/spawn/
│   │   ├── SpawnApplication.java          # Main application class
│   │   ├── controller/                    # REST controllers
│   │   │   ├── ApplicationController.java
│   │   │   ├── ModelController.java
│   │   │   ├── McpServerController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── dto/                           # Data Transfer Objects
│   │   │   ├── ApplicationRequest.java
│   │   │   ├── ApplicationResponse.java
│   │   │   ├── ModelRequest.java
│   │   │   ├── ModelResponse.java
│   │   │   ├── McpServerRequest.java
│   │   │   └── McpServerResponse.java
│   │   ├── model/                         # JPA entities
│   │   │   ├── Application.java
│   │   │   ├── Model.java
│   │   │   └── McpServer.java
│   │   ├── repository/                    # Spring Data repositories
│   │   │   ├── ApplicationRepository.java
│   │   │   ├── ModelRepository.java
│   │   │   └── McpServerRepository.java
│   │   └── service/                       # Business logic
│   │       ├── ApplicationService.java
│   │       ├── ModelService.java
│   │       ├── McpServerService.java
│   │       └── ResourceNotFoundException.java
│   └── resources/
│       └── application.properties         # Application configuration
└── test/
    └── java/com/teggr/spawn/              # Test classes
```

## Database Schema

The application uses an in-memory H2 database with the following entities:

- **applications**: Stores AI application configurations
- **models**: Stores AI model definitions (e.g., OpenAI, Claude)
- **mcp_servers**: Stores MCP server definitions
- **application_mcp_servers**: Junction table for many-to-many relationship

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
