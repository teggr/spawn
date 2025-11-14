package dev.rebelcraft.ai.spawn.models;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @GetMapping
    public String listModels(Model model) {
        List<ModelResponse> models = modelService.getAllModels();
        model.addAttribute("models", models);
        return "modelsListPage";
    }

    @PostMapping("/{provider}/favorite")
    public String addFavorite(@PathVariable String provider) {
        modelService.addFavorite(provider);
        return "redirect:/models";
    }

    @PostMapping("/{provider}/unfavorite")
    public String removeFavorite(@PathVariable String provider) {
        modelService.removeFavorite(provider);
        return "redirect:/models";
    }
}
