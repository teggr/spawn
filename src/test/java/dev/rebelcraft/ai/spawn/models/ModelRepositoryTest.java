package dev.rebelcraft.ai.spawn.models;
}
    }
        assertFalse(repo.existsByProvider("DoesNotExist"));
        assertTrue(repo.existsByProvider("OpenAI"));

        assertTrue(unknown.isEmpty(), "Lookup for unknown provider should be empty");
        Optional<Model> unknown = repo.findByProvider("DoesNotExist");

        assertTrue(anthropic.isPresent(), "Anthropic provider should be present");
        Optional<Model> anthropic = repo.findByProvider("Anthropic");

        assertEquals("yes", openai.get().getStreaming(), "OpenAI streaming should be 'yes'");
        assertTrue(openai.isPresent(), "OpenAI provider should be present");
        Optional<Model> openai = repo.findByProvider("OpenAI");

        assertTrue(models.size() >= 3, "Expected at least 3 models from the test CSV");
        assertNotNull(models);

        List<Model> models = repo.findAll();
        ModelRepository repo = new ModelRepository();
    void loadModels_fromCsv_shouldContainExpectedProviders() {
    @Test

class ModelRepositoryTest {

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.Test;


