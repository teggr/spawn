package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Primary
@Service
@Transactional
public class JpaMessagingService implements MessagingService {

    private final MessageRepository messageRepository;
    private final ChatRepository chatRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantService participantService;

    public JpaMessagingService(MessageRepository messageRepository,
                               ChatRepository chatRepository,
                               ParticipantRepository participantRepository,
                               ParticipantService participantService) {
        this.messageRepository = messageRepository;
        this.chatRepository = chatRepository;
        this.participantRepository = participantRepository;
        this.participantService = participantService;
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

        // TEMPORARY: Trigger echo responses if message is from current user
        if (authorId != null && authorId.equals(JpaParticipantService.CURRENT_USER_ID)) {
            sendEchoResponses(chatId, message.getContent(), authorId);
        }

        return true;
    }

    // TEMPORARY: Echo response for demo - remove when real AI integration is added
    @Async("chatResponseExecutor")
    public void sendEchoResponses(String chatId, String originalMessage, String originalAuthorId) {
        try {
            // Random delay between 1-3 seconds for more realistic feel
            Random random = new Random();
            int delaySeconds = 1 + random.nextInt(3);
            TimeUnit.SECONDS.sleep(delaySeconds);

            // Get the chat and all participants
            Optional<Chat> chatOpt = chatRepository.findById(Long.parseLong(chatId));
            if (chatOpt.isEmpty()) {
                return;
            }
            Chat chat = chatOpt.get();

            // Get current user name for personalized response
            String currentUserName = "there"; // fallback
            try {
                Participant currentUser = participantService.getParticipant(originalAuthorId);
                currentUserName = currentUser.getName();
            } catch (Exception e) {
                // Use fallback name
            }

            // Send echo from all other participants (not the original author)
            for (String participantId : chat.getParticipantIds()) {
                if (!participantId.equals(originalAuthorId)) {
                    try {
                        Participant responder = participantService.getParticipant(participantId);

                        // Create echo message
                        String echoContent = String.format("Hey %s... i got the message - %s",
                                currentUserName, originalMessage);
                        Message echoMessage = new Message(null, chatId, participantId,
                                echoContent, LocalDateTime.now());
                        echoMessage.setAuthor(responder);
                        echoMessage.setChat(chat);
                        messageRepository.save(echoMessage);

                        // Small delay between multiple participants responding
                        if (chat.getParticipantIds().size() > 2) {
                            TimeUnit.MILLISECONDS.sleep(500 + random.nextInt(1000));
                        }
                    } catch (Exception e) {
                        // Continue with other participants if one fails
                    }
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            // Log error but don't fail the original request
            System.err.println("Error sending echo response: " + e.getMessage());
        }
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
