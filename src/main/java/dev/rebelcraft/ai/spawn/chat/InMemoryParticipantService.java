package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class InMemoryParticipantService implements ParticipantService {

    public static String CURRENT_USER_ID;

    private final Map<String, Participant> store = new ConcurrentHashMap<>();

    @PostConstruct
    public void initializeDefaultParticipants() {
        Participant currentUser = createParticipant("You", null, "CURRENT_USER");
        CURRENT_USER_ID = currentUser.getId();
        createParticipant("Lisa Zhang", null, "USER");
        createParticipant("Arcadio Buendia", null, "USER");
        createParticipant("Lee Hao", null, "USER");
    }

    @Override
    public Participant createParticipant(String name, String avatarUrl, String role) {
        String id = UUID.randomUUID().toString();
        Participant participant = new Participant(id, name, avatarUrl, role);
        store.put(id, participant);
        return participant;
    }

    @Override
    public List<Participant> getAllParticipants() {
        return new ArrayList<>(store.values());
    }

    @Override
    public Participant getParticipant(String participantId) {
        Participant participant = store.get(participantId);
        if (participant == null) {
            throw new ResourceNotFoundException("Participant not found with id: " + participantId);
        }
        return participant;
    }

    @Override
    public Participant updateParticipant(String participantId, Map<String, Object> details) {
        Participant participant = getParticipant(participantId);
        if (details.containsKey("name")) {
            participant.setName((String) details.get("name"));
        }
        if (details.containsKey("avatarUrl")) {
            participant.setAvatarUrl((String) details.get("avatarUrl"));
        }
        if (details.containsKey("role")) {
            participant.setRole((String) details.get("role"));
        }
        return participant;
    }

    @Override
    public boolean deleteParticipant(String participantId) {
        return store.remove(participantId) != null;
    }
}
