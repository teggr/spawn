package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class McpServerService {

    private final McpServerRepository mcpServerRepository;

    public McpServerService(McpServerRepository mcpServerRepository) {
        this.mcpServerRepository = mcpServerRepository;
    }

    public McpServerResponse createMcpServer(McpServerRequest request) {
        McpServer mcpServer = new McpServer(request.getName(), request.getUrl());
        mcpServer.setDescription(request.getDescription());
        McpServer savedServer = mcpServerRepository.save(mcpServer);
        return toResponse(savedServer);
    }

    public List<McpServerResponse> getAllMcpServers() {
        return mcpServerRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public McpServerResponse getMcpServerById(Long id) {
        McpServer mcpServer = mcpServerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MCP Server not found with id: " + id));
        return toResponse(mcpServer);
    }

    public McpServerResponse updateMcpServer(Long id, McpServerRequest request) {
        McpServer mcpServer = mcpServerRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("MCP Server not found with id: " + id));
        
        mcpServer.setName(request.getName());
        mcpServer.setUrl(request.getUrl());
        mcpServer.setDescription(request.getDescription());
        
        McpServer updatedServer = mcpServerRepository.save(mcpServer);
        return toResponse(updatedServer);
    }

    public void deleteMcpServer(Long id) {
        if (!mcpServerRepository.existsById(id)) {
            throw new ResourceNotFoundException("MCP Server not found with id: " + id);
        }
        mcpServerRepository.deleteById(id);
    }

    private McpServerResponse toResponse(McpServer mcpServer) {
        return new McpServerResponse(
            mcpServer.getId(),
            mcpServer.getName(),
            mcpServer.getUrl(),
            mcpServer.getDescription()
        );
    }
}
