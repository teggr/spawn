package dev.rebelcraft.ai.spawn.models;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ModelRepositoryTest {

    @Test
    void loadModels_fromCsv_shouldContainExpectedProviders() {
        ModelRepository repo = new ModelRepository();
        List<Model> models = repo.findAll();

        assertNotNull(models, "Models list should not be null");
        assertTrue(models.size() >= 3, "Expected at least 3 models from the test CSV");

        Optional<Model> openai = repo.findByProvider("OpenAI");
        assertTrue(openai.isPresent(), "OpenAI provider should be present");
        assertEquals("yes", openai.get().getStreaming(), "OpenAI streaming should be 'yes'");

        Optional<Model> anthropic = repo.findByProvider("Anthropic");
        assertTrue(anthropic.isPresent(), "Anthropic provider should be present");

        Optional<Model> unknown = repo.findByProvider("DoesNotExist");
        assertTrue(unknown.isEmpty(), "Lookup for unknown provider should be empty");

        assertTrue(repo.existsByProvider("OpenAI"));
        assertFalse(repo.existsByProvider("DoesNotExist"));
    }
}
