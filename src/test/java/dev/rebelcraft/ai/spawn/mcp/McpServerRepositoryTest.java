package dev.rebelcraft.ai.spawn.mcp;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class McpServerRepositoryTest {

    @Test
    void loadValidCsv_returnsAllServers() {
        McpServerRepository repo = new McpServerRepository("mcp/valid_mcp_servers.csv");
        List<McpServer> servers = repo.getAll();
        assertEquals(3, servers.size());

        // Find by name instead of relying on fixed index order which can vary
        McpServer local = servers.stream().filter(s -> "LocalFS".equals(s.getName())).findFirst().orElse(null);
        assertNotNull(local);
        assertEquals("fa-folder", local.getIcon());
        assertEquals("Provides filesystem access", local.getDescription());

        McpServer quoted = servers.stream().filter(s -> "Quoted".equals(s.getName())).findFirst().orElse(null);
        assertNotNull(quoted);
        assertEquals("Has, comma in description", quoted.getDescription());
    }

    @Test
    void malformedCsv_skipsMalformedLines() {
        McpServerRepository repo = new McpServerRepository("mcp/malformed_mcp_servers.csv");
        List<McpServer> servers = repo.getAll();
        // The malformed file should result in zero valid servers parsed
        assertEquals(0, servers.size());
    }

    @Test
    void findByName_isCaseInsensitiveAndTrimmed() {
        McpServerRepository repo = new McpServerRepository("mcp/valid_mcp_servers.csv");
        assertTrue(repo.existsByName("localfs"));
        assertTrue(repo.existsByName(" LocalFS "));
        assertTrue(repo.findByName("dbserver").isPresent());
    }

    @Test
    void missingCsv_throwsRuntimeException() {
        assertThrows(RuntimeException.class, () -> new McpServerRepository("mcp/does_not_exist.csv"));
    }
}
