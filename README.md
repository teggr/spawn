# Spawn

Application for building AI capable applications on the fly with UI, models and mcp servers.

## Overview

Spawn is a Spring Boot application that enables you to configure, build, and deploy new AI applications built on the Spring Boot / Spring AI framework. The application provides a web-based UI with server-side rendered HTML pages to manage applications, AI models, and MCP (Model Context Protocol) servers.

## Main Workflow

1. **Browse available models** - View the list of AI models loaded from CSV (e.g., OpenAI, Anthropic Claude, Azure OpenAI)
2. **Browse available MCP servers** - View the list of MCP servers loaded from CSV (e.g., GitHub, Notion, Stripe)
3. **Create a new application** with a name and select a model provider
4. **Add MCP servers** to your application from the available list
5. **Build and deploy** a new application based on the configuration

## Technology Stack

- Java 17
- Spring Boot 3.2.0
- Spring Data JPA
- H2 Database (in-memory)
- J2HTML 1.6.0 (server-side HTML rendering)
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

## Web UI

The application provides a web-based user interface with the following pages:

### Models (`/models`)
- **Read-only list** of AI model providers loaded from `src/main/resources/models/models.csv`
- Displays model capabilities: multimodality, tools/functions, streaming, retry, observability, etc.
- No create, edit, or delete operations - models are configuration-only

### MCP Servers (`/mcp-servers`)
- **Read-only list** of MCP servers loaded from `src/main/resources/mcp/mcp_servers.csv`
- Displays server icons and descriptions
- Includes 48+ servers from the GitHub MCP registry (GitHub, Notion, Stripe, etc.)
- No create, edit, or delete operations - servers are configuration-only

### Applications (`/applications`)
- **Full CRUD operations** for AI applications
- Create applications with a name and model provider
- View, edit, and delete applications
- Add/remove MCP servers from applications
- Each application stores:
  - Name
  - Model provider (reference to CSV)
  - Set of MCP server names (references to CSV)
  - Creation timestamp

### Agents (`/agents`)
- New domain: Agents represent tailored expertise for specific tasks.
- Each Agent stores:
  - `name` (required)
  - `description` (optional)
  - `systemPrompt` (long text, stored as CLOB)
  - `createdAt` timestamp
  - A per-agent persisted list of MCP server names (free-form strings) stored in `agent_mcp_servers` table
- Endpoints (server-rendered HTML):
  - `GET /agents` — List all agents
  - `GET /agents/new` — Show creation form (dropdown of known MCP servers + "Other" free-form add)
  - `POST /agents` — Create agent (redirect on success)
  - `GET /agents/{id}` — Detail page showing full system prompt and attached MCP names (unknown names highlighted with a warning badge)
  - `GET /agents/{id}/edit` — Edit form (pre-filled)
  - `POST /agents/{id}` — Update agent
  - `POST /agents/{id}/delete` — Delete agent
  - `POST /agents/{id}/mcp-servers/add` — Attach an MCP name to an agent
  - `POST /agents/{id}/mcp-servers/{mcpName}/remove` — Remove an MCP name from an agent
- MCP servers list used for dropdown is loaded from CSV (read-only). Agents accept free-form MCP names; UI highlights names that don't match any known MCP server.

## Data Architecture

### CSV-Based Configuration (Read-Only)

**Models** and **MCP Servers** are loaded from CSV files at application startup:

- **Models CSV** (`src/main/resources/models/models.csv`):
  - Columns: Provider, Multimodality, Tools/Functions, Streaming, Retry, Observability, Built-in JSON, Local, OpenAI API Compatible
  - Example: `OpenAI,"In: text, image, audio Out: text, audio",yes,yes,yes,yes,yes,no,yes`

- **MCP Servers CSV** (`src/main/resources/mcp/mcp_servers.csv`):
  - Columns: Name, Icon, Description
  - Example: `GitHub,https://avatars.githubusercontent.com/u/9919?v=4,"Official GitHub MCP Server..."`

### Database Entities (CRUD)

**Applications** are stored in the H2 database:
- Application name
- Model provider name (validated against CSV)
- Collection of MCP server names (validated against CSV)
- Creation timestamp

**Agents** are stored in the H2 database:
- Agent name
- Description
- System prompt
- Creation timestamp
- Collection of MCP server names (free-form, validated against CSV)

## Example Workflow

Using the web UI:

1. **View Available Models**
   - Navigate to `http://localhost:8080/models`
   - Browse the list of available AI model providers

2. **View Available MCP Servers**
   - Navigate to `http://localhost:8080/mcp-servers`
   - Browse the list of available MCP servers with descriptions

3. **Create an Application**
   - Navigate to `http://localhost:8080/applications`
   - Click "Create New Application"
   - Enter application name: "My AI Assistant"
   - Select model provider: "OpenAI"
   - Click "Save"

4. **Add MCP Servers**
   - Click "View" on your application
   - Select an MCP server from the dropdown (e.g., "GitHub")
   - Click "Add Server"
   - Repeat to add more servers

5. **View Application Configuration**
   - The application detail page shows:
     - Application details (ID, name, model provider, created date)
     - Associated MCP servers with icons and descriptions
     - Option to add/remove servers

6. **Create an Agent**
   - Navigate to `http://localhost:8080/agents`
   - Click "Create New Agent"
   - Enter agent name: "Code Reviewer"
   - Optionally, enter a description
   - Enter the system prompt
   - Select MCP servers from the dropdown
   - Click "Save"

7. **View Agent Details**
   - Click "View" on your agent
   - See the full system prompt and attached MCP servers
   - Option to edit or delete the agent

## Testing

Run the tests:
```bash
mvn test
```

The test suite includes:
- Controller integration tests for all domains
- Service tests for CSV loading
- End-to-end workflow test demonstrating the full application lifecycle
- All tests use MockMvc for controller testing

## Project Structure

```
src/
├── main/
│   ├── java/dev/rebelcraft/ai/spawn/
│   │   ├── SpawnApplication.java          # Main application class
│   │   ├── apps/                          # Applications domain
│   │   │   ├── Application.java           # JPA entity
│   │   │   ├── ApplicationController.java # Web controller
│   │   │   ├── ApplicationService.java    # Business logic
│   │   │   ├── ApplicationRepository.java # Spring Data repository
│   │   │   ├── ApplicationRequest.java    # DTO
│   │   │   ├── ApplicationResponse.java   # DTO
│   │   │   ├── ApplicationFormPage.java   # J2HTML view
│   │   │   ├── ApplicationDetailPage.java # J2HTML view
│   │   │   └── ApplicationsListPage.java  # J2HTML view
│   │   ├── models/                        # Models domain (read-only CSV)
│   │   │   ├── Model.java                 # Plain POJO
│   │   │   ├── ModelController.java       # Web controller
│   │   │   ├── ModelService.java          # CSV loader
│   │   │   ├── ModelResponse.java         # DTO
│   │   │   └── ModelsListPage.java        # J2HTML view
│   │   ├── mcp/                           # MCP Servers domain (read-only CSV)
│   │   │   ├── McpServer.java             # Plain POJO
│   │   │   ├── McpServerController.java   # Web controller
│   │   │   ├── McpServerService.java      # CSV loader
│   │   │   ├── McpServerResponse.java     # DTO
│   │   │   └── McpServersListPage.java    # J2HTML view
│   │   ├── agents/                        # Agents domain
│   │   │   ├── Agent.java                 # JPA entity
│   │   │   ├── AgentController.java       # Web controller
│   │   │   ├── AgentService.java          # Business logic
│   │   │   ├── AgentRepository.java       # Spring Data repository
│   │   │   ├── AgentRequest.java          # DTO
│   │   │   ├── AgentResponse.java         # DTO
│   │   │   ├── AgentFormPage.java         # J2HTML view
│   │   │   ├── AgentDetailPage.java       # J2HTML view
│   │   │   └── AgentsListPage.java        # J2HTML view
│   │   ├── web/                           # Cross-cutting web concerns
│   │   │   ├── IndexController.java       # Home page
│   │   │   └── view/                      # Shared view components
│   │   ├── utils/                         # Utilities
│   │   │   └── ResourceNotFoundException.java
│   │   └── config/                        # Configuration
│   └── resources/
│       ├── application.properties         # Application configuration
│       ├── models/
│       │   └── models.csv                 # AI model providers
│       └── mcp/
│           └── mcp_servers.csv            # MCP servers
└── test/
    └── java/dev/rebelcraft/ai/spawn/      # Test classes
```

## Database Schema

The application uses an in-memory H2 database with the following schema:

- **applications**: Stores AI application configurations
  - id, name, model_provider, created_at
- **application_mcp_servers**: Stores MCP server names for each application
  - application_id, mcp_server_name
- **agents**: Stores agent configurations
  - id, name, description, system_prompt, created_at
- **agent_mcp_servers**: Stores MCP server names for each agent
  - agent_id, mcp_server_name

**Note**: Models and MCP servers are NOT stored in the database. They are loaded from CSV files at startup.

## License

This project is licensed under the Apache License 2.0 - see the LICENSE file for details.
