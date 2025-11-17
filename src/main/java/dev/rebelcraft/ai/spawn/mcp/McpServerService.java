package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class McpServerService {

    private final McpRepository mcpRepository;
    private final McpServerFavoriteRepository favoriteRepository;
    private final McpTemplateService templateService;

    public McpServerService(McpRepository mcpRepository, McpServerFavoriteRepository favoriteRepository, McpTemplateService templateService) {
        this.mcpRepository = mcpRepository;
        this.favoriteRepository = favoriteRepository;
        this.templateService = templateService;
    }

    public List<McpServerResponse> getAllMcpServers() {
        // Get all favorites
        Set<String> favoriteNames = favoriteRepository.findAll().stream()
            .map(McpServerFavorite::getServerName)
            .collect(Collectors.toSet());

        return mcpRepository.getAll().stream()
            .map(server -> toResponse(server, favoriteNames.contains(server.getName())))
            .collect(Collectors.toList());
    }

    public Optional<McpServerResponse> getMcpServerByName(String name) {
        boolean isFavorite = favoriteRepository.existsByServerName(name);
        return mcpRepository.findByName(name)
            .map(server -> toResponse(server, isFavorite));
    }

    public void addFavorite(String serverName) {
        // Check if server exists
        boolean serverExists = mcpRepository.existsByName(serverName);

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
        boolean templateAvailable = templateService.getTemplateForServer(server.getName()).isPresent();
        String templateFilename = templateService.getTemplateFilenameForServer(server.getName()).orElse(null);

        return new McpServerResponse(
            server.getName(),
            server.getIcon(),
            server.getDescription(),
            isFavorite,
            templateAvailable,
            templateFilename
        );
    }
}
