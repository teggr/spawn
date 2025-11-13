package com.teggr.spawn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teggr.spawn.dto.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test demonstrating the complete workflow:
 * 1. Create models (OpenAI)
 * 2. Create MCP servers
 * 3. Create multiple applications
 * 4. Assign a model to an application
 * 5. Add MCP servers to applications
 */
@SpringBootTest
@AutoConfigureMockMvc
class WorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCompleteFullWorkflow() throws Exception {
        // Step 1: Create a model (OpenAI)
        ModelRequest modelRequest = new ModelRequest("GPT-4", "OpenAI");
        modelRequest.setDescription("OpenAI GPT-4 Model for advanced AI tasks");

        MvcResult modelResult = mockMvc.perform(post("/api/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(modelRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("GPT-4"))
                .andExpect(jsonPath("$.type").value("OpenAI"))
                .andReturn();

        ModelResponse model = objectMapper.readValue(
                modelResult.getResponse().getContentAsString(), 
                ModelResponse.class);

        // Step 2: Create MCP servers
        McpServerRequest mcpServer1Request = new McpServerRequest(
                "FileSystem MCP", 
                "http://localhost:8080/mcp/filesystem");
        mcpServer1Request.setDescription("MCP Server for file system operations");

        MvcResult mcpServer1Result = mockMvc.perform(post("/api/mcp-servers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mcpServer1Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        McpServerResponse mcpServer1 = objectMapper.readValue(
                mcpServer1Result.getResponse().getContentAsString(),
                McpServerResponse.class);

        McpServerRequest mcpServer2Request = new McpServerRequest(
                "Database MCP", 
                "http://localhost:8080/mcp/database");
        mcpServer2Request.setDescription("MCP Server for database operations");

        MvcResult mcpServer2Result = mockMvc.perform(post("/api/mcp-servers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mcpServer2Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andReturn();

        McpServerResponse mcpServer2 = objectMapper.readValue(
                mcpServer2Result.getResponse().getContentAsString(),
                McpServerResponse.class);

        // Step 3: Create first application
        ApplicationRequest app1Request = new ApplicationRequest("AI Assistant App");
        app1Request.setModelId(model.getId());

        MvcResult app1Result = mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(app1Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("AI Assistant App"))
                .andExpect(jsonPath("$.model.id").value(model.getId()))
                .andReturn();

        ApplicationResponse app1 = objectMapper.readValue(
                app1Result.getResponse().getContentAsString(),
                ApplicationResponse.class);

        // Step 4: Create second application
        ApplicationRequest app2Request = new ApplicationRequest("Code Analysis App");

        MvcResult app2Result = mockMvc.perform(post("/api/applications")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(app2Request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("Code Analysis App"))
                .andReturn();

        ApplicationResponse app2 = objectMapper.readValue(
                app2Result.getResponse().getContentAsString(),
                ApplicationResponse.class);

        // Step 5: Add model to second application
        app2Request.setModelId(model.getId());
        mockMvc.perform(put("/api/applications/" + app2.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(app2Request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.model.id").value(model.getId()));

        // Step 6: Add MCP servers to first application
        mockMvc.perform(post("/api/applications/" + app1.getId() + "/mcp-servers/" + mcpServer1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mcpServers").isArray());

        mockMvc.perform(post("/api/applications/" + app1.getId() + "/mcp-servers/" + mcpServer2.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mcpServers").isArray());

        // Step 7: Verify the configuration
        mockMvc.perform(get("/api/applications/" + app1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("AI Assistant App"))
                .andExpect(jsonPath("$.model.name").value("GPT-4"))
                .andExpect(jsonPath("$.model.type").value("OpenAI"))
                .andExpect(jsonPath("$.mcpServers").isArray());

        // Step 8: Verify we can list all applications
        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
