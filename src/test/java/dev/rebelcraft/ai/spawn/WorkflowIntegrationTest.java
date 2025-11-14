package dev.rebelcraft.ai.spawn;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * End-to-end test demonstrating the complete workflow through HTML UI:
 * 1. View available models (loaded from CSV)
 * 2. Create MCP servers
 * 3. Create applications with model providers
 * 4. Add MCP servers to applications via the UI
 */
@SpringBootTest
@AutoConfigureMockMvc
class WorkflowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCompleteFullWorkflow() throws Exception {
        // Step 1: Verify models are loaded from CSV
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Models - Spawn</title>")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("OpenAI")));

        // Step 2: Create MCP servers
        mockMvc.perform(post("/mcp-servers")
                .param("name", "FileSystem MCP")
                .param("url", "http://localhost:8080/mcp/filesystem")
                .param("description", "MCP Server for file system operations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));

        mockMvc.perform(post("/mcp-servers")
                .param("name", "Database MCP")
                .param("url", "http://localhost:8080/mcp/database")
                .param("description", "MCP Server for database operations"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));

        // Step 3: Create applications with model providers from CSV
        mockMvc.perform(post("/applications")
                .param("name", "AI Assistant App")
                .param("modelProvider", "OpenAI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));

        mockMvc.perform(post("/applications")
                .param("name", "Code Analysis App")
                .param("modelProvider", "Anthropic Claude"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));

        // Step 4: Verify we can view the applications list page
        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Applications - Spawn</title>")));

        // Step 5: Verify we can view the application detail page
        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Application Details - Spawn</title>")));

        // Step 6: Add MCP server to application
        mockMvc.perform(post("/applications/1/mcp-servers/add")
                .param("mcpServerId", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/1"));
    }
}
