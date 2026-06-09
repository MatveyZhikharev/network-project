package ru.matvey.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import ru.matvey.chat.domain.entities.enums.AccessType;

public class RoomDtos {

    public record CreateRoomRequest(
            @NotBlank String name,
            String description,
            @NotNull AccessType accessType,
            String password
    ) {}

    public record JoinRoomRequest(
            String password
    ) {}

    public record RoomResponse(
            String id,
            String name,
            String description,
            AccessType accessType,
            String createdById
    ) {}

    public record RoomMemberResponse(
            String userId,
            String username,
            String role
    ) {}
}