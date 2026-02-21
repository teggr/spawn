package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InMemoryParticipantServiceTest {

    @Autowired
    private ParticipantService participantService;

    @Test
    void shouldCreateParticipant() {
        Participant participant = participantService.createParticipant("Alice", "https://example.com/alice.png", "admin");

        assertThat(participant.getId()).isNotNull();
        assertThat(participant.getName()).isEqualTo("Alice");
        assertThat(participant.getAvatarUrl()).isEqualTo("https://example.com/alice.png");
        assertThat(participant.getRole()).isEqualTo("admin");
    }

    @Test
    void shouldGetParticipant() {
        Participant created = participantService.createParticipant("Bob", "https://example.com/bob.png", null);

        Participant retrieved = participantService.getParticipant(created.getId());

        assertThat(retrieved.getId()).isEqualTo(created.getId());
        assertThat(retrieved.getName()).isEqualTo("Bob");
    }

    @Test
    void shouldThrowWhenParticipantNotFound() {
        assertThatThrownBy(() -> participantService.getParticipant("nonexistent-id"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldUpdateParticipant() {
        Participant created = participantService.createParticipant("Charlie", "https://example.com/charlie.png", "user");

        Map<String, Object> details = Map.of("name", "Charlie Updated", "role", "moderator");
        Participant updated = participantService.updateParticipant(created.getId(), details);

        assertThat(updated.getName()).isEqualTo("Charlie Updated");
        assertThat(updated.getRole()).isEqualTo("moderator");
        assertThat(updated.getAvatarUrl()).isEqualTo("https://example.com/charlie.png");
    }

    @Test
    void shouldDeleteParticipant() {
        Participant created = participantService.createParticipant("Dave", null, null);

        boolean deleted = participantService.deleteParticipant(created.getId());
        assertThat(deleted).isTrue();

        assertThatThrownBy(() -> participantService.getParticipant(created.getId()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonexistentParticipant() {
        boolean result = participantService.deleteParticipant("nonexistent-id");
        assertThat(result).isFalse();
    }
}
