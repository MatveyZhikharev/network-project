package ru.matvey.chat.domain.entities;
import jakarta.persistence.*;
import ru.matvey.chat.domain.entities.enums.MessageType;
import java.time.Instant;
import java.util.UUID;
@Entity
@Table(name = "messages", indexes = {@Index(name = "idx_message_room_created", columnList = "room_id, created_at")})
public class Message {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) @JoinColumn(name = "room_id", nullable = false) private Room room;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) @JoinColumn(name = "sender_id") private User sender;
    @Column(nullable = false, columnDefinition = "TEXT") private String content;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private MessageType messageType;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    public Message() {}
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public Room getRoom() { return room; }
    public void setRoom(Room room) { this.room = room; }
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
