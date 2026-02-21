package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Primary
@Service
@Transactional
public class JpaMessagingService implements MessagingService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;

    public JpaMessagingService(MessageRepository messageRepository,
                               ChatRepository chatRepository,
                               ParticipantRepository participantRepository) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
    }

    @Override
    public boolean sendMessage(String chatId, Message message) {
        Optional<Chat> chatOpt;
        try {
            chatOpt = chatRepository.findById(Long.parseLong(chatId));
        } catch (NumberFormatException e) {
            return false;
        }
        if (chatOpt.isEmpty()) {
            return false;
        }
        Chat chat = chatOpt.get();

        String authorId = message.getPendingAuthorId() != null
                ? message.getPendingAuthorId()
                : (message.getAuthor() != null ? message.getAuthor().getId() : null);

        if (authorId != null) {
            try {
                participantRepository.findById(Long.parseLong(authorId))
                        .ifPresent(message::setAuthor);
            } catch (NumberFormatException e) {
                // author not resolved; proceed without
            }
        }

        message.setChat(chat);
        messageRepository.save(message);
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Message> getMessages(String chatId, PaginationOptions paginationOptions) {
        Optional<Chat> chatOpt;
        try {
            chatOpt = chatRepository.findById(Long.parseLong(chatId));
        } catch (NumberFormatException e) {
            return Collections.emptyList();
        }
        if (chatOpt.isEmpty()) {
            return Collections.emptyList();
        }
        int page = paginationOptions != null ? paginationOptions.getPage() : 0;
        int size = paginationOptions != null ? paginationOptions.getSize() : 20;
        Pageable pageable = PageRequest.of(page, size);
        return messageRepository.findByChatOrderByTimestampSentAsc(chatOpt.get(), pageable);
    }

    @Override
    public Message updateMessage(String messageId, String content) {
        try {
            Long id = Long.parseLong(messageId);
            Message message = messageRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Message not found with id: " + messageId));
            message.setContent(content);
            return messageRepository.save(message);
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Message not found with id: " + messageId);
        }
    }

    @Override
    public boolean deleteMessage(String messageId) {
        try {
            Long id = Long.parseLong(messageId);
            if (messageRepository.existsById(id)) {
                messageRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
