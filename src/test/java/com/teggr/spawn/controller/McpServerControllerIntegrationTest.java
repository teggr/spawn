package com.teggr.spawn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teggr.spawn.dto.McpServerRequest;
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
class McpServerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateMcpServer() throws Exception {
        McpServerRequest request = new McpServerRequest("FileSystem MCP", "http://localhost:8080/mcp");
        request.setDescription("MCP Server for file system operations");

        mockMvc.perform(post("/api/mcp-servers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("FileSystem MCP"))
                .andExpect(jsonPath("$.url").value("http://localhost:8080/mcp"))
                .andExpect(jsonPath("$.description").value("MCP Server for file system operations"));
    }

    @Test
    void shouldGetAllMcpServers() throws Exception {
        mockMvc.perform(get("/api/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnNotFoundWhenMcpServerDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/mcp-servers/999"))
                .andExpect(status().isNotFound());
    }
}
