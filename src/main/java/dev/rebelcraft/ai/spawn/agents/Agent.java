package dev.rebelcraft.ai.spawn.agents;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "agents")
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Agent name is required")
    @Column(nullable = false)
    private String name;

    private String description;

    @Lob
    @Column(name = "system_prompt")
    private String systemPrompt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ElementCollection
    @CollectionTable(
        name = "agent_mcp_servers",
        joinColumns = @JoinColumn(name = "agent_id")
    )
    @Column(name = "mcp_server_name")
    private Set<String> mcpServerNames = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Agent() { }

    public Agent(String name, String systemPrompt) {
        this.name = name;
        this.systemPrompt = systemPrompt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<String> getMcpServerNames() {
        return mcpServerNames;
    }

    public void setMcpServerNames(Set<String> mcpServerNames) {
        this.mcpServerNames = mcpServerNames;
    }

    public void addMcpServerName(String mcpServerName) {
        if (mcpServerName != null && !mcpServerName.isBlank()) {
            this.mcpServerNames.add(mcpServerName);
        }
    }

    public void removeMcpServerName(String mcpServerName) {
        this.mcpServerNames.remove(mcpServerName);
    }
}
