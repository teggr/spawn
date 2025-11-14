package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for managing MCP server favorites.
 */
@Repository
public interface McpServerFavoriteRepository extends JpaRepository<McpServerFavorite, Long> {
    
    Optional<McpServerFavorite> findByServerName(String serverName);
    
    boolean existsByServerName(String serverName);
    
    void deleteByServerName(String serverName);
}
