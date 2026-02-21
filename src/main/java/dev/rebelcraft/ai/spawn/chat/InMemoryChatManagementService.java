package dev.rebelcraft.ai.spawn.chat;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryChatManagementService implements ChatManagementService {

    private final Map<String, Chat> store = new ConcurrentHashMap<>();
    private final ParticipantService participantService;

    public InMemoryChatManagementService(ParticipantService participantService) {
        this.participantService = participantService;
    }

    @Override
    public Chat createChat(List<Participant> participants) {
        String id = UUID.randomUUID().toString();
        List<String> participantIds = new ArrayList<>();
        for (Participant participant : participants) {
            participantIds.add(participant.getId());
        }
        Chat chat = new Chat(id, participantIds, new ArrayList<>(), LocalDateTime.now(), null);
        store.put(id, chat);
        return chat;
    }

    @Override
    public Optional<Chat> getChat(String chatId) {
        return Optional.ofNullable(store.get(chatId));
    }

    @Override
    public List<Chat> getAllChats() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Optional<Chat> findChatByParticipants(List<String> participantIds) {
        List<String> sorted = new ArrayList<>(participantIds);
        java.util.Collections.sort(sorted);
        return store.values().stream()
                .filter(chat -> {
                    List<String> chatSorted = new ArrayList<>(chat.getParticipantIds());
                    java.util.Collections.sort(chatSorted);
                    return chatSorted.equals(sorted);
                })
                .findFirst();
    }

    @Override
    public boolean deleteChat(String chatId) {
        return store.remove(chatId) != null;
    }

    @Override
    public boolean addParticipantToChat(String chatId, Participant participant) {
        Chat chat = store.get(chatId);
        if (chat == null) {
            return false;
        }
        if (!chat.getParticipantIds().contains(participant.getId())) {
            chat.getParticipantIds().add(participant.getId());
        }
        return true;
    }

    @Override
    public boolean removeParticipantFromChat(String chatId, String participantId) {
        Chat chat = store.get(chatId);
        if (chat == null) {
            return false;
        }
        return chat.getParticipantIds().remove(participantId);
    }

    @Override
    public boolean markLastSeenMessage(String chatId, String participantId, String messageId) {
        Chat chat = store.get(chatId);
        if (chat == null || !chat.getParticipantIds().contains(participantId)) {
            return false;
        }
        chat.getLastSeenMessageIds().put(participantId, messageId);
        return true;
    }

    @Override
    public Optional<String> getLastSeenMessageId(String chatId, String participantId) {
        Chat chat = store.get(chatId);
        if (chat == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(chat.getLastSeenMessageIds().get(participantId));
    }
}
