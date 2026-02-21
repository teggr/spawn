package dev.rebelcraft.ai.spawn.chat;

import java.util.List;
import java.util.Optional;

public interface ChatManagementService {

    Chat createChat(List<Participant> participants);

    Optional<Chat> getChat(String chatId);

    boolean deleteChat(String chatId);

    boolean addParticipantToChat(String chatId, Participant participant);

    boolean removeParticipantFromChat(String chatId, String participantId);

    boolean markLastSeenMessage(String chatId, String participantId, String messageId);

    Optional<String> getLastSeenMessageId(String chatId, String participantId);
}
