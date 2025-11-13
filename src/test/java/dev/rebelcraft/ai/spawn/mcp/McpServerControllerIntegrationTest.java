package dev.rebelcraft.ai.spawn.mcp;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class McpServerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateMcpServer() throws Exception {
        mockMvc.perform(post("/mcp-servers")
                .param("name", "FileSystem MCP")
                .param("url", "http://localhost:8080/mcp")
                .param("description", "MCP Server for file system operations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));
    }

    @Test
    void shouldGetAllMcpServers() throws Exception {
        mockMvc.perform(get("/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>MCP Servers - Spawn</title>")));
    }

    @Test
    void shouldReturnNotFoundWhenMcpServerDoesNotExist() throws Exception {
        mockMvc.perform(get("/mcp-servers/999/edit"))
                .andExpect(status().is4xxClientError());
    }
}
