package dev.rebelcraft.ai.spawn.chat;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByChatOrderByTimestampSentAsc(Chat chat, Pageable pageable);
}
