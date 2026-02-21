package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class InMemoryMessagingServiceTest {

    @Autowired
    private MessagingService messagingService;

    @Autowired
    private ChatManagementService chatManagementService;

    @Autowired
    private ParticipantService participantService;

    @Test
    void shouldSendMessage() {
        Participant alice = participantService.createParticipant("Alice", null, null);
        Chat chat = chatManagementService.createChat(List.of(alice));

        Message message = new Message(null, null, alice.getId(), "Hello!", LocalDateTime.now());
        boolean sent = messagingService.sendMessage(chat.getId(), message);

        assertThat(sent).isTrue();
        assertThat(message.getId()).isNotNull();
        assertThat(message.getChatId()).isEqualTo(chat.getId());
        assertThat(chatManagementService.getChat(chat.getId()).get().getMessageIds()).contains(message.getId());
    }

    @Test
    void shouldReturnFalseWhenSendingMessageToNonexistentChat() {
        Message message = new Message(null, null, "author-id", "Hello!", LocalDateTime.now());
        boolean result = messagingService.sendMessage("nonexistent-id", message);
        assertThat(result).isFalse();
    }

    @Test
    void shouldGetMessages() {
        Participant bob = participantService.createParticipant("Bob", null, null);
        Chat chat = chatManagementService.createChat(List.of(bob));

        messagingService.sendMessage(chat.getId(), new Message(null, null, bob.getId(), "Msg 1", LocalDateTime.now()));
        messagingService.sendMessage(chat.getId(), new Message(null, null, bob.getId(), "Msg 2", LocalDateTime.now()));

        List<Message> messages = messagingService.getMessages(chat.getId(), new PaginationOptions(0, 10));
        assertThat(messages).hasSize(2);
        assertThat(messages).extracting(Message::getContent).containsExactlyInAnyOrder("Msg 1", "Msg 2");
    }

    @Test
    void shouldGetMessagesWithPagination() {
        Participant carol = participantService.createParticipant("Carol", null, null);
        Chat chat = chatManagementService.createChat(List.of(carol));

        for (int i = 1; i <= 5; i++) {
            messagingService.sendMessage(chat.getId(), new Message(null, null, carol.getId(), "Msg " + i, LocalDateTime.now()));
        }

        List<Message> page0 = messagingService.getMessages(chat.getId(), new PaginationOptions(0, 3));
        assertThat(page0).hasSize(3);

        List<Message> page1 = messagingService.getMessages(chat.getId(), new PaginationOptions(1, 3));
        assertThat(page1).hasSize(2);
    }

    @Test
    void shouldReturnEmptyListForNonexistentChat() {
        List<Message> messages = messagingService.getMessages("nonexistent-id", new PaginationOptions(0, 10));
        assertThat(messages).isEmpty();
    }

    @Test
    void shouldUpdateMessage() {
        Participant dave = participantService.createParticipant("Dave", null, null);
        Chat chat = chatManagementService.createChat(List.of(dave));

        Message message = new Message(null, null, dave.getId(), "Original", LocalDateTime.now());
        messagingService.sendMessage(chat.getId(), message);

        Message updated = messagingService.updateMessage(message.getId(), "Updated content");
        assertThat(updated.getContent()).isEqualTo("Updated content");
    }

    @Test
    void shouldThrowWhenUpdatingNonexistentMessage() {
        assertThatThrownBy(() -> messagingService.updateMessage("nonexistent-id", "content"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void shouldDeleteMessage() {
        Participant eve = participantService.createParticipant("Eve", null, null);
        Chat chat = chatManagementService.createChat(List.of(eve));

        Message message = new Message(null, null, eve.getId(), "To be deleted", LocalDateTime.now());
        messagingService.sendMessage(chat.getId(), message);
        String messageId = message.getId();

        boolean deleted = messagingService.deleteMessage(messageId);
        assertThat(deleted).isTrue();

        assertThat(chatManagementService.getChat(chat.getId()).get().getMessageIds()).doesNotContain(messageId);
    }

    @Test
    void shouldReturnFalseWhenDeletingNonexistentMessage() {
        boolean result = messagingService.deleteMessage("nonexistent-id");
        assertThat(result).isFalse();
    }
}
