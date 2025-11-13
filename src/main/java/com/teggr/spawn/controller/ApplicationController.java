package com.teggr.spawn.controller;

import com.teggr.spawn.dto.ApplicationRequest;
import com.teggr.spawn.dto.ApplicationResponse;
import com.teggr.spawn.dto.McpServerResponse;
import com.teggr.spawn.dto.ModelResponse;
import com.teggr.spawn.service.ApplicationService;
import com.teggr.spawn.service.McpServerService;
import com.teggr.spawn.service.ModelService;
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
                                   @RequestParam(required = false) String modelId,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelId != null && !modelId.isEmpty()) {
                request.setModelId(Long.parseLong(modelId));
            }
            applicationService.createApplication(request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("modelId", modelId);
            model.addAttribute("models", modelService.getAllModels());
            return "applicationFormPage";
        }
    }

    @GetMapping("/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        List<McpServerResponse> allServers = mcpServerService.getAllMcpServers();
        
        // Filter out servers that are already associated
        Set<Long> associatedServerIds = app.getMcpServers() != null ? 
            app.getMcpServers().stream().map(McpServerResponse::getId).collect(Collectors.toSet()) :
            Set.of();
        
        List<McpServerResponse> availableServers = allServers.stream()
            .filter(server -> !associatedServerIds.contains(server.getId()))
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
        model.addAttribute("modelId", app.getModel() != null ? app.getModel().getId().toString() : "");
        model.addAttribute("models", models);
        return "applicationFormPage";
    }

    @PostMapping("/{id}")
    public String updateApplication(@PathVariable Long id,
                                   @RequestParam String name,
                                   @RequestParam(required = false) String modelId,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            if (modelId != null && !modelId.isEmpty()) {
                request.setModelId(Long.parseLong(modelId));
            }
            applicationService.updateApplication(id, request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("applicationId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("modelId", modelId);
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
                              @RequestParam Long mcpServerId) {
        applicationService.addMcpServerToApplication(applicationId, mcpServerId);
        return "redirect:/applications/" + applicationId;
    }

    @PostMapping("/{applicationId}/mcp-servers/{mcpServerId}/remove")
    public String removeMcpServer(@PathVariable Long applicationId,
                                 @PathVariable Long mcpServerId) {
        applicationService.removeMcpServerFromApplication(applicationId, mcpServerId);
        return "redirect:/applications/" + applicationId;
    }
}
