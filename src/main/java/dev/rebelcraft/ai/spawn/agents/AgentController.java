package dev.rebelcraft.ai.spawn.agents;

import dev.rebelcraft.ai.spawn.mcp.McpServerResponse;
import dev.rebelcraft.ai.spawn.mcp.McpServerService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/agents")
public class AgentController {

    private final AgentService agentService;
    private final McpServerService mcpServerService;

    public AgentController(AgentService agentService, McpServerService mcpServerService) {
        this.agentService = agentService;
        this.mcpServerService = mcpServerService;
    }

    @GetMapping
    public String listAgents(Model model) {
        List<AgentResponse> agents = agentService.getAllAgents();
        model.addAttribute("agents", agents);
        return "agentsListPage";
    }

    @GetMapping("/new")
    public String newAgentForm(Model model) {
        model.addAttribute("mcpServers", mcpServerService.getAllMcpServers());
        return "agentFormPage";
    }

    @PostMapping
    public String createAgent(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam String systemPrompt,
                              @RequestParam(required = false) List<String> mcpServerNames,
                              Model model) {
        try {
            AgentRequest request = new AgentRequest();
            request.setName(name);
            request.setDescription(description);
            request.setSystemPrompt(systemPrompt);
            if (mcpServerNames != null) request.setMcpServerNames(mcpServerNames);

            agentService.createAgent(request);
            return "redirect:/agents";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("systemPrompt", systemPrompt);
            model.addAttribute("mcpServerNames", mcpServerNames);
            model.addAttribute("mcpServers", mcpServerService.getAllMcpServers());
            return "agentFormPage";
        }
    }

    @GetMapping("/{id}")
    public String viewAgent(@PathVariable Long id, Model model) {
        AgentResponse agent = agentService.getAgentById(id);
        model.addAttribute("agent", agent);
        model.addAttribute("mcpServers", mcpServerService.getAllMcpServers());
        return "agentDetailPage";
    }

    @GetMapping("/{id}/edit")
    public String editAgentForm(@PathVariable Long id, Model model) {
        AgentResponse agent = agentService.getAgentById(id);
        model.addAttribute("agentId", agent.getId().toString());
        model.addAttribute("name", agent.getName());
        model.addAttribute("description", agent.getDescription());
        model.addAttribute("systemPrompt", agent.getSystemPrompt());
        model.addAttribute("mcpServerNames", agent.getMcpServerNames());
        model.addAttribute("mcpServers", mcpServerService.getAllMcpServers());
        return "agentFormPage";
    }

    @PostMapping("/{id}")
    public String updateAgent(@PathVariable Long id,
                              @RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam String systemPrompt,
                              @RequestParam(required = false) List<String> mcpServerNames,
                              Model model) {
        try {
            AgentRequest request = new AgentRequest();
            request.setName(name);
            request.setDescription(description);
            request.setSystemPrompt(systemPrompt);
            if (mcpServerNames != null) request.setMcpServerNames(mcpServerNames);

            agentService.updateAgent(id, request);
            return "redirect:/agents";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("agentId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("description", description);
            model.addAttribute("systemPrompt", systemPrompt);
            model.addAttribute("mcpServerNames", mcpServerNames);
            model.addAttribute("mcpServers", mcpServerService.getAllMcpServers());
            return "agentFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return "redirect:/agents";
    }

    @PostMapping("/{id}/mcp-servers/add")
    public String addMcpServer(@PathVariable Long id,
                               @RequestParam String mcpName) {
        agentService.addMcpName(id, mcpName);
        return "redirect:/agents/" + id;
    }

    @PostMapping("/{id}/mcp-servers/{mcpName}/remove")
    public String removeMcpServer(@PathVariable Long id,
                                  @PathVariable String mcpName) {
        agentService.removeMcpName(id, mcpName);
        return "redirect:/agents/" + id;
    }
}
