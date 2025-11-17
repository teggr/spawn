package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.mcp.templates.McpServerExampleConfigurationsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class McpServerService {

    private final McpServerRepository mcpServerRepository;
    private final McpServerFavoriteRepository favoriteRepository;
    private final McpServerExampleConfigurationsService templateService;

    public McpServerService(McpServerRepository mcpServerRepository, McpServerFavoriteRepository favoriteRepository, McpServerExampleConfigurationsService templateService) {
        this.mcpServerRepository = mcpServerRepository;
        this.favoriteRepository = favoriteRepository;
        this.templateService = templateService;
    }

    public List<McpServerResponse> getAllMcpServers() {
        // Get all favorites
        Set<String> favoriteNames = favoriteRepository.findAll().stream()
            .map(McpServerFavorite::getServerName)
            .collect(Collectors.toSet());

        return mcpServerRepository.getAll().stream()
            .map(server -> toResponse(server, favoriteNames.contains(server.getName())))
            .collect(Collectors.toList());
    }

    public Optional<McpServerResponse> getMcpServerByName(String name) {
        boolean isFavorite = favoriteRepository.existsByServerName(name);
        return mcpServerRepository.findByName(name)
            .map(server -> toResponse(server, isFavorite));
    }

    public void addFavorite(String serverName) {
        // Check if server exists
        boolean serverExists = mcpServerRepository.existsByName(serverName);

        if (!serverExists) {
            throw new IllegalArgumentException("MCP server not found: " + serverName);
        }

        // Add favorite if not already exists
        if (!favoriteRepository.existsByServerName(serverName)) {
            favoriteRepository.save(new McpServerFavorite(serverName));
        }
    }

    @Transactional
    public void removeFavorite(String serverName) {
        favoriteRepository.deleteByServerName(serverName);
    }

    private McpServerResponse toResponse(McpServer server, boolean isFavorite) {
        return new McpServerResponse(
            server.getName(),
            server.getIcon(),
            server.getDescription(),
            isFavorite,
            templateService.hasExamples(server.getName())
        );
    }
}
