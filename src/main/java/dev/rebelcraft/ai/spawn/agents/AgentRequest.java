package dev.rebelcraft.ai.spawn.agents;

import java.util.ArrayList;
import java.util.List;

public class AgentRequest {
    private String name;
    private String description;
    private String systemPrompt;
    private String modelProvider;
    private List<String> mcpServerNames = new ArrayList<>();

    public AgentRequest() { }

    public AgentRequest(String name, String systemPrompt) {
        this.name = name;
        this.systemPrompt = systemPrompt;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
    }

    public List<String> getMcpServerNames() {
        return mcpServerNames;
    }

    public void setMcpServerNames(List<String> mcpServerNames) {
        this.mcpServerNames = mcpServerNames;
    }

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }
}

