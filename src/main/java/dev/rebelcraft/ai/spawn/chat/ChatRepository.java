package dev.rebelcraft.ai.spawn.chat;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c JOIN c.participants p WHERE p.id IN :ids GROUP BY c HAVING COUNT(DISTINCT p) = :size")
    List<Chat> findChatsContainingParticipants(@Param("ids") List<Long> ids, @Param("size") long size);
}
