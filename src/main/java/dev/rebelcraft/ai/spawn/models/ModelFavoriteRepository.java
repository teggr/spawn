package dev.rebelcraft.ai.spawn.models;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing model favorites.
 */
@Repository
public interface ModelFavoriteRepository extends JpaRepository<ModelFavorite, Long> {
    
    Optional<ModelFavorite> findByProvider(String provider);
    
    boolean existsByProvider(String provider);
    
    void deleteByProvider(String provider);
}
