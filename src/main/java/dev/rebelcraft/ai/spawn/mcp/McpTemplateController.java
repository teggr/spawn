package dev.rebelcraft.ai.spawn.mcp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/mcp-servers")
public class McpTemplateController {

    private final McpTemplateService templateService;
    private final McpServerService mcpServerService;

    public McpTemplateController(McpTemplateService templateService, McpServerService mcpServerService) {
        this.templateService = templateService;
        this.mcpServerService = mcpServerService;
    }

    @GetMapping("/{serverName}/template")
    public String viewTemplate(@PathVariable String serverName, Model model, RedirectAttributes redirectAttributes) {
        Optional<McpTemplate> templateOpt = templateService.getTemplateForServer(serverName);
        
        if (templateOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Template not found for server: " + serverName);
            return "redirect:/mcp-servers";
        }
        
        McpTemplate template = templateOpt.get();
        
        try {
            // Get template raw JSON (pretty printed)
            String templateRaw = templateService.compileTemplate(template); // Will use as base
            // Get compiled preview with placeholders replaced
            String templateCompiled = templateService.compileTemplate(template);
            
            // Get server info
            Optional<McpServerResponse> serverOpt = mcpServerService.getMcpServerByName(serverName);
            
            model.addAttribute("serverName", serverName);
            model.addAttribute("serverDescription", serverOpt.map(McpServerResponse::getDescription).orElse(""));
            model.addAttribute("templateFilename", templateService.getTemplateFilenameForServer(serverName).orElse(""));
            model.addAttribute("templateRaw", templateRaw);
            model.addAttribute("templateCompiled", templateCompiled);
            model.addAttribute("inputs", template.getInputs());
            
            return "mcpServerTemplatePage";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to load template: " + e.getMessage());
            return "redirect:/mcp-servers";
        }
    }
}
