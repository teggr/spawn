package dev.rebelcraft.ai.spawn.apps;

import jakarta.validation.constraints.NotBlank;
import java.util.HashSet;
import java.util.Set;

public class ApplicationRequest {

    @NotBlank(message = "Application name is required")
    private String name;

    private Set<String> modelProviders = new HashSet<>();
    private Set<String> agentNames = new HashSet<>();

    // Constructors
    public ApplicationRequest() {
    }

    public ApplicationRequest(String name) {
        this.name = name;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getModelProviders() {
        return modelProviders;
    }

    public void setModelProviders(Set<String> modelProviders) {
        this.modelProviders = modelProviders;
    }

    public Set<String> getAgentNames() {
        return agentNames;
    }

    public void setAgentNames(Set<String> agentNames) {
        this.agentNames = agentNames;
    }
}
