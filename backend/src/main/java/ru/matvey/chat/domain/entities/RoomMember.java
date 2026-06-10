package ru.matvey.chat.domain.entities;
import jakarta.persistence.*;
import ru.matvey.chat.domain.entities.enums.RoleType;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "room_members", uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "room_id"})})
public class RoomMember {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) @JoinColumn(name = "user_id", nullable = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) @JoinColumn(name = "room_id", nullable = false) private Room room;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private RoleType role;
    @Column(nullable = false, updatable = false) private Instant joinedAt = Instant.now();
    @Column(nullable = false) private boolean active = true;
    public RoomMember() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public RoleType getRole() { return role; }
    public void setRole(RoleType role) { this.role = role; }
    public Instant getJoinedAt() { return joinedAt; }
    public void setJoinedAt(Instant joinedAt) { this.joinedAt = joinedAt; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
