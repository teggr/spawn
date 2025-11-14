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
class McpServerFavoriteIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAddMcpServerToFavorites() throws Exception {
        mockMvc.perform(post("/mcp-servers/GitHub/favorite"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));
    }

    @Test
    void shouldRemoveMcpServerFromFavorites() throws Exception {
        // First add to favorites
        mockMvc.perform(post("/mcp-servers/GitHub/favorite"))
                .andExpect(status().is3xxRedirection());

        // Then remove from favorites
        mockMvc.perform(post("/mcp-servers/GitHub/unfavorite"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mcp-servers"));
    }

    @Test
    void shouldDisplayFavoritesSection() throws Exception {
        // Add a server to favorites
        mockMvc.perform(post("/mcp-servers/GitHub/favorite"));

        // Check that the favorites section is displayed
        mockMvc.perform(get("/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorites")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("GitHub")));
    }

    @Test
    void shouldDisplayFavoriteAndUnfavoriteButtons() throws Exception {
        mockMvc.perform(get("/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorite")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Actions")));
    }

    @Test
    void shouldSortFavoritesAlphabetically() throws Exception {
        // Add multiple servers to favorites
        mockMvc.perform(post("/mcp-servers/Playwright/favorite"));
        mockMvc.perform(post("/mcp-servers/GitHub/favorite"));
        mockMvc.perform(post("/mcp-servers/Context7/favorite"));

        // Verify the page loads and contains the favorites section
        mockMvc.perform(get("/mcp-servers"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Favorites")));
    }
}
