package dev.rebelcraft.ai.spawn.mcp;

import jakarta.validation.constraints.NotBlank;

public class McpServerRequest {

    @NotBlank(message = "Server name is required")
    private String name;

    @NotBlank(message = "Server URL is required")
    private String url;

    private String description;

    // Constructors
    public McpServerRequest() {
    }

    public McpServerRequest(String name, String url) {
        this.name = name;
        this.url = url;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
