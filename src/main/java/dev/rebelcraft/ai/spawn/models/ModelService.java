package dev.rebelcraft.ai.spawn.models;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ModelService {

    private final List<Model> models;

    public ModelService() {
        this.models = loadModelsFromCsv();
    }

    private List<Model> loadModelsFromCsv() {
        List<Model> loadedModels = new ArrayList<>();
        try {
            ClassPathResource resource = new ClassPathResource("models/models.csv");
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
                
                // Skip header line
                String headerLine = reader.readLine();
                
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] fields = parseCsvLine(line);
                    if (fields.length >= 9) {
                        Model model = new Model(
                            fields[0].trim(), // Provider
                            fields[1].trim(), // Multimodality
                            fields[2].trim(), // Tools/Functions
                            fields[3].trim(), // Streaming
                            fields[4].trim(), // Retry
                            fields[5].trim(), // Observability
                            fields[6].trim(), // Built-in JSON
                            fields[7].trim(), // Local
                            fields[8].trim()  // OpenAI API Compatible
                        );
                        loadedModels.add(model);
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load models from CSV", e);
        }
        return loadedModels;
    }

    /**
     * Parse a CSV line handling quoted fields properly
     */
    private String[] parseCsvLine(String line) {
        List<String> fields = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder currentField = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                fields.add(currentField.toString());
                currentField = new StringBuilder();
            } else {
                currentField.append(c);
            }
        }
        fields.add(currentField.toString());
        
        return fields.toArray(new String[0]);
    }

    public List<ModelResponse> getAllModels() {
        return models.stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    public Optional<ModelResponse> getModelByProvider(String provider) {
        return models.stream()
            .filter(model -> model.getProvider().equalsIgnoreCase(provider))
            .map(this::toResponse)
            .findFirst();
    }

    private ModelResponse toResponse(Model model) {
        return new ModelResponse(
            model.getProvider(),
            model.getMultimodality(),
            model.getToolsFunctions(),
            model.getStreaming(),
            model.getRetry(),
            model.getObservability(),
            model.getBuiltInJson(),
            model.getLocal(),
            model.getOpenAiApiCompatible()
        );
    }
}
