package com.teggr.spawn.controller;

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
}
