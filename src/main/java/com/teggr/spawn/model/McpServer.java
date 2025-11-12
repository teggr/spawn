package com.teggr.spawn.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "mcp_servers")
public class McpServer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Server name is required")
    @Column(nullable = false)
    private String name;

    @NotBlank(message = "Server URL is required")
    @Column(nullable = false)
    private String url;

    @Column(length = 500)
    private String description;

    // Constructors
    public McpServer() {
    }

    public McpServer(String name, String url) {
        this.name = name;
        this.url = url;
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
