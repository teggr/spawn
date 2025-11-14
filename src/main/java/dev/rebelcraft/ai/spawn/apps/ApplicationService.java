package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.agents.AgentService;
import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final AgentService agentService;

    public ApplicationService(ApplicationRepository applicationRepository,
                            AgentService agentService) {
        this.applicationRepository = applicationRepository;
        this.agentService = agentService;
    }

    public ApplicationResponse createApplication(ApplicationRequest request) {
        Application application = new Application(request.getName());
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
        
        Application updatedApplication = applicationRepository.save(application);
        return toResponse(updatedApplication);
    }

    public void deleteApplication(Long id) {
        if (!applicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Application not found with id: " + id);
        }
        applicationRepository.deleteById(id);
    }

    public ApplicationResponse addAgentToApplication(Long applicationId, String agentName) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + applicationId));
        
        // Validate that the agent exists
        try {
            agentService.getAllAgents().stream()
                .filter(a -> a.getName().equals(agentName))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Agent not found: " + agentName));
        } catch (Exception e) {
            throw new IllegalArgumentException("Agent not found: " + agentName);
        }
        
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

    private ApplicationResponse toResponse(Application application) {
        ApplicationResponse response = new ApplicationResponse(
            application.getId(),
            application.getName(),
            application.getCreatedAt()
        );
        
        if (application.getAgentNames() != null && !application.getAgentNames().isEmpty()) {
            Set<AgentResponse> agents = application.getAgentNames().stream()
                .map(name -> agentService.getAllAgents().stream()
                    .filter(a -> a.getName().equals(name))
                    .findFirst()
                    .orElse(null))
                .filter(agent -> agent != null)
                .collect(Collectors.toSet());
            response.setAgents(agents);
        }
        
        return response;
    }
}
