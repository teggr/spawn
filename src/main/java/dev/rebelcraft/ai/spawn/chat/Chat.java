package dev.rebelcraft.ai.spawn.chat;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Entity
@Table(name = "chats")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "chat_participants",
            joinColumns = @JoinColumn(name = "chat_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("timestampSent ASC")
    private List<Message> messages = new ArrayList<>();

    @Column(name = "timestamp_created")
    private LocalDateTime timestampCreated;

    @ElementCollection
    @CollectionTable(
            name = "chat_last_seen",
            joinColumns = @JoinColumn(name = "chat_id")
    )
    @MapKeyColumn(name = "participant_id")
    @Column(name = "message_id")
    private Map<String, String> lastSeenMessageIds = new HashMap<>();

    @PrePersist
    protected void onCreate() {
        if (timestampCreated == null) {
            timestampCreated = LocalDateTime.now();
        }
    }

    public Chat() { }

    public String getId() {
        return id != null ? id.toString() : null;
    }

    public Long getLongId() {
        return id;
    }

    public List<Participant> getParticipants() {
        return participants;
    }

    public void setParticipants(List<Participant> participants) {
        this.participants = participants;
    }

    public List<String> getParticipantIds() {
        return participants.stream().map(Participant::getId).collect(Collectors.toList());
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<String> getMessageIds() {
        return messages.stream().map(Message::getId).collect(Collectors.toList());
    }

    public LocalDateTime getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(LocalDateTime timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Map<String, String> getLastSeenMessageIds() {
        return lastSeenMessageIds;
    }

    public void setLastSeenMessageIds(Map<String, String> lastSeenMessageIds) {
        this.lastSeenMessageIds = lastSeenMessageIds;
    }
}
