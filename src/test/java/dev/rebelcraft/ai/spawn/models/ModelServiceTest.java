package dev.rebelcraft.ai.spawn.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModelServiceTest {

    private ModelRepository modelRepository;
    private ModelFavoriteRepository favoriteRepository;
    private ModelService modelService;

    @BeforeEach
    void setUp() {
        modelRepository = new ModelRepository(); // loads from test resources
        favoriteRepository = mock(ModelFavoriteRepository.class);
        modelService = new ModelService(modelRepository, favoriteRepository);
    }

    @Test
    void getAllModels_marksFavoritesCorrectly() {
        when(favoriteRepository.findAll()).thenReturn(List.of(new ModelFavorite("OpenAI")));

        List<ModelResponse> responses = modelService.getAllModels();

        assertNotNull(responses);
        assertTrue(responses.stream().anyMatch(r -> r.getProvider().equalsIgnoreCase("OpenAI") && r.isFavorite()));
        assertTrue(responses.stream().anyMatch(r -> r.getProvider().equalsIgnoreCase("Anthropic") && !r.isFavorite()));

        verify(favoriteRepository, times(1)).findAll();
    }

    @Test
    void getModelByProvider_returnsFavoriteFlag() {
        when(favoriteRepository.existsByProvider("OpenAI")).thenReturn(true);

        Optional<ModelResponse> resp = modelService.getModelByProvider("OpenAI");
        assertTrue(resp.isPresent());
        assertTrue(resp.get().isFavorite());

        Optional<ModelResponse> missing = modelService.getModelByProvider("DoesNotExist");
        assertTrue(missing.isEmpty());
    }

    @Test
    void addFavorite_savesWhenModelExists() {
        when(favoriteRepository.existsByProvider("OpenAI")).thenReturn(false);

        modelService.addFavorite("OpenAI");

        ArgumentCaptor<ModelFavorite> captor = ArgumentCaptor.forClass(ModelFavorite.class);
        verify(favoriteRepository, times(1)).save(captor.capture());
        assertEquals("OpenAI", captor.getValue().getProvider());
    }

    @Test
    void addFavorite_throwsWhenModelMissing() {
        assertThrows(IllegalArgumentException.class, () -> modelService.addFavorite("DoesNotExist"));
        verify(favoriteRepository, never()).save(any());
    }

    @Test
    void removeFavorite_deletesByProvider() {
        modelService.removeFavorite("OpenAI");
        verify(favoriteRepository, times(1)).deleteByProvider("OpenAI");
    }
}

