package dev.rebelcraft.ai.spawn.mcp;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class McpTemplateServiceTest {

    @Autowired
    private ResourcePatternResolver resourceResolver;

    @Autowired
    private ObjectMapper objectMapper;

    private McpTemplateService templateService;

    @BeforeEach
    void setUp() {
        templateService = new McpTemplateService(resourceResolver, objectMapper);
        templateService.loadTemplates();
    }

    @Test
    void shouldLoadTemplatesFromResources() {
        Map<String, McpTemplate> allTemplates = templateService.getAllTemplates();
        assertFalse(allTemplates.isEmpty(), "Should load at least one template");
    }

    @Test
    void shouldGetTemplateByExactMatch() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("GitHub");
        assertTrue(template.isPresent(), "Should find template with exact name match");
        assertNotNull(template.get().getServers());
        assertNotNull(template.get().getInputs());
    }

    @Test
    void shouldGetTemplateByCaseInsensitiveMatch() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("github");
        assertTrue(template.isPresent(), "Should find template with case-insensitive match");
    }

    @Test
    void shouldGetTemplateByNormalizedMatch() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("Azure MCP Server");
        assertTrue(template.isPresent(), "Should find template with normalized name");
        
        // Also test the normalized filename directly
        Optional<McpTemplate> templateDirect = templateService.getTemplateForServer("azure-mcp-server");
        assertTrue(templateDirect.isPresent(), "Should find template with normalized filename");
    }

    @Test
    void shouldReturnEmptyOptionalForNonExistentTemplate() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("NonExistentServer");
        assertFalse(template.isPresent(), "Should not find non-existent template");
    }

    @Test
    void shouldGetTemplateFilename() {
        Optional<String> filename = templateService.getTemplateFilenameForServer("GitHub");
        assertTrue(filename.isPresent());
        assertEquals("GitHub.json", filename.get());
    }

    @Test
    void shouldCompileTemplateWithPasswordMasking() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("GitHub");
        assertTrue(template.isPresent());

        String compiled = templateService.compileTemplate(template.get());
        
        assertNotNull(compiled);
        assertTrue(compiled.contains("*****"), "Should mask password input with *****");
        assertFalse(compiled.contains("${input:github_mcp_pat}"), "Should replace placeholder");
    }

    @Test
    void shouldCompileTemplateWithDefaultValues() {
        Optional<McpTemplate> template = templateService.getTemplateForServer("GitHub");
        assertTrue(template.isPresent());

        String compiled = templateService.compileTemplate(template.get());
        
        assertNotNull(compiled);
        assertTrue(compiled.contains("https://api.github.com/"), "Should use default value for non-password input");
    }

    @Test
    void shouldReplaceUnknownInputsWithHints() {
        // Create a template with an input reference that doesn't exist in inputs list
        McpTemplate template = new McpTemplate();
        Map<String, Object> servers = Map.of(
            "test", Map.of(
                "url", "https://example.com/${input:unknown_input}"
            )
        );
        template.setServers(servers);
        template.setInputs(java.util.Collections.emptyList());

        String compiled = templateService.compileTemplate(template);
        
        assertNotNull(compiled);
        assertTrue(compiled.contains("<unknown_input>"), "Should replace unknown input with hint");
    }

    @Test
    void shouldHandleTemplateWithNoInputs() {
        McpTemplate template = new McpTemplate();
        Map<String, Object> servers = Map.of(
            "simple", Map.of(
                "type", "http",
                "url", "https://example.com"
            )
        );
        template.setServers(servers);
        template.setInputs(null);

        String compiled = templateService.compileTemplate(template);
        
        assertNotNull(compiled);
        assertTrue(compiled.contains("https://example.com"), "Should compile template without inputs");
    }
}
