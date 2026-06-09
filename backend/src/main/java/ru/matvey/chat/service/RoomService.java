package ru.matvey.chat.service;


import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.matvey.chat.domain.entities.Room;
import ru.matvey.chat.domain.entities.RoomMember;
import ru.matvey.chat.domain.entities.User;
import ru.matvey.chat.domain.entities.enums.AccessType;
import ru.matvey.chat.domain.entities.enums.RoleType;
import ru.matvey.chat.dto.RoomDtos.*;
import ru.matvey.chat.exception.ApiException;
import ru.matvey.chat.repository.RoomMemberRepository;
import ru.matvey.chat.repository.RoomRepository;
import ru.matvey.chat.repository.UserRepository;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service

public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMemberRepository roomMemberRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RoomService(RoomRepository roomRepository, RoomMemberRepository roomMemberRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) { this.roomRepository = roomRepository; this.roomMemberRepository = roomMemberRepository; this.userRepository = userRepository; this.passwordEncoder = passwordEncoder; }


    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(r -> new RoomResponse(r.getId().toString(), r.getName(), r.getDescription(), r.getAccessType(), r.getCreatedBy().getId().toString()))
                .collect(Collectors.toList());
    }

    @Transactional
    public RoomResponse createRoom(CreateRoomRequest req) {
        User user = getCurrentUser();

        if (req.accessType() == AccessType.PROTECTED && (req.password() == null || req.password().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Password is required for PROTECTED rooms");
        }

        Room room = new Room();
        room.setName(req.name());
        room.setDescription(req.description());
        room.setAccessType(req.accessType());
        room.setCreatedBy(user);
        
        if (req.accessType() == AccessType.PROTECTED) {
            room.setPasswordHash(passwordEncoder.encode(req.password()));
        }

        room = roomRepository.save(room);

        RoomMember member = new RoomMember();
        member.setRoom(room);
        member.setUser(user);
        member.setRole(RoleType.OWNER);
        roomMemberRepository.save(member);

        return new RoomResponse(room.getId().toString(), room.getName(), room.getDescription(), room.getAccessType(), user.getId().toString());
    }

    @Transactional
    public void joinRoom(UUID roomId, JoinRoomRequest req) {
        User user = getCurrentUser();
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Room not found"));

        if (roomMemberRepository.existsByRoomIdAndUserIdAndActiveTrue(roomId, user.getId())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Already a member");
        }

        if (room.getAccessType() == AccessType.PROTECTED) {
            if (req.password() == null || !passwordEncoder.matches(req.password(), room.getPasswordHash())) {
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid room password");
            }
        }

        RoomMember member = new RoomMember();
        member.setRoom(room);
        member.setUser(user);
        member.setRole(RoleType.MEMBER);
        roomMemberRepository.save(member);
    }

    public List<RoomMemberResponse> getRoomMembers(UUID roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Room not found");
        }
        
        return roomMemberRepository.findAllByRoomIdAndActiveTrue(roomId).stream()
                .map(m -> new RoomMemberResponse(m.getUser().getId().toString(), m.getUser().getUsername(), m.getRole().name()))
                .collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
    }
}

