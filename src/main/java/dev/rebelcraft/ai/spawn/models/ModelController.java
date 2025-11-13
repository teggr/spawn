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

    @GetMapping("/new")
    public String newModelForm() {
        return "modelFormPage";
    }

    @PostMapping
    public String createModel(@RequestParam String name, 
                            @RequestParam String type,
                            @RequestParam(required = false) String description,
                            Model model) {
        try {
            ModelRequest request = new ModelRequest(name, type);
            request.setDescription(description);
            modelService.createModel(request);
            return "redirect:/models";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("name", name);
            model.addAttribute("type", type);
            model.addAttribute("description", description);
            return "modelFormPage";
        }
    }

    @GetMapping("/{id}/edit")
    public String editModelForm(@PathVariable Long id, Model model) {
        ModelResponse modelResponse = modelService.getModelById(id);
        model.addAttribute("modelId", modelResponse.getId().toString());
        model.addAttribute("name", modelResponse.getName());
        model.addAttribute("type", modelResponse.getType());
        model.addAttribute("description", modelResponse.getDescription());
        return "modelFormPage";
    }

    @PostMapping("/{id}")
    public String updateModel(@PathVariable Long id,
                            @RequestParam String name,
                            @RequestParam String type,
                            @RequestParam(required = false) String description,
                            Model model) {
        try {
            ModelRequest request = new ModelRequest(name, type);
            request.setDescription(description);
            modelService.updateModel(id, request);
            return "redirect:/models";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("modelId", id.toString());
            model.addAttribute("name", name);
            model.addAttribute("type", type);
            model.addAttribute("description", description);
            return "modelFormPage";
        }
    }

    @PostMapping("/{id}/delete")
    public String deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return "redirect:/models";
    }
}
