package dev.rebelcraft.ai.spawn.chat;

import dev.rebelcraft.ai.spawn.utils.ResourceNotFoundException;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Primary
@Service
@Transactional
public class JpaParticipantService implements ParticipantService {

    public static String CURRENT_USER_ID;

    private final ParticipantRepository participantRepository;

    public JpaParticipantService(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    @PostConstruct
    public void initializeDefaultParticipants() {
        if (participantRepository.count() == 0) {
            Participant currentUser = createParticipant("You", null, "CURRENT_USER");
            CURRENT_USER_ID = currentUser.getId();
            createParticipant("Lisa Zhang", null, "USER");
            createParticipant("Arcadio Buendia", null, "USER");
            createParticipant("Lee Hao", null, "USER");
        } else {
            Participant currentUser = participantRepository.findByNameAndRole("You", "CURRENT_USER")
                    .orElseGet(() -> createParticipant("You", null, "CURRENT_USER"));
            CURRENT_USER_ID = currentUser.getId();
        }
    }

    @Override
    public Participant createParticipant(String name, String avatarUrl, String role) {
        Participant participant = new Participant(null, name, avatarUrl, role);
        return participantRepository.save(participant);
    }

    @Override
    public List<Participant> getAllParticipants() {
        return participantRepository.findAll();
    }

    @Override
    public Participant getParticipant(String participantId) {
        try {
            Long id = Long.parseLong(participantId);
            return participantRepository.findById(id)
                    .orElseThrow(() -> new ResourceNotFoundException("Participant not found with id: " + participantId));
        } catch (NumberFormatException e) {
            throw new ResourceNotFoundException("Participant not found with id: " + participantId);
        }
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
        return participantRepository.save(participant);
    }

    @Override
    public boolean deleteParticipant(String participantId) {
        try {
            Long id = Long.parseLong(participantId);
            if (participantRepository.existsById(id)) {
                participantRepository.deleteById(id);
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
