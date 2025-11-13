# Controller Agent Instructions

This agent specializes in creating and maintaining MVC controllers for the Spawn application.

## Controller Purpose

Controllers in the Spawn application are responsible for:
- Handling HTTP requests and rendering HTML views
- Processing form submissions using `@RequestParam`
- Delegating business logic to service layer
- Managing model attributes for views
- Implementing Post-Redirect-Get pattern after form submissions
- Error handling and displaying errors to users

## Controller Structure Pattern

### Standard Controller Template

```java
package dev.rebelcraft.ai.spawn.controller;

import dev.rebelcraft.ai.spawn.dto.ResourceRequest;
import dev.rebelcraft.ai.spawn.dto.ResourceResponse;
import dev.rebelcraft.ai.spawn.service.ResourceService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/resources")
public class ResourceController {

  private final ResourceService resourceService;

  public ResourceController(ResourceService resourceService) {
    this.resourceService = resourceService;
  }

  @GetMapping
  public String listResources(Model model) {
    List<ResourceResponse> resources = resourceService.getAllResources();
    model.addAttribute("resources", resources);
    return "resourcesListPage";
  }

  @GetMapping("/new")
  public String newResourceForm() {
    return "resourceFormPage";
  }

  @PostMapping
  public String createResource(@RequestParam String name,
                               @RequestParam String type,
                               @RequestParam(required = false) String description,
                               Model model) {
    try {
      ResourceRequest request = new ResourceRequest(name, type);
      request.setDescription(description);
      resourceService.createResource(request);
      return "redirect:/resources";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("name", name);
      model.addAttribute("type", type);
      model.addAttribute("description", description);
      return "resourceFormPage";
    }
  }

  @GetMapping("/{id}/edit")
  public String editResourceForm(@PathVariable Long id, Model model) {
    ResourceResponse resource = resourceService.getResourceById(id);
    model.addAttribute("resourceId", resource.getId().toString());
    model.addAttribute("name", resource.getName());
    model.addAttribute("type", resource.getType());
    model.addAttribute("description", resource.getDescription());
    return "resourceFormPage";
  }

  @PostMapping("/{id}")
  public String updateResource(@PathVariable Long id,
                               @RequestParam String name,
                               @RequestParam String type,
                               @RequestParam(required = false) String description,
                               Model model) {
    try {
      ResourceRequest request = new ResourceRequest(name, type);
      request.setDescription(description);
      resourceService.updateResource(id, request);
      return "redirect:/resources";
    } catch (Exception e) {
      model.addAttribute("error", e.getMessage());
      model.addAttribute("resourceId", id.toString());
      model.addAttribute("name", name);
      model.addAttribute("type", type);
      model.addAttribute("description", description);
      return "resourceFormPage";
    }
  }

  @PostMapping("/{id}/delete")
  public String deleteResource(@PathVariable Long id) {
    resourceService.deleteResource(id);
    return "redirect:/resources";
  }
}
```

## Key Annotations

### Class-Level Annotations

- `@Controller` - Marks the class as an MVC controller (returns view names)
- `@RequestMapping("/resources")` - Base path for all endpoints in this controller

### Method-Level Annotations

- `@GetMapping` - Handle HTTP GET requests (display pages)
- `@PostMapping` - Handle HTTP POST requests (process forms)
- `@GetMapping("/{id}/edit")` - Display edit form with path variable
- `@PostMapping("/{id}/delete")` - Delete operation via POST

### Parameter Annotations

- `@RequestParam` - Extract form field values from POST requests
- `@PathVariable` - Extract values from URI path (e.g., `/resources/{id}`)
- `Model` - Spring MVC model object for passing attributes to views

## Return Values and Redirects

Controllers return view names (Strings) instead of ResponseEntity:

| Operation | Return Value | Method |
|-----------|--------------|---------|
| Display list | View name | `return "resourcesListPage";` |
| Display form | View name | `return "resourceFormPage";` |
| Create (success) | Redirect | `return "redirect:/resources";` |
| Create (error) | View name | `return "resourceFormPage";` |
| Update (success) | Redirect | `return "redirect:/resources";` |
| Update (error) | View name | `return "resourceFormPage";` |
| Delete | Redirect | `return "redirect:/resources";` |

**Post-Redirect-Get Pattern**: Always redirect after successful POST operations to prevent form resubmission.

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

## Form Validation

- Use `@RequestParam` with `required` attribute for mandatory fields
- Validation happens in try-catch blocks in controller methods
- On validation error, add error message to model and return to form
- Pre-fill form fields with submitted values when showing errors

Example:
```java
@PostMapping
public String createResource(@RequestParam String name, 
                            @RequestParam String type,
                            Model model) {
    try {
        // Validation logic or service call
        resourceService.createResource(request);
        return "redirect:/resources";
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("name", name);
        model.addAttribute("type", type);
        return "resourceFormPage";
    }
}
```

## Exception Handling

Controllers catch exceptions in try-catch blocks and handle them locally:

- Service exceptions → Catch, add error to model, return form view
- `ResourceNotFoundException` → Let it bubble to `GlobalExceptionHandler`
- Form validation errors → Catch, add error message, return form with data

Controllers handle errors by adding them to the model for display in views.

## Controller Responsibilities

### ✅ Controllers SHOULD:
- Accept HTTP requests
- Extract form data using `@RequestParam`
- Call service methods
- Add attributes to Model for views
- Return view names or redirects
- Catch exceptions and display errors
- Use Post-Redirect-Get pattern

### ❌ Controllers SHOULD NOT:
- Contain business logic (belongs in services)
- Directly access repositories (use services)
- Return JSON responses (use view names instead)
- Perform database operations
- Contain complex logic

## Standard CRUD Endpoints

### Display List Page (GET)
```java
@GetMapping
public String listResources(Model model) {
    List<ResourceResponse> resources = resourceService.getAllResources();
    model.addAttribute("resources", resources);
    return "resourcesListPage";
}
```

### Display Create Form (GET)
```java
@GetMapping("/new")
public String newResourceForm() {
    return "resourceFormPage";
}
```

### Process Create Form (POST)
```java
@PostMapping
public String createResource(@RequestParam String name,
                            @RequestParam String type,
                            @RequestParam(required = false) String description,
                            Model model) {
    try {
        ResourceRequest request = new ResourceRequest(name, type);
        request.setDescription(description);
        resourceService.createResource(request);
        return "redirect:/resources";
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("name", name);
        model.addAttribute("type", type);
        model.addAttribute("description", description);
        return "resourceFormPage";
    }
}
```

### Display Edit Form (GET)
```java
@GetMapping("/{id}/edit")
public String editResourceForm(@PathVariable Long id, Model model) {
    ResourceResponse resource = resourceService.getResourceById(id);
    model.addAttribute("resourceId", resource.getId().toString());
    model.addAttribute("name", resource.getName());
    model.addAttribute("type", resource.getType());
    model.addAttribute("description", resource.getDescription());
    return "resourceFormPage";
}
```

### Process Update Form (POST)
```java
@PostMapping("/{id}")
public String updateResource(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String type,
                            @RequestParam(required = false) String description,
                            Model model) {
    try {
        ResourceRequest request = new ResourceRequest(name, type);
        request.setDescription(description);
        resourceService.updateResource(id, request);
        return "redirect:/resources";
    } catch (Exception e) {
        model.addAttribute("error", e.getMessage());
        model.addAttribute("resourceId", id.toString());
        model.addAttribute("name", name);
        model.addAttribute("type", type);
        model.addAttribute("description", description);
        return "resourceFormPage";
    }
}
```

### Delete Resource (POST)
```java
@PostMapping("/{id}/delete")
public String deleteResource(@PathVariable Long id) {
    resourceService.deleteResource(id);
    return "redirect:/resources";
}
```

## Relationship Endpoints

For managing many-to-many relationships (e.g., adding MCP servers to applications):

### Add Relationship (POST)
```java
@PostMapping("/{parentId}/children/{childId}")
public String addChild(@PathVariable Long parentId,
                      @PathVariable Long childId) {
    parentService.addChild(parentId, childId);
    return "redirect:/parents/" + parentId;
}
```

### Remove Relationship (POST)
```java
@PostMapping("/{parentId}/children/{childId}/remove")
public String removeChild(@PathVariable Long parentId,
                         @PathVariable Long childId) {
    parentService.removeChild(parentId, childId);
    return "redirect:/parents/" + parentId;
}
```

## Naming Conventions

- Controller class name: `{EntityName}Controller`
- Method names: Use clear, descriptive verbs
  - `listResources()`, `newResourceForm()`, `createResource()`, `editResourceForm()`, `updateResource()`, `deleteResource()`
  - For relationships: `addChild()`, `removeChild()`
- View names: lowercase with suffix
  - `resourcesListPage`, `resourceFormPage`, `resourceDetailPage`
- Request DTOs: `{EntityName}Request`
- Response DTOs: `{EntityName}Response`

## URL Design

Follow MVC conventions:
- Use plural nouns: `/models`, `/applications`
- Use IDs in path: `/models/{id}/edit`
- Use verb suffixes for actions: `/new`, `/edit`, `/delete`
- Nest related resources: `/applications/{applicationId}/mcp-servers/{mcpServerId}`
- Use POST for all mutations (create, update, delete)

## Example: Complete Controller

See existing controllers for reference:
- `ModelController` - Simple CRUD with forms
- `ApplicationController` - CRUD plus relationship management
- `McpServerController` - Simple CRUD with forms
- `IndexController` - Simple home page

## When Creating New Controllers

1. Create a new class in `src/main/java/com/teggr/spawn/controller/`
2. Add `@Controller` and `@RequestMapping` annotations
3. Inject required service using constructor injection
4. Implement CRUD endpoints following the standard pattern
5. Use POST for all form submissions
6. Extract form data with `@RequestParam`
7. Add Model attributes for views
8. Return view names (for display) or redirects (after POST)
9. Catch exceptions and add errors to model
10. Create corresponding view classes in `view/` package
11. Create corresponding integration tests (see unit-testing agent)
12. Ensure the controller only handles HTTP concerns, not business logic

## Code Quality Checklist

Before completing a controller:
- ✅ Constructor-based dependency injection used
- ✅ All parameters properly annotated (`@PathVariable`, `@RequestParam`)
- ✅ Correct HTTP methods used (GET for display, POST for mutations)
- ✅ View names or redirects returned (not ResponseEntity)
- ✅ Post-Redirect-Get pattern followed
- ✅ Exceptions caught and errors added to model
- ✅ No business logic in controller
- ✅ No direct repository access
- ✅ Follows naming conventions
- ✅ Consistent with existing controllers
- ✅ Integration tests created
- ✅ Corresponding view classes created
