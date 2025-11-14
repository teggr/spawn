package dev.rebelcraft.ai.spawn.agents;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AgentResponse {
    private Long id;
    private String name;
    private String description;
    private String systemPrompt;
    private List<String> mcpServerNames = new ArrayList<>();
    private Set<String> unmatchedMcpNames = new HashSet<>();
    private LocalDateTime createdAt;

    public AgentResponse() { }

    public AgentResponse(Long id, String name, String description, String systemPrompt, List<String> mcpServerNames, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.systemPrompt = systemPrompt;
        this.mcpServerNames = mcpServerNames;
        this.createdAt = createdAt;
    }

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

    public Set<String> getUnmatchedMcpNames() {
        return unmatchedMcpNames;
    }

    public void setUnmatchedMcpNames(Set<String> unmatchedMcpNames) {
        this.unmatchedMcpNames = unmatchedMcpNames;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}

