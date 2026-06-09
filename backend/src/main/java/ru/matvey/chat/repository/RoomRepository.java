package ru.matvey.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matvey.chat.domain.entities.Room;

import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
}