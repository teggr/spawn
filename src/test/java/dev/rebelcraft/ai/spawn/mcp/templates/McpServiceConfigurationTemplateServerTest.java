package dev.rebelcraft.ai.spawn.mcp.templates;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.ResourcePatternResolver;

@SpringBootTest
class McpServiceConfigurationTemplateServerTest {

    @Autowired
    private ResourcePatternResolver resourceResolver;

    private McpServerExampleConfigurationsRepository templateService;

    @BeforeEach
    void setUp() {
        templateService = new McpServerExampleConfigurationsRepository(resourceResolver);
        templateService.loadTemplates();
    }

}
