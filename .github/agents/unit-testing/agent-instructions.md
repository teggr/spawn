# Unit Testing Agent Instructions

This agent specializes in creating and maintaining integration tests for the Spawn application.

## Test Framework and Tools

- **JUnit 5** - Testing framework
- **Spring Boot Test** - Spring testing support with `@SpringBootTest`
- **MockMvc** - Testing REST controllers without starting a full HTTP server
- **Jackson ObjectMapper** - JSON serialization/deserialization in tests
- **H2 Database** - In-memory database automatically reset between tests

## Test Structure Pattern

All tests in this project are **integration tests** that:
1. Load the full Spring application context using `@SpringBootTest`
2. Use `@AutoConfigureMockMvc` to configure MockMvc
3. Test the complete request/response cycle through controllers
4. Use the real H2 in-memory database (not mocked)

### Standard Test Class Template

```java
package com.teggr.spawn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class YourControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    RequestDto request = new RequestDto("name", "type");
    request.setDescription("description");

    mockMvc.perform(post("/api/resources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.name").value("name"))
            .andExpect(jsonPath("$.type").value("type"))
            .andExpect(jsonPath("$.description").value("description"));
}
```

### READ (GET) Tests

**Get All:**
```java
@Test
void shouldGetAllResources() throws Exception {
    mockMvc.perform(get("/api/resources"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON));
}
```

**Get By ID:**
```java
@Test
void shouldGetResourceById() throws Exception {
    // First create a resource
    RequestDto request = new RequestDto("name", "type");
    String response = mockMvc.perform(post("/api/resources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andReturn().getResponse().getContentAsString();
    
    ResponseDto created = objectMapper.readValue(response, ResponseDto.class);
    
    // Then get it by ID
    mockMvc.perform(get("/api/resources/" + created.getId()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(created.getId()))
            .andExpect(jsonPath("$.name").value("name"));
}
```

### UPDATE (PUT) Tests

```java
@Test
void shouldUpdateResource() throws Exception {
    // Create first
    RequestDto createRequest = new RequestDto("original", "type");
    String createResponse = mockMvc.perform(post("/api/resources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(createRequest)))
            .andReturn().getResponse().getContentAsString();
    
    ResponseDto created = objectMapper.readValue(createResponse, ResponseDto.class);
    
    // Then update
    RequestDto updateRequest = new RequestDto("updated", "type");
    mockMvc.perform(put("/api/resources/" + created.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateRequest)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("updated"));
}
```

### DELETE (DELETE) Tests

```java
@Test
void shouldDeleteResource() throws Exception {
    // Create first
    RequestDto request = new RequestDto("name", "type");
    String response = mockMvc.perform(post("/api/resources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andReturn().getResponse().getContentAsString();
    
    ResponseDto created = objectMapper.readValue(response, ResponseDto.class);
    
    // Then delete
    mockMvc.perform(delete("/api/resources/" + created.getId()))
            .andExpect(status().isNoContent());
    
    // Verify it's deleted
    mockMvc.perform(get("/api/resources/" + created.getId()))
            .andExpect(status().isNotFound());
}
```

## Error Case Testing

### Test Not Found Scenarios

```java
@Test
void shouldReturnNotFoundWhenResourceDoesNotExist() throws Exception {
    mockMvc.perform(get("/api/resources/999"))
            .andExpect(status().isNotFound());
}
```

### Test Validation Errors

```java
@Test
void shouldReturnBadRequestWhenNameIsBlank() throws Exception {
    RequestDto request = new RequestDto("", "type");
    
    mockMvc.perform(post("/api/resources")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
}
```

## Testing Relationships

For endpoints that manage relationships (e.g., adding MCP servers to applications):

```java
@Test
void shouldAddRelatedResource() throws Exception {
    // Create parent and child resources first
    // Then test the relationship endpoint
    mockMvc.perform(post("/api/parents/{parentId}/children/{childId}", 
            parentId, childId))
            .andExpect(status().isOk());
    
    // Verify the relationship
    mockMvc.perform(get("/api/parents/" + parentId))
            .andExpect(jsonPath("$.children[0].id").value(childId));
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
6. **Verify Status Codes**: Always check the HTTP status code is correct
7. **Verify Response Content**: Check that response JSON contains expected data
8. **Test Error Cases**: Include tests for error scenarios (not found, validation errors, etc.)
9. **Use JsonPath**: Use `jsonPath()` matchers to verify JSON response structure and values
10. **Follow Existing Patterns**: Match the style and structure of existing tests

## Common Assertions

```java
// Status codes
.andExpect(status().isOk())              // 200
.andExpect(status().isCreated())         // 201
.andExpect(status().isNoContent())       // 204
.andExpect(status().isBadRequest())      // 400
.andExpect(status().isNotFound())        // 404

// Content type
.andExpect(content().contentType(MediaType.APPLICATION_JSON))

// JSON path assertions
.andExpect(jsonPath("$.id").exists())
.andExpect(jsonPath("$.name").value("expected"))
.andExpect(jsonPath("$.items").isArray())
.andExpect(jsonPath("$.items[0].id").value(1))
```

## When Creating New Tests

1. Identify the controller and endpoints to test
2. Create a new test class following the naming convention
3. Add `@SpringBootTest` and `@AutoConfigureMockMvc` annotations
4. Inject `MockMvc` and `ObjectMapper`
5. Write tests for each endpoint covering:
   - Happy path (successful operations)
   - Error cases (not found, validation failures)
   - Edge cases specific to the business logic
6. Run the tests with `mvn test` to verify they pass
7. Ensure tests are independent and can run in any order
