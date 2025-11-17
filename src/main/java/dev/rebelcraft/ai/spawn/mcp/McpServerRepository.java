package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Repository
public class McpServerRepository {

    private final List<McpServer> mcpServers;

    public McpServerRepository() {
        this("mcp/mcp_servers.csv");
    }

    /**
     * Test-friendly constructor that allows pointing at a different classpath resource.
     */
    public McpServerRepository(String classpathResource) {
        this.mcpServers = loadMcpServersFromCsv(classpathResource);
    }

    /**
     * Test-only constructor that allows supplying servers directly (avoids file I/O).
     */
    protected McpServerRepository(List<McpServer> servers) {
        this.mcpServers = Collections.unmodifiableList(new ArrayList<>(servers));
    }

    private List<McpServer> loadMcpServersFromCsv(String classpathResource) {
        List<McpServer> loadedServers = new ArrayList<>();
        try {
            Resource resource = new ClassPathResource(classpathResource);
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

                // Skip header line
                String headerLine = reader.readLine();

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    String[] fields = parseCsvLine(line);
                    if (fields.length >= 3) {
                        String name = fields[0].trim();
                        if (name.isEmpty()) continue; // ignore entries without a name
                        McpServer server = new McpServer(
                                name, // Name
                                fields[1].trim(), // Icon
                                fields[2].trim()  // Description
                        );
                        loadedServers.add(server);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load MCP servers from CSV: " + classpathResource, e);
        }
        return Collections.unmodifiableList(loadedServers);
    }

    /**
     * Parse a CSV line handling quoted fields properly
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());

        return fields.toArray(new String[0]);
    }

    public List<McpServer> getAll() {
        return mcpServers;
    }

    public Optional<McpServer> findByName(String name) {
        if (name == null) return Optional.empty();
        String trimmed = name.trim();
        return mcpServers.stream()
                .filter(s -> s.getName() != null && s.getName().equalsIgnoreCase(trimmed))
                .findFirst();
    }

    public boolean existsByName(String name) {
        return findByName(name).isPresent();
    }
}
