package dev.rebelcraft.ai.spawn.apps;

import dev.rebelcraft.ai.spawn.agents.AgentResponse;
import dev.rebelcraft.ai.spawn.agents.AgentService;
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
    private final AgentService agentService;

    public ApplicationController(ApplicationService applicationService, 
                                AgentService agentService) {
        this.applicationService = applicationService;
        this.agentService = agentService;
    }

    @GetMapping
    public String listApplications(Model model) {
        List<ApplicationResponse> applications = applicationService.getAllApplications();
        model.addAttribute("applications", applications);
        return "applicationsListPage";
    }

    @GetMapping("/new")
    public String newApplicationForm(Model model) {
        return "applicationFormPage";
    }

    @PostMapping
    public String createApplication(@RequestParam String name,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            applicationService.createApplication(request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            return "applicationFormPage";
        }
    }

    @GetMapping("/{id}")
    public String viewApplication(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        List<AgentResponse> allAgents = agentService.getAllAgents();
        
        // Filter out agents that are already associated
        Set<String> associatedAgentNames = app.getAgents() != null ? 
            app.getAgents().stream().map(AgentResponse::getName).collect(Collectors.toSet()) :
            Set.of();
        
        List<AgentResponse> availableAgents = allAgents.stream()
            .filter(agent -> !associatedAgentNames.contains(agent.getName()))
            .collect(Collectors.toList());
        
        model.addAttribute("application", app);
        model.addAttribute("availableAgents", availableAgents);
        return "applicationDetailPage";
    }

    @GetMapping("/{id}/edit")
    public String editApplicationForm(@PathVariable Long id, Model model) {
        ApplicationResponse app = applicationService.getApplicationById(id);
        
        model.addAttribute("applicationId", app.getId().toString());
        model.addAttribute("name", app.getName());
        return "applicationFormPage";
    }

    @PostMapping("/{id}")
    public String updateApplication(@PathVariable Long id,
                                   @RequestParam String name,
                                   Model model) {
        try {
            ApplicationRequest request = new ApplicationRequest(name);
            applicationService.updateApplication(id, request);
            return "redirect:/applications";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("applicationId", id.toString());
            model.addAttribute("name", name);
            return "applicationFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return "redirect:/applications";
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
}
