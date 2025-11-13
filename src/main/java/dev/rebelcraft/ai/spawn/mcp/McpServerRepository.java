package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McpServerRepository extends JpaRepository<McpServer, Long> {
}
