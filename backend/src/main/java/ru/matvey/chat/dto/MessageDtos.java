package ru.matvey.chat.dto;

import jakarta.validation.constraints.NotBlank;

public class MessageDtos {

    public record SendMessageRequest(
            @NotBlank String content
    ) {}

    public record MessageResponse(
            String id,
            String roomId,
            String senderId,
            String senderUsername,
            String content,
            String messageType,
            String createdAt
    ) {}
}