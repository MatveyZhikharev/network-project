package ru.matvey.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.matvey.chat.domain.entities.Message;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId AND m.createdAt < :before ORDER BY m.createdAt DESC")
    List<Message> findHistoryByRoomBefore(
            @Param("roomId") UUID roomId, 
            @Param("before") Instant before, 
            Pageable pageable
    );
    
    @Query("SELECT m FROM Message m WHERE m.room.id = :roomId ORDER BY m.createdAt DESC")
    List<Message> findLatestByRoom(
            @Param("roomId") UUID roomId, 
            Pageable pageable
    );

    List<Message> findByRoomIdOrderByCreatedAtAsc(UUID roomId);
}