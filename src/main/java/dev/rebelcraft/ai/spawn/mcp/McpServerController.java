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
}
