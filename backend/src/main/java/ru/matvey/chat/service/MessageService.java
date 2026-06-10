package ru.matvey.chat.service;


import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matvey.chat.domain.entities.Message;
import ru.matvey.chat.domain.entities.Room;
import ru.matvey.chat.domain.entities.User;
import ru.matvey.chat.domain.entities.enums.MessageType;
import ru.matvey.chat.dto.MessageDtos.MessageResponse;
import ru.matvey.chat.exception.ApiException;
import ru.matvey.chat.repository.MessageRepository;
import ru.matvey.chat.repository.RoomMemberRepository;
import ru.matvey.chat.repository.RoomRepository;
import ru.matvey.chat.repository.UserRepository;

import java.util.List;
import java.util.UUID;

@Service

public class MessageService {

    private final MessageRepository messageRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomMemberRepository roomMemberRepository;

    public MessageService(MessageRepository messageRepository, RoomRepository roomRepository, UserRepository userRepository, RoomMemberRepository roomMemberRepository) { this.messageRepository = messageRepository; this.roomRepository = roomRepository; this.userRepository = userRepository; this.roomMemberRepository = roomMemberRepository; }


    @Transactional
    public MessageResponse processMessage(UUID roomId, String username, String content) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room not found"));

        if (!roomMemberRepository.existsByRoomIdAndUserIdAndActiveTrue(roomId, user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "User is not a member of this room");
        }

        Message message = new Message();
        message.setRoom(room);
        message.setSender(user);
        if (content.contains(".png") || content.contains(".jpg") || content.contains(".jpeg")) content = "\"</p><img href=\"%s\" width=300px height=300px><p>\"".formatted(content);
        message.setContent(content);
        message.setMessageType(MessageType.TEXT);
        
        message = messageRepository.save(message);

        return new MessageResponse(
                message.getId().toString(),
                roomId.toString(),
                user.getId().toString(),
                user.getUsername(),
                message.getContent(),
                message.getMessageType().name(),
                message.getCreatedAt().toString()
        );
    }

  public List<MessageResponse> getMessagesByRoom(UUID roomId, String username) {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));

    if (!roomMemberRepository.existsByRoomIdAndUserIdAndActiveTrue(roomId, user.getId())) {
      throw new ApiException(HttpStatus.FORBIDDEN, "You are not a member of this room");
    }
    return messageRepository.findByRoomIdOrderByCreatedAtAsc(roomId)
        .stream()
        .map(m -> new MessageResponse(
            m.getId().toString(),
            m.getRoom().getId().toString(),
            m.getSender().getId().toString(),
            m.getSender().getUsername(),
            m.getContent(),
            m.getMessageType().toString(),
            m.getCreatedAt().toString()
        ))
        .toList();
  }
}

