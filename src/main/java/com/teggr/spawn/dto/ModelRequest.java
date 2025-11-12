package com.teggr.spawn.dto;

import jakarta.validation.constraints.NotBlank;

public class ModelRequest {

    @NotBlank(message = "Model name is required")
    private String name;

    @NotBlank(message = "Model type is required")
    private String type;

    private String description;

    // Constructors
    public ModelRequest() {
    }

    public ModelRequest(String name, String type) {
        this.name = name;
        this.type = type;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
