package ru.matvey.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.matvey.chat.domain.entities.User;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}