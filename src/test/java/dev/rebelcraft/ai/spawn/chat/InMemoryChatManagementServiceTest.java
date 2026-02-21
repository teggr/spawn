package dev.rebelcraft.ai.spawn.chat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class InMemoryChatManagementServiceTest {

    @Autowired
    private ChatManagementService chatManagementService;

    @Autowired
    private ParticipantService participantService;

    @Test
    void shouldCreateChat() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Participant bob = participantService.createParticipant("Bob", null, null);

        Chat chat = chatManagementService.createChat(List.of(alice, bob));

        assertThat(chat.getId()).isNotNull();
        assertThat(chat.getParticipantIds()).containsExactlyInAnyOrder(alice.getId(), bob.getId());
        assertThat(chat.getTimestampCreated()).isNotNull();
    }

    @Test
    void shouldGetChat() {
        Participant participant = participantService.createParticipant("Eve", null, null);
        Chat created = chatManagementService.createChat(List.of(participant));

        Optional<Chat> retrieved = chatManagementService.getChat(created.getId());

        assertThat(retrieved).isPresent();
        assertThat(retrieved.get().getId()).isEqualTo(created.getId());
    }

    @Test
    void shouldReturnEmptyWhenChatNotFound() {
        Optional<Chat> result = chatManagementService.getChat("nonexistent-id");
        assertThat(result).isEmpty();
    }

    @Test
    void shouldDeleteChat() {
        Participant participant = participantService.createParticipant("Frank", null, null);
        Chat chat = chatManagementService.createChat(List.of(participant));

        boolean deleted = chatManagementService.deleteChat(chat.getId());
        assertThat(deleted).isTrue();

        assertThat(chatManagementService.getChat(chat.getId())).isEmpty();
    }

    @Test
    void shouldReturnFalseWhenDeletingNonexistentChat() {
        boolean result = chatManagementService.deleteChat("nonexistent-id");
        assertThat(result).isFalse();
    }

    @Test
    void shouldAddParticipantToChat() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        Participant grace = participantService.createParticipant("Grace", null, null);
        boolean added = chatManagementService.addParticipantToChat(chat.getId(), grace);

        assertThat(added).isTrue();
        assertThat(chatManagementService.getChat(chat.getId()).get().getParticipantIds())
                .contains(alice.getId(), grace.getId());
    }

    @Test
    void shouldReturnFalseWhenAddingParticipantToNonexistentChat() {
        Participant participant = participantService.createParticipant("Heidi", null, null);
        boolean result = chatManagementService.addParticipantToChat("nonexistent-id", participant);
        assertThat(result).isFalse();
    }

    @Test
    void shouldRemoveParticipantFromChat() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Participant bob = participantService.createParticipant("Bob", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice, bob));

        boolean removed = chatManagementService.removeParticipantFromChat(chat.getId(), bob.getId());

        assertThat(removed).isTrue();
        assertThat(chatManagementService.getChat(chat.getId()).get().getParticipantIds())
                .containsOnly(alice.getId());
    }

    @Test
    void shouldReturnFalseWhenRemovingParticipantFromNonexistentChat() {
        boolean result = chatManagementService.removeParticipantFromChat("nonexistent-id", "some-id");
        assertThat(result).isFalse();
    }

    @Test
    void shouldMarkLastSeenMessage() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        boolean marked = chatManagementService.markLastSeenMessage(chat.getId(), alice.getId(), "msg-42");

        assertThat(marked).isTrue();
        assertThat(chatManagementService.getLastSeenMessageId(chat.getId(), alice.getId()))
                .isPresent()
                .hasValue("msg-42");
    }

    @Test
    void shouldUpdateLastSeenMessageForParticipant() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        chatManagementService.markLastSeenMessage(chat.getId(), alice.getId(), "msg-1");
        chatManagementService.markLastSeenMessage(chat.getId(), alice.getId(), "msg-5");

        assertThat(chatManagementService.getLastSeenMessageId(chat.getId(), alice.getId()))
                .hasValue("msg-5");
    }

    @Test
    void shouldReturnFalseWhenMarkingLastSeenForNonexistentChat() {
        boolean result = chatManagementService.markLastSeenMessage("nonexistent-id", "some-participant", "some-msg");
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnFalseWhenMarkingLastSeenForNonParticipant() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        boolean result = chatManagementService.markLastSeenMessage(chat.getId(), "non-participant-id", "some-msg");
        assertThat(result).isFalse();
    }

    @Test
    void shouldReturnEmptyLastSeenMessageWhenNoneSet() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        assertThat(chatManagementService.getLastSeenMessageId(chat.getId(), alice.getId())).isEmpty();
    }

    @Test
    void shouldReturnEmptyLastSeenMessageForNonexistentChat() {
        assertThat(chatManagementService.getLastSeenMessageId("nonexistent-id", "some-participant")).isEmpty();
    }
}
