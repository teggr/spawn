package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class InMemoryMessagingService implements MessagingService {

    private final Map<String, Message> store = new ConcurrentHashMap<>();
    private final ChatManagementService chatManagementService;

    public InMemoryMessagingService(ChatManagementService chatManagementService) {
        this.chatManagementService = chatManagementService;
    }

    @Override
    public boolean sendMessage(String chatId, Message message) {
        Optional<Chat> chat = chatManagementService.getChat(chatId);
        if (chat.isEmpty()) {
            return false;
        }
        String messageId = UUID.randomUUID().toString();
        message.setId(messageId);
        message.setChatId(chatId);
        store.put(messageId, message);
        chat.get().getMessageIds().add(messageId);
        return true;
    }

    @Override
    public List<Message> getMessages(String chatId, PaginationOptions paginationOptions) {
        Optional<Chat> chat = chatManagementService.getChat(chatId);
        if (chat.isEmpty()) {
            return List.of();
        }
        List<String> messageIds = chat.get().getMessageIds();
        int page = paginationOptions != null ? paginationOptions.getPage() : 0;
        int size = paginationOptions != null ? paginationOptions.getSize() : 20;
        int fromIndex = page * size;
        if (fromIndex >= messageIds.size()) {
            return List.of();
        }
        int toIndex = Math.min(fromIndex + size, messageIds.size());
        return messageIds.subList(fromIndex, toIndex).stream()
                .map(store::get)
                .filter(m -> m != null)
                .collect(Collectors.toList());
    }

    @Override
    public Message updateMessage(String messageId, String content) {
        Message message = store.get(messageId);
        if (message == null) {
            throw new ResourceNotFoundException("Message not found with id: " + messageId);
        }
        message.setContent(content);
        return message;
    }

    @Override
    public boolean deleteMessage(String messageId) {
        Message message = store.remove(messageId);
        if (message != null && message.getChatId() != null) {
            chatManagementService.getChat(message.getChatId())
                    .ifPresent(chat -> chat.getMessageIds().remove(messageId));
        }
        return message != null;
    }
}
