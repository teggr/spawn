package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServer;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerRepository;
import dev.rebelcraft.ai.spawn.models.ModelService;
import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ModelService modelService;
    private final McpServerRepository mcpServerRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                            ModelService modelService,
                            McpServerRepository mcpServerRepository) {
        this.applicationRepository = applicationRepository;
        this.modelService = modelService;
        this.mcpServerRepository = mcpServerRepository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = new Application(request.getName());
        
        if (request.getModelProvider() != null && !request.getModelProvider().isEmpty()) {
            // Validate that the model provider exists in CSV
            Optional<ModelResponse> model = modelService.getModelByProvider(request.getModelProvider());
            if (model.isEmpty()) {
                throw new IllegalArgumentException("Model provider not found: " + request.getModelProvider());
            }
            application.setModelProvider(request.getModelProvider());
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
        
        if (request.getModelProvider() != null && !request.getModelProvider().isEmpty()) {
            // Validate that the model provider exists in CSV
            Optional<ModelResponse> model = modelService.getModelByProvider(request.getModelProvider());
            if (model.isEmpty()) {
                throw new IllegalArgumentException("Model provider not found: " + request.getModelProvider());
            }
            application.setModelProvider(request.getModelProvider());
        } else {
            application.setModelProvider(null);
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

    public ApplicationResponse addMcpServerToApplication(Long applicationId, Long mcpServerId) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        McpServer mcpServer = mcpServerRepository.findById(mcpServerId)
            .orElseThrow(() -> new ResourceNotFoundException("MCP Server not found with id: " + mcpServerId));
        
        application.addMcpServer(mcpServer);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public ApplicationResponse removeMcpServerFromApplication(Long applicationId, Long mcpServerId) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        McpServer mcpServer = mcpServerRepository.findById(mcpServerId)
            .orElseThrow(() -> new ResourceNotFoundException("MCP Server not found with id: " + mcpServerId));
        
        application.removeMcpServer(mcpServer);
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    private ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse(
            application.getId(),
            application.getName(),
            application.getCreatedAt()
        );
        
        if (application.getModelProvider() != null) {
            Optional<ModelResponse> model = modelService.getModelByProvider(application.getModelProvider());
            model.ifPresent(response::setModel);
        }
        
        if (application.getMcpServers() != null) {
            Set<McpServerResponse> mcpServers = application.getMcpServers().stream()
                .map(server -> new McpServerResponse(
                    server.getId(),
                    server.getName(),
                    server.getUrl(),
                    server.getDescription()
                ))
                .collect(Collectors.toSet());
            response.setMcpServers(mcpServers);
        }
        
        return response;
    }
}
