package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class McpServerService {

    private final List<McpServer> mcpServers;

    public McpServerService() {
        this.mcpServers = loadMcpServersFromCsv();
    }

    private List<McpServer> loadMcpServersFromCsv() {
        List<McpServer> loadedServers = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("mcp/mcp_servers.csv");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                // Skip header line
                String headerLine = reader.readLine();
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCsvLine(line);
                    if (fields.length >= 3) {
                        McpServer server = new McpServer(
                            fields[0].trim(), // Name
                            fields[1].trim(), // Icon
                            fields[2].trim()  // Description
                        );
                        loadedServers.add(server);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load MCP servers from CSV", e);
        }
        return loadedServers;
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

    public List<McpServerResponse> getAllMcpServers() {
        return mcpServers.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public Optional<McpServerResponse> getMcpServerByName(String name) {
        return mcpServers.stream()
            .filter(server -> server.getName().equalsIgnoreCase(name))
            .map(this::toResponse)
            .findFirst();
    }

    private McpServerResponse toResponse(McpServer server) {
        return new McpServerResponse(
            server.getName(),
            server.getIcon(),
            server.getDescription()
        );
    }
}
