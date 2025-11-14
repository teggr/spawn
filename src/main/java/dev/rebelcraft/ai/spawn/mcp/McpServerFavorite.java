package dev.rebelcraft.ai.spawn.mcp;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing user's favorite MCP servers.
 * Since MCP servers are loaded from CSV, we store favorites by server name.
 */
@Entity
@Table(name = "mcp_server_favorites")
public class McpServerFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "server_name", nullable = false, unique = true)
    private String serverName;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public McpServerFavorite() {
    }

    public McpServerFavorite(String serverName) {
        this.serverName = serverName;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
