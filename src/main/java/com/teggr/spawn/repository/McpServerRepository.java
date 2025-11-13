package com.teggr.spawn.repository;

import com.teggr.spawn.model.McpServer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface McpServerRepository extends JpaRepository<McpServer, Long> {
}
