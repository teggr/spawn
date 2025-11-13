# Controller Agent Instructions

This agent specializes in creating and maintaining REST controllers for the Spawn application.

## Controller Purpose

Controllers in the Spawn application are responsible for:
- Handling HTTP requests and responses
- Request validation using Jakarta Bean Validation
- Delegating business logic to service layer
- Returning appropriate HTTP status codes
- Mapping between DTOs and service responses

## Controller Structure Pattern

### Standard Controller Template

```java
package com.teggr.spawn.controller;

import com.teggr.spawn.dto.ResourceRequest;
import com.teggr.spawn.dto.ResourceResponse;
import com.teggr.spawn.service.ResourceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/resources")
public class ResourceController {

    private final ResourceService resourceService;

    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping
    public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
        ResourceResponse response = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ResourceResponse>> getAllResources() {
        List<ResourceResponse> resources = resourceService.getAllResources();
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Long id) {
        ResourceResponse response = resourceService.getResourceById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse response = resourceService.updateResource(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
```

## Key Annotations

### Class-Level Annotations

- `@RestController` - Combines `@Controller` and `@ResponseBody`, returns data directly as JSON
- `@RequestMapping("/api/resources")` - Base path for all endpoints in this controller

### Method-Level Annotations

- `@PostMapping` - Handle HTTP POST requests (create)
- `@GetMapping` - Handle HTTP GET requests (read)
- `@PutMapping` - Handle HTTP UPDATE requests (update)
- `@DeleteMapping` - Handle HTTP DELETE requests (delete)
- `@PatchMapping` - Handle HTTP PATCH requests (partial update) - rarely used in this project

### Parameter Annotations

- `@RequestBody` - Bind request body JSON to Java object
- `@Valid` - Trigger validation on request DTO
- `@PathVariable` - Extract values from URI path (e.g., `/api/resources/{id}`)
- `@RequestParam` - Extract query parameters (e.g., `/api/resources?name=value`)

## HTTP Status Codes

Use appropriate status codes for each operation:

| Operation | Success Status | Method |
|-----------|---------------|---------|
| Create (POST) | 201 Created | `ResponseEntity.status(HttpStatus.CREATED).body(response)` |
| Read (GET) | 200 OK | `ResponseEntity.ok(response)` |
| Update (PUT) | 200 OK | `ResponseEntity.ok(response)` |
| Delete (DELETE) | 204 No Content | `ResponseEntity.noContent().build()` |
| Not Found | 404 Not Found | Handled by `GlobalExceptionHandler` |
| Validation Error | 400 Bad Request | Handled by Spring automatically with `@Valid` |

## Dependency Injection

**Always use constructor-based dependency injection:**

```java
private final ResourceService resourceService;

public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
}
```

**Do NOT use field injection:**
```java
@Autowired  // ❌ Don't do this
private ResourceService resourceService;
```

## Request Validation

- Use `@Valid` annotation on `@RequestBody` parameters
- Validation rules are defined in DTO classes using Jakarta Bean Validation
- Spring automatically returns 400 Bad Request for validation failures
- `GlobalExceptionHandler` formats validation error responses

Example:
```java
@PostMapping
public ResponseEntity<ResourceResponse> createResource(
        @Valid @RequestBody ResourceRequest request) {
    // Spring validates request automatically before this code runs
    ResourceResponse response = resourceService.createResource(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

## Exception Handling

Controllers should **not** handle exceptions directly. All exception handling is centralized in `GlobalExceptionHandler`:

- `ResourceNotFoundException` → 404 Not Found response
- Validation errors → 400 Bad Request with error details
- Other exceptions → 500 Internal Server Error

Controllers just let exceptions bubble up - the handler will catch them.

## Controller Responsibilities

### ✅ Controllers SHOULD:
- Accept HTTP requests
- Validate input using `@Valid`
- Call service methods
- Return appropriate ResponseEntity with status codes
- Map path variables and request parameters
- Use DTOs for request/response

### ❌ Controllers SHOULD NOT:
- Contain business logic (belongs in services)
- Directly access repositories (use services)
- Handle exceptions (use GlobalExceptionHandler)
- Perform database operations
- Transform domain objects (use DTOs)
- Contain complex logic

## Standard CRUD Endpoints

### Create Resource (POST)
```java
@PostMapping
public ResponseEntity<ResourceResponse> createResource(@Valid @RequestBody ResourceRequest request) {
    ResourceResponse response = resourceService.createResource(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
}
```

### Get All Resources (GET)
```java
@GetMapping
public ResponseEntity<List<ResourceResponse>> getAllResources() {
    List<ResourceResponse> resources = resourceService.getAllResources();
    return ResponseEntity.ok(resources);
}
```

### Get Resource by ID (GET)
```java
@GetMapping("/{id}")
public ResponseEntity<ResourceResponse> getResourceById(@PathVariable Long id) {
    ResourceResponse response = resourceService.getResourceById(id);
    return ResponseEntity.ok(response);
}
```

### Update Resource (PUT)
```java
@PutMapping("/{id}")
public ResponseEntity<ResourceResponse> updateResource(
        @PathVariable Long id,
        @Valid @RequestBody ResourceRequest request) {
    ResourceResponse response = resourceService.updateResource(id, request);
    return ResponseEntity.ok(response);
}
```

### Delete Resource (DELETE)
```java
@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
    resourceService.deleteResource(id);
    return ResponseEntity.noContent().build();
}
```

## Relationship Endpoints

For managing many-to-many relationships (e.g., adding MCP servers to applications):

### Add Relationship
```java
@PostMapping("/{parentId}/children/{childId}")
public ResponseEntity<ParentResponse> addChild(
        @PathVariable Long parentId,
        @PathVariable Long childId) {
    ParentResponse response = parentService.addChild(parentId, childId);
    return ResponseEntity.ok(response);
}
```

### Remove Relationship
```java
@DeleteMapping("/{parentId}/children/{childId}")
public ResponseEntity<ParentResponse> removeChild(
        @PathVariable Long parentId,
        @PathVariable Long childId) {
    ParentResponse response = parentService.removeChild(parentId, childId);
    return ResponseEntity.ok(response);
}
```

## Naming Conventions

- Controller class name: `{EntityName}Controller`
- Method names: Use clear, descriptive verbs
  - `createResource()`, `getResourceById()`, `updateResource()`, `deleteResource()`
  - For relationships: `addChild()`, `removeChild()`
- Request DTOs: `{EntityName}Request`
- Response DTOs: `{EntityName}Response`

## API URL Design

Follow RESTful conventions:
- Use plural nouns: `/api/models`, `/api/applications`
- Use IDs in path: `/api/models/{id}`
- Nest related resources: `/api/applications/{applicationId}/mcp-servers/{mcpServerId}`
- Use HTTP verbs correctly (POST=create, GET=read, PUT=update, DELETE=delete)

## Example: Complete Controller

See existing controllers for reference:
- `ModelController` - Simple CRUD operations
- `ApplicationController` - CRUD plus relationship management
- `McpServerController` - Simple CRUD operations

## When Creating New Controllers

1. Create a new class in `src/main/java/com/teggr/spawn/controller/`
2. Add `@RestController` and `@RequestMapping` annotations
3. Inject required service using constructor injection
4. Implement CRUD endpoints following the standard pattern
5. Use appropriate HTTP methods and status codes
6. Validate input with `@Valid` on request DTOs
7. Return ResponseEntity with proper status codes
8. Create corresponding integration tests (see unit-testing agent)
9. Ensure the controller only handles HTTP concerns, not business logic

## Code Quality Checklist

Before completing a controller:
- ✅ Constructor-based dependency injection used
- ✅ All parameters properly annotated (`@PathVariable`, `@RequestBody`, `@Valid`)
- ✅ Correct HTTP methods used (POST, GET, PUT, DELETE)
- ✅ Appropriate status codes returned
- ✅ No business logic in controller
- ✅ No direct repository access
- ✅ Follows naming conventions
- ✅ Consistent with existing controllers
- ✅ Integration tests created
