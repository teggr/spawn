package dev.rebelcraft.ai.spawn.chat;

import java.time.LocalDateTime;

public class Message {

    private String id;
    private String chatId;
    private String authorId;
    private String content;
    private LocalDateTime timestampSent;
    private MessageStatus status;

    public Message() { }

    public Message(String id, String chatId, String authorId, String content,
                   LocalDateTime timestampSent, MessageStatus status) {
        this.id = id;
        this.chatId = chatId;
        this.authorId = authorId;
        this.content = content;
        this.timestampSent = timestampSent;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestampSent() {
        return timestampSent;
    }

    public void setTimestampSent(LocalDateTime timestampSent) {
        this.timestampSent = timestampSent;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }
}
