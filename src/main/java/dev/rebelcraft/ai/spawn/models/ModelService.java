package dev.rebelcraft.ai.spawn.models;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private final ModelRepository modelRepository;
    private final ModelFavoriteRepository favoriteRepository;

    public ModelService(ModelRepository modelRepository, ModelFavoriteRepository favoriteRepository) {
        this.modelRepository = modelRepository;
        this.favoriteRepository = favoriteRepository;
    }

    public List<ModelResponse> getAllModels() {
        // Get all favorites
        Set<String> favoriteProviders = favoriteRepository.findAll().stream()
            .map(ModelFavorite::getProvider)
            .collect(Collectors.toSet());

        return modelRepository.findAll().stream()
            .map(model -> toResponse(model, favoriteProviders.contains(model.getProvider())))
            .collect(Collectors.toList());
    }

    public Optional<ModelResponse> getModelByProvider(String provider) {
        boolean isFavorite = favoriteRepository.existsByProvider(provider);
        return modelRepository.findByProvider(provider)
            .map(model -> toResponse(model, isFavorite));
    }

    public void addFavorite(String provider) {
        // Check if model exists
        boolean modelExists = modelRepository.existsByProvider(provider);

        if (!modelExists) {
            throw new IllegalArgumentException("Model not found: " + provider);
        }

        // Add favorite if not already exists
        if (!favoriteRepository.existsByProvider(provider)) {
            favoriteRepository.save(new ModelFavorite(provider));
        }
    }

    @Transactional
    public void removeFavorite(String provider) {
        favoriteRepository.deleteByProvider(provider);
    }

    private ModelResponse toResponse(Model model, boolean isFavorite) {
        return new ModelResponse(
            model.getProvider(),
            model.getMultimodality(),
            model.getToolsFunctions(),
            model.getStreaming(),
            model.getRetry(),
            model.getObservability(),
            model.getBuiltInJson(),
            model.getLocal(),
            model.getOpenAiApiCompatible(),
            isFavorite
        );
    }
}
