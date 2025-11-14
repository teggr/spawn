package dev.rebelcraft.ai.spawn.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity for storing user's favorite models.
 * Since models are loaded from CSV, we store favorites by provider name.
 */
@Entity
@Table(name = "model_favorites")
public class ModelFavorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "provider", nullable = false, unique = true)
    private String provider;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    // Constructors
    public ModelFavorite() {
    }

    public ModelFavorite(String provider) {
        this.provider = provider;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
