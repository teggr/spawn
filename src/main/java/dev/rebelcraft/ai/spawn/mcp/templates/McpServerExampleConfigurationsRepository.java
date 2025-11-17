package dev.rebelcraft.ai.spawn.mcp.templates;

import jakarta.annotation.PostConstruct;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Repository;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class McpServerExampleConfigurationsRepository {

  private static final Logger logger = LoggerFactory.getLogger(McpServerExampleConfigurationsRepository.class);

  private final ResourcePatternResolver resourceResolver;

  // Index: -> (mcp server, template[])
  private final Map<String, List<McpServerExampleConfiguration>> templatesByMcpServer = new HashMap<>();

  public McpServerExampleConfigurationsRepository(ResourcePatternResolver resourceResolver) {
    this.resourceResolver = resourceResolver;
  }

  @PostConstruct
  void loadTemplates() {
    try {
      Resource[] resources = resourceResolver.getResources("classpath:/mcp/configuration/examples/*.md");
      logger.info("Found {} template files", resources.length);

      for (Resource resource : resources) {
        try {
          McpServerExampleConfiguration template = readTemplateFromResource(resource);

          List<McpServerExampleConfiguration> list = templatesByMcpServer.get(template.mcpServer());
          if(list == null) {
            list = new java.util.ArrayList<>();
            templatesByMcpServer.put(template.mcpServer(), list);
          }
          list.add(template);

          logger.info("Loaded template: {} for {}", template.name(), template.mcpServer());
        } catch (IOException e) {
          logger.error("Failed to parse template file: {}", resource.getFilename(), e);
        }
      }
    } catch (IOException e) {
      logger.error("Failed to load templates", e);
    }
  }

  private McpServerExampleConfiguration readTemplateFromResource(Resource resource) throws IOException {

    String filename = resource.getFilename();
    String baseName = FilenameUtils.getBaseName(filename);

    String contentAsString = resource.getContentAsString(StandardCharsets.UTF_8);

    String yamlSection = null;
    String body = contentAsString;

    if (contentAsString.startsWith("---")) {
      int second = contentAsString.indexOf("---", 3);
      if (second != -1) {
        yamlSection = contentAsString.substring(3, second).trim();
        body = contentAsString.substring(second + 3).trim();
      }
    }

    String description = "";
    String mcpServer = "";

    if (yamlSection != null && !yamlSection.isEmpty()) {
      Yaml yaml = new Yaml();
      Object parsed = yaml.load(yamlSection);
      if (parsed instanceof Map) {
        //noinspection unchecked
        description = ((Map) parsed).getOrDefault("description", "").toString();
        mcpServer = ((Map) parsed).getOrDefault("mcp_server", "").toString();
      }
    }

    return new McpServerExampleConfiguration(baseName, mcpServer, description, body);
  }

  public boolean hasTemplates(String name) {
    return this.templatesByMcpServer.containsKey(name);
  }

  public List<McpServerExampleConfiguration> findAllByName(String mcpServerName) {
    return this.templatesByMcpServer.getOrDefault(mcpServerName, Collections.emptyList());
  }

}
