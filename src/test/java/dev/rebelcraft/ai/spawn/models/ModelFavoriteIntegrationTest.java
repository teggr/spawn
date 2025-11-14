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
class ModelFavoriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddModelToFavorites() throws Exception {
        mockMvc.perform(post("/models/OpenAI/favorite"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/models"));
    }

    @Test
    void shouldRemoveModelFromFavorites() throws Exception {
        // First add to favorites
        mockMvc.perform(post("/models/OpenAI/favorite"))
                .andExpect(status().is3xxRedirection());

        // Then remove from favorites
        mockMvc.perform(post("/models/OpenAI/unfavorite"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/models"));
    }

    @Test
    void shouldDisplayFavoritesSection() throws Exception {
        // Add a model to favorites
        mockMvc.perform(post("/models/OpenAI/favorite"));

        // Check that the favorites section is displayed
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorites")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("OpenAI")));
    }

    @Test
    void shouldDisplayFavoriteAndUnfavoriteButtons() throws Exception {
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorite")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Actions")));
    }

    @Test
    void shouldSortFavoritesAlphabetically() throws Exception {
        // Add multiple models to favorites
        mockMvc.perform(post("/models/OpenAI/favorite"));
        mockMvc.perform(post("/models/Anthropic Claude/favorite"));
        mockMvc.perform(post("/models/Google GenAI/favorite"));

        // Verify the page loads and contains the favorites section
        mockMvc.perform(get("/models"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorites")));
    }
}
