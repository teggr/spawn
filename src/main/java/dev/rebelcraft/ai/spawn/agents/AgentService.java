package dev.rebelcraft.ai.spawn.agents;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerService;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.models.ModelService;
import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class AgentService {

    private final AgentRepository agentRepository;
    private final McpServerService mcpServerService;
    private final ModelService modelService;

    public AgentService(AgentRepository agentRepository, McpServerService mcpServerService, ModelService modelService) {
        this.agentRepository = agentRepository;
        this.mcpServerService = mcpServerService;
        this.modelService = modelService;
    }

    public AgentResponse createAgent(AgentRequest request) {
        validateRequest(request);

        Agent agent = new Agent(request.getName(), request.getSystemPrompt());
        agent.setDescription(request.getDescription());
        
        if (request.getModelProvider() != null && !request.getModelProvider().isEmpty()) {
            // Validate that the model provider exists in CSV
            Optional<ModelResponse> model = modelService.getModelByProvider(request.getModelProvider());
            if (model.isEmpty()) {
                throw new IllegalArgumentException("Model provider not found: " + request.getModelProvider());
            }
            agent.setModelProvider(request.getModelProvider());
        }
        
        if (request.getMcpServerNames() != null) {
            request.getMcpServerNames().forEach(agent::addMcpServerName);
        }

        Agent saved = agentRepository.save(agent);
        return toResponse(saved);
    }

    public List<AgentResponse> getAllAgents() {
        return agentRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public AgentResponse getAgentById(Long id) {
        Agent agent = agentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));
        return toResponse(agent);
    }

    public AgentResponse updateAgent(Long id, AgentRequest request) {
        validateRequest(request);
        Agent agent = agentRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + id));

        agent.setName(request.getName());
        agent.setDescription(request.getDescription());
        agent.setSystemPrompt(request.getSystemPrompt());

        if (request.getModelProvider() != null && !request.getModelProvider().isEmpty()) {
            // Validate that the model provider exists in CSV
            Optional<ModelResponse> model = modelService.getModelByProvider(request.getModelProvider());
            if (model.isEmpty()) {
                throw new IllegalArgumentException("Model provider not found: " + request.getModelProvider());
            }
            agent.setModelProvider(request.getModelProvider());
        } else {
            agent.setModelProvider(null);
        }

        // replace MCP names
        agent.getMcpServerNames().clear();
        if (request.getMcpServerNames() != null) {
            request.getMcpServerNames().forEach(agent::addMcpServerName);
        }

        Agent updated = agentRepository.save(agent);
        return toResponse(updated);
    }

    public void deleteAgent(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Agent not found with id: " + id);
        }
        agentRepository.deleteById(id);
    }

    public AgentResponse addMcpName(Long agentId, String mcpName) {
        Agent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        agent.addMcpServerName(mcpName);
        Agent updated = agentRepository.save(agent);
        return toResponse(updated);
    }

    public AgentResponse removeMcpName(Long agentId, String mcpName) {
        Agent agent = agentRepository.findById(agentId)
            .orElseThrow(() -> new ResourceNotFoundException("Agent not found with id: " + agentId));
        agent.removeMcpServerName(mcpName);
        Agent updated = agentRepository.save(agent);
        return toResponse(updated);
    }

    private void validateRequest(AgentRequest request) {
        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Agent name is required");
        }
        if (request.getSystemPrompt() == null || request.getSystemPrompt().isBlank()) {
            throw new IllegalArgumentException("System prompt is required");
        }
    }

    private AgentResponse toResponse(Agent agent) {
        List<String> names = new ArrayList<>();
        if (agent.getMcpServerNames() != null) {
            names.addAll(agent.getMcpServerNames());
        }

        AgentResponse response = new AgentResponse(
            agent.getId(),
            agent.getName(),
            agent.getDescription(),
            agent.getSystemPrompt(),
            agent.getModelProvider(),
            names,
            agent.getCreatedAt()
        );

        // compute unmatched names
        Set<String> known = mcpServerService.getAllMcpServers().stream()
            .map(McpServerResponse::getName)
            .map(String::toLowerCase)
            .collect(Collectors.toSet());

        Set<String> unmatched = names.stream()
            .filter(n -> !known.contains(n.toLowerCase()))
            .collect(Collectors.toSet());

        response.setUnmatchedMcpNames(unmatched);
        return response;
    }
}

