package dev.rebelcraft.ai.spawn.chat;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParticipantRepository extends JpaRepository<Participant, Long> {

    Optional<Participant> findByNameAndRole(String name, String role);
}
