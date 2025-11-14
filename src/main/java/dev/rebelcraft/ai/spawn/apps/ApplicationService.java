package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.agents.AgentService;
import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerService;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.models.ModelService;
import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ModelService modelService;
    private final AgentService agentService;
    private final McpServerService mcpServerService;

    public ApplicationService(ApplicationRepository applicationRepository,
                            ModelService modelService,
                            AgentService agentService,
                            McpServerService mcpServerService) {
        this.applicationRepository = applicationRepository;
        this.modelService = modelService;
        this.agentService = agentService;
        this.mcpServerService = mcpServerService;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = new Application(request.getName());
        
        // Validate and set model providers
        if (request.getModelProviders() != null) {
            for (String provider : request.getModelProviders()) {
                Optional<ModelResponse> model = modelService.getModelByProvider(provider);
                if (model.isEmpty()) {
                    throw new IllegalArgumentException("Model provider not found: " + provider);
                }
                application.addModelProvider(provider);
            }
        }
        
        // Validate and set agent names
        if (request.getAgentNames() != null) {
            for (String agentName : request.getAgentNames()) {
                try {
                    // Validate agent exists by trying to find it by name
                    agentService.getAllAgents().stream()
                        .filter(a -> a.getName().equals(agentName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentName));
                    application.addAgentName(agentName);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Agent not found: " + agentName);
                }
            }
        }
        
        Application savedApplication = applicationRepository.save(application);
        return toResponse(savedApplication);
    }

    public List<ApplicationResponse> getAllApplications() {
        return applicationRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ApplicationResponse getApplicationById(Long id) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        return toResponse(application);
    }

    public ApplicationResponse updateApplication(Long id, ApplicationRequest request) {
        Application application = applicationRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
        
        application.setName(request.getName());
        
        // Replace model providers
        application.getModelProviders().clear();
        if (request.getModelProviders() != null) {
            for (String provider : request.getModelProviders()) {
                Optional<ModelResponse> model = modelService.getModelByProvider(provider);
                if (model.isEmpty()) {
                    throw new IllegalArgumentException("Model provider not found: " + provider);
                }
                application.addModelProvider(provider);
            }
        }
        
        // Replace agent names
        application.getAgentNames().clear();
        if (request.getAgentNames() != null) {
            for (String agentName : request.getAgentNames()) {
                try {
                    agentService.getAllAgents().stream()
                        .filter(a -> a.getName().equals(agentName))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentName));
                    application.addAgentName(agentName);
                } catch (Exception e) {
                    throw new IllegalArgumentException("Agent not found: " + agentName);
                }
            }
        }
        
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }
        applicationRepository.deleteById(id);
    }

    public ApplicationResponse addModelToApplication(Long applicationId, String modelProvider) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Validate that the model provider exists in CSV
        Optional<ModelResponse> model = modelService.getModelByProvider(modelProvider);
        if (model.isEmpty()) {
            throw new IllegalArgumentException("Model provider not found: " + modelProvider);
        }
        
        application.addModelProvider(modelProvider);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse removeModelFromApplication(Long applicationId, String modelProvider) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        application.removeModelProvider(modelProvider);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse addAgentToApplication(Long applicationId, String agentName) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Validate that the agent exists
        agentService.getAllAgents().stream()
            .filter(a -> a.getName().equals(agentName))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentName));
        
        application.addAgentName(agentName);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse removeAgentFromApplication(Long applicationId, String agentName) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        application.removeAgentName(agentName);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse addMcpServerToApplication(Long applicationId, String mcpServerName) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Validate that the MCP server exists in CSV
        Optional<McpServerResponse> mcpServer = mcpServerService.getMcpServerByName(mcpServerName);
        if (mcpServer.isEmpty()) {
            throw new IllegalArgumentException("MCP Server not found: " + mcpServerName);
        }
        
        application.addMcpServerName(mcpServerName);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse removeMcpServerFromApplication(Long applicationId, String mcpServerName) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        application.removeMcpServerName(mcpServerName);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    private ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse(
            application.getId(),
            application.getName(),
            application.getCreatedAt()
        );
        
        // Convert model providers to ModelResponse objects
        if (application.getModelProviders() != null && !application.getModelProviders().isEmpty()) {
            Set<ModelResponse> models = application.getModelProviders().stream()
                .map(provider -> modelService.getModelByProvider(provider))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            response.setModels(models);
        }
        
        // Convert agent names to AgentResponse objects
        if (application.getAgentNames() != null && !application.getAgentNames().isEmpty()) {
            Set<AgentResponse> agents = application.getAgentNames().stream()
                .map(name -> agentService.getAllAgents().stream()
                    .filter(a -> a.getName().equals(name))
                    .findFirst())
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            response.setAgents(agents);
        }
        
        // Convert MCP server names to McpServerResponse objects
        if (application.getMcpServerNames() != null && !application.getMcpServerNames().isEmpty()) {
            Set<McpServerResponse> mcpServers = application.getMcpServerNames().stream()
                .map(name -> mcpServerService.getMcpServerByName(name))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
            response.setMcpServers(mcpServers);
        }
        
        return response;
    }
}
