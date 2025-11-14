package dev.rebelcraft.ai.spawn.agents;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AgentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AgentRepository agentRepository;

    @Test
    void shouldCreateAgent() throws Exception {
        mockMvc.perform(post("/agents")
                .param("name", "Test Agent")
                .param("systemPrompt", "You are a helpful assistant."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/agents"));
    }

    @Test
    void shouldGetNewAgentForm() throws Exception {
        mockMvc.perform(get("/agents/new"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Create Agent - Spawn</title>")))
                // Top action buttons should be present
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Save")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Back")))
                // Free-text MCP input should NOT be present
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Other MCP name"))));
    }

    @Test
    void shouldGetAllAgents() throws Exception {
        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Agents - Spawn</title>")));
    }

    @Test
    void shouldHighlightUnknownMcpInDetail() throws Exception {
        mockMvc.perform(post("/agents")
                .param("name", "AgentWithUnknownMcp")
                .param("systemPrompt", "Prompt")
                .param("mcpServerNames", "NonExistentMCP"))
                .andExpect(status().is3xxRedirection());

        // get created agent id
        Long id = agentRepository.findAll().stream().filter(a -> "AgentWithUnknownMcp".equals(a.getName())).findFirst().orElseThrow().getId();

        mockMvc.perform(get("/agents/" + id))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Unknown MCP")))
                // The detail page should no longer have inline add/remove forms for MCPs
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("Select a server..."))));
    }

    @Test
    void shouldEditAgent() throws Exception {
        mockMvc.perform(post("/agents")
                .param("name", "ToEdit")
                .param("systemPrompt", "Prompt"))
                .andExpect(status().is3xxRedirection());

        Long id = agentRepository.findAll().stream().filter(a -> "ToEdit".equals(a.getName())).findFirst().orElseThrow().getId();

        mockMvc.perform(post("/agents/" + id)
                .param("name", "EditedName")
                .param("systemPrompt", "NewPrompt"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("EditedName")));
    }

    @Test
    void shouldDeleteAgent() throws Exception {
        mockMvc.perform(post("/agents")
                .param("name", "ToDelete")
                .param("systemPrompt", "Prompt"))
                .andExpect(status().is3xxRedirection());

        Long id = agentRepository.findAll().stream().filter(a -> "ToDelete".equals(a.getName())).findFirst().orElseThrow().getId();

        mockMvc.perform(post("/agents/" + id + "/delete"))
                .andExpect(status().is3xxRedirection());

        mockMvc.perform(get("/agents"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.not(org.hamcrest.Matchers.containsString("ToDelete"))));
    }
}
