package dev.rebelcraft.ai.spawn.apps;

import jakarta.validation.constraints.NotBlank;

public class ApplicationRequest {

    @NotBlank(message = "Application name is required")
    private String name;

    private String modelProvider;

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

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }
}
