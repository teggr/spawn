package com.teggr.spawn.service;

import com.teggr.spawn.dto.*;
import com.teggr.spawn.model.Application;
import com.teggr.spawn.model.McpServer;
import com.teggr.spawn.model.Model;
import com.teggr.spawn.repository.ApplicationRepository;
import com.teggr.spawn.repository.McpServerRepository;
import com.teggr.spawn.repository.ModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ModelRepository modelRepository;
    private final McpServerRepository mcpServerRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                            ModelRepository modelRepository,
                            McpServerRepository mcpServerRepository) {
        this.applicationRepository = applicationRepository;
        this.modelRepository = modelRepository;
        this.mcpServerRepository = mcpServerRepository;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = new Application(request.getName());
        
        if (request.getModelId() != null) {
            Model model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Model not found with id: " + request.getModelId()));
            application.setModel(model);
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
        
        if (request.getModelId() != null) {
            Model model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Model not found with id: " + request.getModelId()));
            application.setModel(model);
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
        
        if (application.getModel() != null) {
            Model model = application.getModel();
            response.setModel(new ModelResponse(
                model.getId(),
                model.getName(),
                model.getType(),
                model.getDescription()
            ));
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
