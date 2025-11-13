package com.teggr.spawn.dto;

import java.time.LocalDateTime;
import java.util.Set;

public class ApplicationResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private ModelResponse model;
    private Set<McpServerResponse> mcpServers;

    // Constructors
    public ApplicationResponse() {
    }

    public ApplicationResponse(Long id, String name, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public ModelResponse getModel() {
        return model;
    }

    public void setModel(ModelResponse model) {
        this.model = model;
    }

    public Set<McpServerResponse> getMcpServers() {
        return mcpServers;
    }

    public void setMcpServers(Set<McpServerResponse> mcpServers) {
        this.mcpServers = mcpServers;
    }
}
