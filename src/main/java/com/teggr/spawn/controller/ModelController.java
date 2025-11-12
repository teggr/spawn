package com.teggr.spawn.controller;

import com.teggr.spawn.dto.ModelRequest;
import com.teggr.spawn.dto.ModelResponse;
import com.teggr.spawn.service.ModelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    public ResponseEntity<ModelResponse> createModel(@Valid @RequestBody ModelRequest request) {
        ModelResponse response = modelService.createModel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        List<ModelResponse> models = modelService.getAllModels();
        return ResponseEntity.ok(models);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable Long id) {
        ModelResponse response = modelService.getModelById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ModelResponse> updateModel(
            @PathVariable Long id,
            @Valid @RequestBody ModelRequest request) {
        ModelResponse response = modelService.updateModel(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}
