package dev.rebelcraft.ai.spawn.chat;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Primary
@Service
@Transactional
public class JpaChatManagementService implements ChatManagementService {

    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;

    public JpaChatManagementService(ChatRepository chatRepository,
                                    ParticipantRepository participantRepository) {
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public Chat createChat(List<Participant> participants) {
        Chat chat = new Chat();
        chat.setTimestampCreated(LocalDateTime.now());
        chat.setParticipants(participants);
        return chatRepository.save(chat);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Chat> getChat(String chatId) {
        try {
            Long id = Long.parseLong(chatId);
            return chatRepository.findById(id);
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Chat> getAllChats() {
        return chatRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Chat> findChatByParticipants(List<String> participantIds) {
        List<Long> longIds = participantIds.stream()
                .map(id -> {
                    try { return Long.parseLong(id); } catch (NumberFormatException e) { return null; }
                })
                .filter(id -> id != null)
                .collect(Collectors.toList());

        if (longIds.size() != participantIds.size()) {
            return Optional.empty();
        }

        List<Chat> candidates = chatRepository.findChatsContainingParticipants(longIds, longIds.size());
        return candidates.stream()
                .filter(c -> c.getParticipants().size() == longIds.size())
                .findFirst();
    }

    @Override
    public boolean deleteChat(String chatId) {
        try {
            Long id = Long.parseLong(chatId);
            if (chatRepository.existsById(id)) {
                chatRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean addParticipantToChat(String chatId, Participant participant) {
        Optional<Chat> chatOpt = getChat(chatId);
        if (chatOpt.isEmpty()) {
            return false;
        }
        Chat chat = chatOpt.get();
        boolean alreadyPresent = chat.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(participant.getId()));
        if (!alreadyPresent) {
            chat.getParticipants().add(participant);
            chatRepository.save(chat);
        }
        return true;
    }

    @Override
    public boolean removeParticipantFromChat(String chatId, String participantId) {
        Optional<Chat> chatOpt = getChat(chatId);
        if (chatOpt.isEmpty()) {
            return false;
        }
        Chat chat = chatOpt.get();
        boolean removed = chat.getParticipants().removeIf(p -> p.getId().equals(participantId));
        if (removed) {
            chatRepository.save(chat);
        }
        return removed;
    }

    @Override
    public boolean markLastSeenMessage(String chatId, String participantId, String messageId) {
        Optional<Chat> chatOpt = getChat(chatId);
        if (chatOpt.isEmpty()) {
            return false;
        }
        Chat chat = chatOpt.get();
        boolean isParticipant = chat.getParticipantIds().contains(participantId);
        if (!isParticipant) {
            return false;
        }
        chat.getLastSeenMessageIds().put(participantId, messageId);
        chatRepository.save(chat);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<String> getLastSeenMessageId(String chatId, String participantId) {
        return getChat(chatId)
                .map(chat -> chat.getLastSeenMessageIds().get(participantId));
    }
}
