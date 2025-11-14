package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.models.ModelResponse;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public class ApplicationResponse {

    private Long id;
    private String name;
    private LocalDateTime createdAt;
    private Set<ModelResponse> models = new HashSet<>();
    private Set<AgentResponse> agents = new HashSet<>();

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

    public Set<ModelResponse> getModels() {
        return models;
    }

    public void setModels(Set<ModelResponse> models) {
        this.models = models;
    }

    public Set<AgentResponse> getAgents() {
        return agents;
    }

    public void setAgents(Set<AgentResponse> agents) {
        this.agents = agents;
    }

}
