package dev.rebelcraft.ai.spawn.apps;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "applications")
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Application name is required")
    @Column(nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "model_provider")
    private String modelProvider;

    @ElementCollection
    @CollectionTable(
        name = "application_mcp_servers",
        joinColumns = @JoinColumn(name = "application_id")
    )
    @Column(name = "mcp_server_name")
    private Set<String> mcpServerNames = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public Application() {
    }

    public Application(String name) {
        this.name = name;
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

    public String getModelProvider() {
        return modelProvider;
    }

    public void setModelProvider(String modelProvider) {
        this.modelProvider = modelProvider;
    }

    public Set<String> getMcpServerNames() {
        return mcpServerNames;
    }

    public void setMcpServerNames(Set<String> mcpServerNames) {
        this.mcpServerNames = mcpServerNames;
    }

    public void addMcpServerName(String mcpServerName) {
        this.mcpServerNames.add(mcpServerName);
    }

    public void removeMcpServerName(String mcpServerName) {
        this.mcpServerNames.remove(mcpServerName);
    }
}
