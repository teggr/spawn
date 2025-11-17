package dev.rebelcraft.ai.spawn.mcp.templates;

public class McpServerExampleConfiguration {

  private final String name;
  private final String mcpServer;
  private final String description;
  private final String configuration;

  public McpServerExampleConfiguration(String name, String mcpServer, String description, String configuration) {
    this.name = name;
    this.mcpServer = mcpServer;
    this.description = description;
    this.configuration = configuration;
  }

  public String name() {
    return name;
  }

  public String mcpServer() {
    return mcpServer;
  }

  public String description() {
    return description;
  }

  public String configuration() {
    return configuration;
  }

}
