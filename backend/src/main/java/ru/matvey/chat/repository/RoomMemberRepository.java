package ru.matvey.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matvey.chat.domain.entities.RoomMember;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    boolean existsByRoomIdAndUserIdAndActiveTrue(UUID roomId, UUID userId);
    Optional<RoomMember> findByRoomIdAndUserId(UUID roomId, UUID userId);
    List<RoomMember> findAllByRoomIdAndActiveTrue(UUID roomId);
}