package dev.rebelcraft.ai.spawn.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chat {

    private String id;
    private List<String> participantIds;
    private List<String> messageIds;
    private LocalDateTime timestampCreated;
    private Map<String, String> metadata;
    private Map<String, String> lastSeenMessageIds;

    public Chat() {
        this.participantIds = new ArrayList<>();
        this.messageIds = new ArrayList<>();
        this.metadata = new HashMap<>();
        this.lastSeenMessageIds = new HashMap<>();
    }

    public Chat(String id, List<String> participantIds, List<String> messageIds,
                LocalDateTime timestampCreated, Map<String, String> metadata) {
        this.id = id;
        this.participantIds = participantIds != null ? participantIds : new ArrayList<>();
        this.messageIds = messageIds != null ? messageIds : new ArrayList<>();
        this.timestampCreated = timestampCreated;
        this.metadata = metadata != null ? metadata : new HashMap<>();
        this.lastSeenMessageIds = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getParticipantIds() {
        return participantIds;
    }

    public void setParticipantIds(List<String> participantIds) {
        this.participantIds = participantIds;
    }

    public List<String> getMessageIds() {
        return messageIds;
    }

    public void setMessageIds(List<String> messageIds) {
        this.messageIds = messageIds;
    }

    public LocalDateTime getTimestampCreated() {
        return timestampCreated;
    }

    public void setTimestampCreated(LocalDateTime timestampCreated) {
        this.timestampCreated = timestampCreated;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Map<String, String> getLastSeenMessageIds() {
        return lastSeenMessageIds;
    }

    public void setLastSeenMessageIds(Map<String, String> lastSeenMessageIds) {
        this.lastSeenMessageIds = lastSeenMessageIds;
    }
}
