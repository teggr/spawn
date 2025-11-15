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
class McpServerTemplateIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldDisplayTemplatePageForGitHub() throws Exception {
        mockMvc.perform(get("/mcp-servers/GitHub/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MCP Server Template: GitHub")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("github_mcp_pat")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Authorization")));
    }

    @Test
    void shouldDisplayTemplateByCaseInsensitiveName() throws Exception {
        mockMvc.perform(get("/mcp-servers/github/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MCP Server Template: github")));
    }

    @Test
    void shouldDisplayTemplateByNormalizedName() throws Exception {
        // Test using the normalized filename directly
        mockMvc.perform(get("/mcp-servers/azure-mcp-server/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("MCP Server Template: azure-mcp-server")));
    }

    @Test
    void shouldRedirectWhenTemplateNotFound() throws Exception {
        mockMvc.perform(get("/mcp-servers/NonExistentServer/template"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));
    }

    @Test
    void shouldMaskPasswordFieldsInPreview() throws Exception {
        mockMvc.perform(get("/mcp-servers/GitHub/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("*****")))
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("${input:github_mcp_pat}"))));
    }

    @Test
    void shouldShowDefaultValuesInPreview() throws Exception {
        mockMvc.perform(get("/mcp-servers/GitHub/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("https://api.github.com/")));
    }

    @Test
    void shouldDisplayInputsTable() throws Exception {
        mockMvc.perform(get("/mcp-servers/GitHub/template"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Configuration Inputs")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("GitHub Personal Access Token")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("GitHub API base URL")));
    }

    @Test
    void shouldShowViewTemplateButtonInMcpServersList() throws Exception {
        mockMvc.perform(get("/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("View Template")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("/mcp-servers/GitHub/template")));
    }
}
