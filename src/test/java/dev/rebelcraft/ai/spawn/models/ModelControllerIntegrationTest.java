package dev.rebelcraft.ai.spawn.models;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ModelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldCreateModel() throws Exception {
        mockMvc.perform(post("/models")
                .param("name", "GPT-4")
                .param("type", "OpenAI")
                .param("description", "OpenAI GPT-4 Model"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/models"));
    }

    @Test
    void shouldGetAllModels() throws Exception {
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("<title>Models - Spawn</title>")));
    }

    @Test
    void shouldReturnNotFoundWhenModelDoesNotExist() throws Exception {
        mockMvc.perform(get("/models/999/edit"))
                .andExpect(status().is4xxClientError());
    }
}
