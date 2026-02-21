package dev.rebelcraft.ai.spawn.chat;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "messages")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Chat chat;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "author_id")
    private Participant author;

    @Transient
    private String pendingAuthorId;

    @Lob
    private String content;

    @Column(name = "timestamp_sent")
    private LocalDateTime timestampSent;

    public Message() { }

    public Message(String id, String chatId, String authorId, String content,
                   LocalDateTime timestampSent) {
        this.pendingAuthorId = authorId;
        this.content = content;
        this.timestampSent = timestampSent;
    }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public Long getLongId() {
        return id;
    }

    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public String getChatId() {
        return chat != null ? chat.getId() : null;
    }

    public Participant getAuthor() {
        return author;
    }

    public void setAuthor(Participant author) {
        this.author = author;
    }

    public String getAuthorId() {
        return author != null ? author.getId() : pendingAuthorId;
    }

    public void setAuthorId(String authorId) {
        this.pendingAuthorId = authorId;
    }

    public String getPendingAuthorId() {
        return pendingAuthorId;
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
}
