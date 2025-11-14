package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.agents.AgentService;
import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerService;
import dev.rebelcraft.ai.spawn.models.ModelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ModelService modelService;
    private final AgentService agentService;
    private final McpServerService mcpServerService;

    public ApplicationController(ApplicationService applicationService, 
                                ModelService modelService,
                                AgentService agentService,
                                McpServerService mcpServerService) {
        this.applicationService = applicationService;
        this.modelService = modelService;
        this.agentService = agentService;
        this.mcpServerService = mcpServerService;
    }

    @GetMapping
    public String listApplications(Model model) {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        model.addAttribute("applications", applications);
        return "applicationsListPage";
    }

    @GetMapping("/new")
    public String newApplicationForm(Model model) {
        List<ModelResponse> models = modelService.getAllModels();
        List<AgentResponse> agents = agentService.getAllAgents();
        model.addAttribute("models", models);
        model.addAttribute("agents", agents);
        return "applicationFormPage";
    }

    @PostMapping
    public String createApplication(@RequestParam String name,
                                   @RequestParam(required = false) List<String> modelProviders,
                                   @RequestParam(required = false) List<String> agentNames,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelProviders != null && !modelProviders.isEmpty()) {
                request.setModelProviders(new HashSet<>(modelProviders));
            }
            if (agentNames != null && !agentNames.isEmpty()) {
                request.setAgentNames(new HashSet<>(agentNames));
            }
            applicationService.createApplication(request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("selectedModelProviders", modelProviders != null ? modelProviders : List.of());
            model.addAttribute("selectedAgentNames", agentNames != null ? agentNames : List.of());
            model.addAttribute("models", modelService.getAllModels());
            model.addAttribute("agents", agentService.getAllAgents());
            return "applicationFormPage";
        }
    }

    @GetMapping("/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        
        // Get all available models and filter out associated ones
        List<ModelResponse> allModels = modelService.getAllModels();
        Set<String> associatedModelProviders = app.getModels() != null ? 
            app.getModels().stream().map(ModelResponse::getProvider).collect(Collectors.toSet()) :
            Set.of();
        List<ModelResponse> availableModels = allModels.stream()
            .filter(m -> !associatedModelProviders.contains(m.getProvider()))
            .collect(Collectors.toList());
        
        // Get all available agents and filter out associated ones
        List<AgentResponse> allAgents = agentService.getAllAgents();
        Set<String> associatedAgentNames = app.getAgents() != null ? 
            app.getAgents().stream().map(AgentResponse::getName).collect(Collectors.toSet()) :
            Set.of();
        List<AgentResponse> availableAgents = allAgents.stream()
            .filter(a -> !associatedAgentNames.contains(a.getName()))
            .collect(Collectors.toList());
        
        // Get all available MCP servers and filter out associated ones
        List<McpServerResponse> allServers = mcpServerService.getAllMcpServers();
        Set<String> associatedServerNames = app.getMcpServers() != null ? 
            app.getMcpServers().stream().map(McpServerResponse::getName).collect(Collectors.toSet()) :
            Set.of();
        List<McpServerResponse> availableServers = allServers.stream()
            .filter(server -> !associatedServerNames.contains(server.getName()))
            .collect(Collectors.toList());
        
        model.addAttribute("application", app);
        model.addAttribute("availableModels", availableModels);
        model.addAttribute("availableAgents", availableAgents);
        model.addAttribute("availableServers", availableServers);
        return "applicationDetailPage";
    }

    @GetMapping("/{id}/edit")
    public String editApplicationForm(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        List<ModelResponse> models = modelService.getAllModels();
        List<AgentResponse> agents = agentService.getAllAgents();
        
        model.addAttribute("applicationId", app.getId().toString());
        model.addAttribute("name", app.getName());
        model.addAttribute("selectedModelProviders", 
            app.getModels() != null ? 
                app.getModels().stream().map(ModelResponse::getProvider).collect(Collectors.toList()) : 
                List.of());
        model.addAttribute("selectedAgentNames", 
            app.getAgents() != null ? 
                app.getAgents().stream().map(AgentResponse::getName).collect(Collectors.toList()) : 
                List.of());
        model.addAttribute("models", models);
        model.addAttribute("agents", agents);
        return "applicationFormPage";
    }

    @PostMapping("/{id}")
    public String updateApplication(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam(required = false) List<String> modelProviders,
                                   @RequestParam(required = false) List<String> agentNames,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelProviders != null && !modelProviders.isEmpty()) {
                request.setModelProviders(new HashSet<>(modelProviders));
            }
            if (agentNames != null && !agentNames.isEmpty()) {
                request.setAgentNames(new HashSet<>(agentNames));
            }
            applicationService.updateApplication(id, request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("applicationId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("selectedModelProviders", modelProviders != null ? modelProviders : List.of());
            model.addAttribute("selectedAgentNames", agentNames != null ? agentNames : List.of());
            model.addAttribute("models", modelService.getAllModels());
            model.addAttribute("agents", agentService.getAllAgents());
            return "applicationFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return "redirect:/applications";
    }

    @PostMapping("/{applicationId}/models/add")
    public String addModel(@PathVariable Long applicationId,
                          @RequestParam String modelProvider) {
        applicationService.addModelToApplication(applicationId, modelProvider);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/models/{modelProvider}/remove")
    public String removeModel(@PathVariable Long applicationId,
                             @PathVariable String modelProvider) {
        applicationService.removeModelFromApplication(applicationId, modelProvider);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/agents/add")
    public String addAgent(@PathVariable Long applicationId,
                          @RequestParam String agentName) {
        applicationService.addAgentToApplication(applicationId, agentName);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/agents/{agentName}/remove")
    public String removeAgent(@PathVariable Long applicationId,
                             @PathVariable String agentName) {
        applicationService.removeAgentFromApplication(applicationId, agentName);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/mcp-servers/add")
    public String addMcpServer(@PathVariable Long applicationId,
                              @RequestParam String mcpServerName) {
        applicationService.addMcpServerToApplication(applicationId, mcpServerName);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/mcp-servers/{mcpServerName}/remove")
    public String removeMcpServer(@PathVariable Long applicationId,
                                 @PathVariable String mcpServerName) {
        applicationService.removeMcpServerFromApplication(applicationId, mcpServerName);
        return "redirect:/applications/" + applicationId;
    }
}
