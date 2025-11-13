package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/mcp-servers")
public class McpServerController {

    private final McpServerService mcpServerService;

    public McpServerController(McpServerService mcpServerService) {
        this.mcpServerService = mcpServerService;
    }

    @GetMapping
    public String listMcpServers(Model model) {
        List<McpServerResponse> servers = mcpServerService.getAllMcpServers();
        model.addAttribute("servers", servers);
        return "mcpServersListPage";
    }

    @GetMapping("/new")
    public String newMcpServerForm() {
        return "mcpServerFormPage";
    }

    @PostMapping
    public String createMcpServer(@RequestParam String name,
                                 @RequestParam String url,
                                 @RequestParam(required = false) String description,
                                 Model model) {
        try {
            McpServerRequest request = new McpServerRequest(name, url);
            request.setDescription(description);
            mcpServerService.createMcpServer(request);
            return "redirect:/mcp-servers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("url", url);
            model.addAttribute("description", description);
            return "mcpServerFormPage";
        }
    }

    @GetMapping("/{id}/edit")
    public String editMcpServerForm(@PathVariable Long id, Model model) {
        McpServerResponse serverResponse = mcpServerService.getMcpServerById(id);
        model.addAttribute("serverId", serverResponse.getId().toString());
        model.addAttribute("name", serverResponse.getName());
        model.addAttribute("url", serverResponse.getUrl());
        model.addAttribute("description", serverResponse.getDescription());
        return "mcpServerFormPage";
    }

    @PostMapping("/{id}")
    public String updateMcpServer(@PathVariable Long id,
                                 @RequestParam String name,
                                 @RequestParam String url,
                                 @RequestParam(required = false) String description,
                                 Model model) {
        try {
            McpServerRequest request = new McpServerRequest(name, url);
            request.setDescription(description);
            mcpServerService.updateMcpServer(id, request);
            return "redirect:/mcp-servers";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("serverId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("url", url);
            model.addAttribute("description", description);
            return "mcpServerFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteMcpServer(@PathVariable Long id) {
        mcpServerService.deleteMcpServer(id);
        return "redirect:/mcp-servers";
    }
}
