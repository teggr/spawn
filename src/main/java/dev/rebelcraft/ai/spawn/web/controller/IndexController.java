package dev.rebelcraft.ai.spawn.web.controller;

import dev.rebelcraft.ai.spawn.apps.ApplicationResponse;
import dev.rebelcraft.ai.spawn.apps.ApplicationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class IndexController {
    private final ApplicationService applicationService;

    public IndexController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @GetMapping("/")
    public String index(Model model) {
        List<ApplicationResponse> recent = applicationService.getAllApplications();
        // show most recent 5
        recent.sort((a,b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        if (recent.size() > 5) recent = recent.subList(0,5);
        model.addAttribute("recentApplications", recent);
        return "indexPage";
    }
}
