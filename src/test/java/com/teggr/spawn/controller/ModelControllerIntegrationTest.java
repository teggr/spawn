package com.teggr.spawn.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.teggr.spawn.dto.ModelRequest;
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
class ModelControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateModel() throws Exception {
        ModelRequest request = new ModelRequest("GPT-4", "OpenAI");
        request.setDescription("OpenAI GPT-4 Model");

        mockMvc.perform(post("/api/models")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("GPT-4"))
                .andExpect(jsonPath("$.type").value("OpenAI"))
                .andExpect(jsonPath("$.description").value("OpenAI GPT-4 Model"));
    }

    @Test
    void shouldGetAllModels() throws Exception {
        mockMvc.perform(get("/api/models"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void shouldReturnNotFoundWhenModelDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/models/999"))
                .andExpect(status().isNotFound());
    }
}
