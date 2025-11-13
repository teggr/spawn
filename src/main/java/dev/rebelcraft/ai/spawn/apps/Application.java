package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.mcp.McpServer;
import dev.rebelcraft.ai.spawn.models.Model;
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

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @ManyToMany
    @JoinTable(
        name = "application_mcp_servers",
        joinColumns = @JoinColumn(name = "application_id"),
        inverseJoinColumns = @JoinColumn(name = "mcp_server_id")
    )
    private Set<McpServer> mcpServers = new HashSet<>();

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

    public Model getModel() {
        return model;
    }

    public void setModel(Model model) {
        this.model = model;
    }

    public Set<McpServer> getMcpServers() {
        return mcpServers;
    }

    public void setMcpServers(Set<McpServer> mcpServers) {
        this.mcpServers = mcpServers;
    }

    public void addMcpServer(McpServer mcpServer) {
        this.mcpServers.add(mcpServer);
    }

    public void removeMcpServer(McpServer mcpServer) {
        this.mcpServers.remove(mcpServer);
    }
}
