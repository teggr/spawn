package dev.rebelcraft.ai.spawn.chat;

import java.util.List;

public interface MessagingService {

    boolean sendMessage(String chatId, Message message);

    List<Message> getMessages(String chatId, PaginationOptions paginationOptions);

    Message updateMessage(String messageId, String content);

    boolean deleteMessage(String messageId);
}
