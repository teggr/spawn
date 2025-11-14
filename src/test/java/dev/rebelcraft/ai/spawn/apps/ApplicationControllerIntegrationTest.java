package dev.rebelcraft.ai.spawn.apps;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateApplication() throws Exception {
        mockMvc.perform(post("/applications")
                .param("name", "Test App"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
    }

    @Test
    void shouldCreateApplicationWithModelsAndAgents() throws Exception {
        // First create an agent
        mockMvc.perform(post("/agents")
                .param("name", "Test Agent")
                .param("systemPrompt", "You are a test agent"));
        
        // Create application with models and agents
        mockMvc.perform(post("/applications")
                .param("name", "Multi-Resource App")
                .param("modelProviders", "OpenAI")
                .param("modelProviders", "Anthropic Claude")
                .param("agentNames", "Test Agent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
    }

    @Test
    void shouldGetAllApplications() throws Exception {
        mockMvc.perform(get("/applications"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Applications - Spawn</title>")));
    }

    @Test
    void shouldReturnNotFoundWhenApplicationDoesNotExist() throws Exception {
        mockMvc.perform(get("/applications/999"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldReturnBadRequestWhenNameIsEmpty() throws Exception {
        // HTML form validation should prevent empty name
        // but if submitted, it will show the form again with error
        mockMvc.perform(post("/applications")
                .param("name", ""))
                .andExpect(status().isOk()); // Returns form page with error
    }

    @Test
    void shouldAddModelToApplication() throws Exception {
        // Create application
        mockMvc.perform(post("/applications")
                .param("name", "App for Models"));
        
        // Add a model
        mockMvc.perform(post("/applications/1/models/add")
                .param("modelProvider", "OpenAI"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/1"));
    }

    @Test
    void shouldRemoveModelFromApplication() throws Exception {
        // Create application with a model
        mockMvc.perform(post("/applications")
                .param("name", "App with Model")
                .param("modelProviders", "OpenAI"));
        
        // Remove the model
        mockMvc.perform(post("/applications/1/models/OpenAI/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/1"));
    }

    @Test
    void shouldAddAgentToApplication() throws Exception {
        // Create agent
        mockMvc.perform(post("/agents")
                .param("name", "Agent For App")
                .param("systemPrompt", "Test prompt"));
        
        // Create application
        mockMvc.perform(post("/applications")
                .param("name", "App for Agents"));
        
        // Add the agent
        mockMvc.perform(post("/applications/1/agents/add")
                .param("agentName", "Agent For App"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/1"));
    }

    @Test
    void shouldRemoveAgentFromApplication() throws Exception {
        // Create agent
        mockMvc.perform(post("/agents")
                .param("name", "Agent to Remove")
                .param("systemPrompt", "Test prompt"));
        
        // Create application with agent
        mockMvc.perform(post("/applications")
                .param("name", "App with Agent")
                .param("agentNames", "Agent to Remove"));
        
        // Remove the agent
        mockMvc.perform(post("/applications/1/agents/Agent to Remove/remove"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications/1"));
    }

    @Test
    void shouldUpdateApplicationWithMultipleResources() throws Exception {
        // Create agent
        mockMvc.perform(post("/agents")
                .param("name", "Update Agent")
                .param("systemPrompt", "Test prompt"));
        
        // Create application
        mockMvc.perform(post("/applications")
                .param("name", "Original App"));
        
        // Update with models and agents
        mockMvc.perform(post("/applications/1")
                .param("name", "Updated App")
                .param("modelProviders", "OpenAI")
                .param("modelProviders", "Anthropic Claude")
                .param("agentNames", "Update Agent"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/applications"));
    }

    @Test
    void shouldDisplayEditFormWithSelectedModelsAndAgents() throws Exception {
        // Create agent
        mockMvc.perform(post("/agents")
                .param("name", "Edit Agent")
                .param("systemPrompt", "Test prompt"));
        
        // Create application with resources
        mockMvc.perform(post("/applications")
                .param("name", "App to Edit")
                .param("modelProviders", "OpenAI")
                .param("agentNames", "Edit Agent"));
        
        // Get edit form
        mockMvc.perform(get("/applications/1/edit"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("applicationId"))
                .andExpect(model().attributeExists("name"))
                .andExpect(model().attributeExists("selectedModelProviders"))
                .andExpect(model().attributeExists("selectedAgentNames"))
                .andExpect(model().attributeExists("models"))
                .andExpect(model().attributeExists("agents"));
    }

    @Test
    void shouldDisplayDetailPageWithModelsAndAgents() throws Exception {
        // Create agent
        mockMvc.perform(post("/agents")
                .param("name", "Detail Agent")
                .param("systemPrompt", "Test prompt"));
        
        // Create application with resources
        mockMvc.perform(post("/applications")
                .param("name", "App Details")
                .param("modelProviders", "OpenAI")
                .param("agentNames", "Detail Agent"));
        
        // View detail page
        mockMvc.perform(get("/applications/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("application"))
                .andExpect(model().attributeExists("availableModels"))
                .andExpect(model().attributeExists("availableAgents"))
                .andExpect(model().attributeExists("availableServers"));
    }
}
