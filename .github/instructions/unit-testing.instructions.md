# Unit Testing Agent Instructions

This agent specializes in creating and maintaining integration tests for the Spawn application.

## Test Framework and Tools

- **JUnit 5** - Testing framework
- **Spring Boot Test** - Spring testing support with `@SpringBootTest`
- **MockMvc** - Testing MVC controllers without starting a full HTTP server
- **H2 Database** - In-memory database automatically reset between tests

## Test Structure Pattern

All tests in this project are **integration tests** that:
1. Load the full Spring application context using `@SpringBootTest`
2. Use `@AutoConfigureMockMvc` to configure MockMvc
3. Test the complete request/response cycle through controllers
4. Verify HTML responses and redirects
5. Use the real H2 in-memory database (not mocked)

### Standard Test Class Template

```java
package dev.rebelcraft.ai.spawn.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class YourControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Test
  void shouldDoSomething() throws Exception {
    // Test implementation
  }
}
```

## Test Naming Conventions

- Test class names: `{ControllerName}IntegrationTest`
- Test method names: Use `should` prefix followed by description
  - `shouldCreateModel()`
  - `shouldGetAllModels()`
  - `shouldReturnNotFoundWhenModelDoesNotExist()`

## Testing CRUD Operations

### CREATE (POST) Tests

```java
@Test
void shouldCreateResource() throws Exception {
    mockMvc.perform(post("/resources")
            .param("name", "Test Name")
            .param("type", "Test Type")
            .param("description", "Test Description"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/resources"));
}

@Test
void shouldShowErrorWhenCreateFails() throws Exception {
    mockMvc.perform(post("/resources")
            .param("name", "")  // Invalid - empty name
            .param("type", "Test Type"))
            .andExpect(status().isOk())
            .andExpect(view().name("resourceFormPage"))
            .andExpect(model().attributeExists("error"));
}
```

### READ (GET) Tests

**Get List Page:**
```java
@Test
void shouldDisplayListPage() throws Exception {
    mockMvc.perform(get("/resources"))
            .andExpect(status().isOk())
            .andExpect(view().name("resourcesListPage"))
            .andExpect(model().attributeExists("resources"));
}
```

**Get Create Form:**
```java
@Test
void shouldDisplayCreateForm() throws Exception {
    mockMvc.perform(get("/resources/new"))
            .andExpect(status().isOk())
            .andExpect(view().name("resourceFormPage"));
}
```

**Get Edit Form:**
```java
@Test
void shouldDisplayEditForm() throws Exception {
    // First create a resource
    mockMvc.perform(post("/resources")
            .param("name", "Test")
            .param("type", "Type"));
    
    // Then display edit form
    mockMvc.perform(get("/resources/1/edit"))
            .andExpect(status().isOk())
            .andExpect(view().name("resourceFormPage"))
            .andExpect(model().attributeExists("resourceId"))
            .andExpect(model().attribute("name", "Test"));
}
```

### UPDATE (POST) Tests

```java
@Test
void shouldUpdateResource() throws Exception {
    // Create first
    mockMvc.perform(post("/resources")
            .param("name", "Original")
            .param("type", "Type"));
    
    // Then update
    mockMvc.perform(post("/resources/1")
            .param("name", "Updated")
            .param("type", "Type"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/resources"));
}
```

### DELETE (POST) Tests

```java
@Test
void shouldDeleteResource() throws Exception {
    // Create first
    mockMvc.perform(post("/resources")
            .param("name", "Test")
            .param("type", "Type"));
    
    // Then delete
    mockMvc.perform(post("/resources/1/delete"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/resources"));
}
```

## Error Case Testing

### Test Not Found Scenarios

```java
@Test
void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
    mockMvc.perform(get("/resources/999/edit"))
            .andExpect(status().isNotFound());
}
```

### Test Validation Errors

```java
@Test
void shouldShowErrorWhenNameIsBlank() throws Exception {
    mockMvc.perform(post("/resources")
            .param("name", "")
            .param("type", "Type"))
            .andExpect(status().isOk())
            .andExpect(view().name("resourceFormPage"))
            .andExpect(model().attributeExists("error"));
}
```

## Testing Relationships

For endpoints that manage relationships (e.g., adding MCP servers to applications):

```java
@Test
void shouldAddRelatedResource() throws Exception {
    // Create parent and child resources first
    mockMvc.perform(post("/parents")
            .param("name", "Parent"));
    
    mockMvc.perform(post("/children")
            .param("name", "Child"));
    
    // Then test the relationship endpoint
    mockMvc.perform(post("/parents/1/children/1"))
            .andExpect(status().is3xxRedirection());
    
    // Verify the relationship by viewing the detail page
    mockMvc.perform(get("/parents/1"))
            .andExpect(status().isOk())
            .andExpect(model().attributeExists("children"));
}
```

## Test Organization

- Group related tests in the same test class
- Each controller should have its own test class
- Keep tests independent - each test should work in isolation
- Don't depend on test execution order
- Clean up is automatic (database reset between tests)

## Best Practices

1. **Test Isolation**: Each test should be independent and not rely on other tests
2. **Arrange-Act-Assert**: Structure tests with clear setup, execution, and verification phases
3. **Meaningful Names**: Use descriptive test method names that explain what is being tested
4. **Test One Thing**: Each test should verify a single behavior or scenario
5. **Use Real Data**: Don't use mocked data when testing controllers - use real database
6. **Verify Redirects**: Check that POST operations redirect correctly
7. **Verify View Names**: Check that GET operations return correct view names
8. **Verify Model Attributes**: Check that required attributes are added to the model
9. **Test Error Cases**: Include tests for error scenarios (not found, validation errors, etc.)
10. **Follow Existing Patterns**: Match the style and structure of existing tests

## Testing HTMX Endpoints

### Test Fragment GET Endpoints

Fragment endpoints return partial HTML. Assert that the expected element ID is present and that the response does **not** contain a full HTML document:

```java
@Test
void shouldReturnDetailsFragment() throws Exception {
    // Set up data first
    Chat chat = createChatWithParticipants();

    mockMvc.perform(get("/chat/" + chat.getId() + "/details"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"chat-content\"")))
            .andExpect(content().string(containsString("id=\"messages-container\"")))
            .andExpect(content().string(not(containsString("<html"))));
}
```

Import `org.hamcrest.Matchers.containsString` and `org.hamcrest.Matchers.not` for content string assertions.

### Test HTMX-Aware POST Endpoints

Send the `HX-Request: true` header to simulate an HTMX request; omit it to simulate a plain form submission:

```java
@Test
void shouldReturnFragmentWhenPostIsViaHtmx() throws Exception {
    Chat chat = createChatWithParticipants();

    mockMvc.perform(post("/chat/" + chat.getId() + "/messages")
                    .header("HX-Request", "true")
                    .param("content", "Hello!"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("id=\"messages-container\"")))
            .andExpect(content().string(containsString("Hello!")))
            .andExpect(content().string(not(containsString("<html"))));
}

@Test
void shouldRedirectWhenPostIsNotViaHtmx() throws Exception {
    Chat chat = createChatWithParticipants();

    mockMvc.perform(post("/chat/" + chat.getId() + "/messages")
                    .param("content", "Hello!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/chat/" + chat.getId()));
}
```

### Test HTMX Attributes in Rendered Pages

Verify that HTMX script and attributes are present in full-page responses using content string assertions:

```java
@Test
void shouldIncludeHtmxScriptInPage() throws Exception {
    mockMvc.perform(get("/chat"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("htmx.org@1.9.10")));
}

@Test
void shouldIncludeHxBoostOnNavbarLinks() throws Exception {
    mockMvc.perform(get("/chat"))
            .andExpect(status().isOk())
            .andExpect(content().string(containsString("hx-boost=\"true\"")));
}
```

## Common Assertions

```java
// Status codes
.andExpect(status().isOk())                   // 200
.andExpect(status().is3xxRedirection())       // 3xx redirect
.andExpect(status().isNotFound())             // 404

// Redirects
.andExpect(redirectedUrl("/resources"))

// View names
.andExpect(view().name("resourceFormPage"))

// Model attributes
.andExpect(model().attributeExists("resources"))
.andExpect(model().attribute("name", "expected"))
.andExpect(model().attributeExists("error"))

// Content string assertions (use for fragment and HTMX attribute checks)
.andExpect(content().string(containsString("id=\"messages-container\"")))
.andExpect(content().string(not(containsString("<html"))))  // fragment has no page wrapper
.andExpect(content().string(containsString("hx-boost=\"true\"")))
```

## When Creating New Tests

1. Identify the controller and endpoints to test
2. Create a new test class following the naming convention
3. Add `@SpringBootTest` and `@AutoConfigureMockMvc` annotations
4. Inject `MockMvc`
5. Write tests for each endpoint covering:
   - Happy path (successful operations with redirects)
   - Form display (GET requests return correct views)
   - Error cases (validation failures, not found)
   - Edge cases specific to the business logic
6. Run the tests with `mvn test` to verify they pass
7. Ensure tests are independent and can run in any order
