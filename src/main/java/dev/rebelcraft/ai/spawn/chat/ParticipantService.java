package dev.rebelcraft.ai.spawn.chat;

import java.util.Map;

public interface ParticipantService {

    Participant createParticipant(String name, String avatarUrl, String role);

    Participant getParticipant(String participantId);

    Participant updateParticipant(String participantId, Map<String, Object> details);

    boolean deleteParticipant(String participantId);
}
