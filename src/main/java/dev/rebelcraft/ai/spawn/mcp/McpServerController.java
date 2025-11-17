package dev.rebelcraft.ai.spawn.mcp;

import dev.rebelcraft.ai.spawn.mcp.templates.McpServerExampleConfiguration;
import dev.rebelcraft.ai.spawn.mcp.templates.McpServerExampleConfigurationsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/mcp-servers")
public class McpServerController {

    private final McpServerService mcpServerService;
    private final McpServerExampleConfigurationsService mcpServerExampleConfigurationsService;

    public McpServerController(McpServerService mcpServerService, McpServerExampleConfigurationsService mcpServerExampleConfigurationsService) {
        this.mcpServerService = mcpServerService;
      this.mcpServerExampleConfigurationsService = mcpServerExampleConfigurationsService;
    }

    @GetMapping
    public String listMcpServers(Model model) {
        List<McpServerResponse> servers = mcpServerService.getAllMcpServers();
        model.addAttribute("servers", servers);
        return "mcpServersListPage";
    }

    @PostMapping("/{serverName}/favorite")
    public String addFavorite(@PathVariable String serverName) {
        mcpServerService.addFavorite(serverName);
        return "redirect:/mcp-servers";
    }

    @PostMapping("/{serverName}/unfavorite")
    public String removeFavorite(@PathVariable String serverName) {
        mcpServerService.removeFavorite(serverName);
        return "redirect:/mcp-servers";
    }

  @GetMapping("/{serverName}")
  public String viewConfigurations(@PathVariable String serverName, Model model, RedirectAttributes redirectAttributes) {

    List<McpServerExampleConfiguration> configurationTemplateList = mcpServerExampleConfigurationsService.getExamples(serverName);

    if (configurationTemplateList.isEmpty()) {
      redirectAttributes.addFlashAttribute("error", "Template not found for server: " + serverName);
      return "redirect:/mcp-servers";
    }

    try {

      // Get server info
      Optional<McpServerResponse> serverOpt = mcpServerService.getMcpServerByName(serverName);

      model.addAttribute("mcpServer", serverOpt.get());
      model.addAttribute("configurationTemplateList", configurationTemplateList );

      return "mcpServerPage";
    } catch (Exception e) {
      redirectAttributes.addFlashAttribute("error", "Failed to load template: " + e.getMessage());
      return "redirect:/mcp-servers";
    }
  }
}
