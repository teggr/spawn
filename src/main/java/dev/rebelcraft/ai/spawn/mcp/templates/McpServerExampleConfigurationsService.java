package dev.rebelcraft.ai.spawn.mcp.templates;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class McpServerExampleConfigurationsService {

  private final McpServerExampleConfigurationsRepository repository;

  public McpServerExampleConfigurationsService(McpServerExampleConfigurationsRepository repository) {
    this.repository = repository;
  }

  public List<McpServerExampleConfiguration> getExamples(String mcpServerName) {
    return this.repository.findAllByName(mcpServerName);
  }

  public boolean hasExamples(String mcpServerName) {
    return this.repository.hasTemplates(mcpServerName);
  }
}
