package ru.matvey.chat.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.matvey.chat.dto.MessageDtos;
import ru.matvey.chat.dto.RoomDtos.*;
import ru.matvey.chat.service.MessageService;
import ru.matvey.chat.service.RoomService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/rooms")

public class RoomController {

    private final RoomService roomService;
    private final MessageService messageService;

    public RoomController(RoomService roomService, MessageService messageService) {
      this.roomService = roomService;
      this.messageService = messageService;
    }


    @GetMapping
    public List<RoomResponse> getAllRooms() {
        return roomService.getAllRooms();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RoomResponse createRoom(@Valid @RequestBody CreateRoomRequest req) {
        return roomService.createRoom(req);
    }

    @PostMapping("/{roomId}/join")
    public void joinRoom(@PathVariable UUID roomId, @RequestBody(required = false) JoinRoomRequest req) {
        roomService.joinRoom(roomId, req != null ? req : new JoinRoomRequest(null));
    }

    @GetMapping("/{roomId}/members")
    public List<RoomMemberResponse> getMembers(@PathVariable UUID roomId) {
        return roomService.getRoomMembers(roomId);
    }

    @GetMapping("/{roomId}/messages")
    public List<MessageDtos.MessageResponse> getMessages(@PathVariable UUID roomId) {
      return messageService.getMessagesByRoom(roomId);
    }
}

