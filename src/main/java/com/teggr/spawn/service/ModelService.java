package com.teggr.spawn.service;

import com.teggr.spawn.dto.ModelRequest;
import com.teggr.spawn.dto.ModelResponse;
import com.teggr.spawn.model.Model;
import com.teggr.spawn.repository.ModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ModelService {

    private final ModelRepository modelRepository;

    public ModelService(ModelRepository modelRepository) {
        this.modelRepository = modelRepository;
    }

    public ModelResponse createModel(ModelRequest request) {
        Model model = new Model(request.getName(), request.getType());
        model.setDescription(request.getDescription());
        Model savedModel = modelRepository.save(model);
        return toResponse(savedModel);
    }

    public List<ModelResponse> getAllModels() {
        return modelRepository.findAll().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public ModelResponse getModelById(Long id) {
        Model model = modelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Model not found with id: " + id));
        return toResponse(model);
    }

    public ModelResponse updateModel(Long id, ModelRequest request) {
        Model model = modelRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Model not found with id: " + id));
        
        model.setName(request.getName());
        model.setType(request.getType());
        model.setDescription(request.getDescription());
        
        Model updatedModel = modelRepository.save(model);
        return toResponse(updatedModel);
    }

    public void deleteModel(Long id) {
        if (!modelRepository.existsById(id)) {
            throw new ResourceNotFoundException("Model not found with id: " + id);
        }
        modelRepository.deleteById(id);
    }

    private ModelResponse toResponse(Model model) {
        return new ModelResponse(
            model.getId(),
            model.getName(),
            model.getType(),
            model.getDescription()
        );
    }
}
