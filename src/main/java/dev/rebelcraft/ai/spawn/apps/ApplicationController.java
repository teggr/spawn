package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.models.ModelResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerService;
import dev.rebelcraft.ai.spawn.models.ModelService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final ModelService modelService;
    private final McpServerService mcpServerService;

    public ApplicationController(ApplicationService applicationService, 
                                ModelService modelService,
                                McpServerService mcpServerService) {
        this.applicationService = applicationService;
        this.modelService = modelService;
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
        model.addAttribute("models", models);
        return "applicationFormPage";
    }

    @PostMapping
    public String createApplication(@RequestParam String name,
                                   @RequestParam(required = false) String modelProvider,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelProvider != null && !modelProvider.isEmpty()) {
                request.setModelProvider(modelProvider);
            }
            applicationService.createApplication(request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("modelProvider", modelProvider);
            model.addAttribute("models", modelService.getAllModels());
            return "applicationFormPage";
        }
    }

    @GetMapping("/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        List<McpServerResponse> allServers = mcpServerService.getAllMcpServers();
        
        // Filter out servers that are already associated
        Set<String> associatedServerNames = app.getMcpServers() != null ? 
            app.getMcpServers().stream().map(McpServerResponse::getName).collect(Collectors.toSet()) :
            Set.of();
        
        List<McpServerResponse> availableServers = allServers.stream()
            .filter(server -> !associatedServerNames.contains(server.getName()))
            .collect(Collectors.toList());
        
        model.addAttribute("application", app);
        model.addAttribute("availableServers", availableServers);
        return "applicationDetailPage";
    }

    @GetMapping("/{id}/edit")
    public String editApplicationForm(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        List<ModelResponse> models = modelService.getAllModels();
        
        model.addAttribute("applicationId", app.getId().toString());
        model.addAttribute("name", app.getName());
        model.addAttribute("modelProvider", app.getModel() != null ? app.getModel().getProvider() : "");
        model.addAttribute("models", models);
        return "applicationFormPage";
    }

    @PostMapping("/{id}")
    public String updateApplication(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam(required = false) String modelProvider,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelProvider != null && !modelProvider.isEmpty()) {
                request.setModelProvider(modelProvider);
            }
            applicationService.updateApplication(id, request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("applicationId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("modelProvider", modelProvider);
            model.addAttribute("models", modelService.getAllModels());
            return "applicationFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return "redirect:/applications";
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
