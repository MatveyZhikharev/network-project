package ru.matvey.chat.controller;


import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import ru.matvey.chat.dto.MessageDtos.MessageResponse;
import ru.matvey.chat.dto.MessageDtos.SendMessageRequest;
import ru.matvey.chat.service.MessageService;

import java.security.Principal;
import java.util.UUID;

@Controller

public class WebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(MessageService messageService, SimpMessagingTemplate messagingTemplate) { this.messageService = messageService; this.messagingTemplate = messagingTemplate; }


    @MessageMapping("/rooms/{roomId}/send")
    public void sendMessage(
            @DestinationVariable UUID roomId,
            @Payload SendMessageRequest request,
            Principal principal
    ) {
        if (principal == null) {
            return;
        }

        MessageResponse response = messageService.processMessage(roomId, principal.getName(), request.content());

        messagingTemplate.convertAndSend("/topic/rooms/" + roomId, response);
    }
}

