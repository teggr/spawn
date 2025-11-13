package com.teggr.spawn.dto;

import jakarta.validation.constraints.NotBlank;

public class ApplicationRequest {

    @NotBlank(message = "Application name is required")
    private String name;

    private Long modelId;

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

    public Long getModelId() {
        return modelId;
    }

    public void setModelId(Long modelId) {
        this.modelId = modelId;
    }
}
