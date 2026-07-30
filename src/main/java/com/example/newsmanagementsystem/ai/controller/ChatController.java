package com.example.newsmanagementsystem.ai.controller;

import com.example.newsmanagementsystem.ai.dto.ChatRequest;
import com.example.newsmanagementsystem.ai.dto.ChatResponse;
import com.example.newsmanagementsystem.ai.service.ChatService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/chats")
public class ChatController {

    private final ChatService chatService;

    ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<Object> chat(
            @PathVariable String conversationId,
            @RequestBody ChatRequest request,
            Authentication authentication
    ) {
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest()
                    .body("Message must not be blank");
        }

        try {

            String answer = chatService.chat(
                    authentication.getName(),
                    conversationId,
                    request.message()
            );

            return ResponseEntity.ok(
                    new ChatResponse(conversationId, answer)
            );
        } catch (RuntimeException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(
                            "The AI service is currently unavailable. "
                                    + "Please try again later."
                    );
        }
    }

    @DeleteMapping("/{conversationId}")
    public ResponseEntity<Void> clearConversation(
            @PathVariable String conversationId,
            Authentication authentication
    ) {
        chatService.clearConversation(
                authentication.getName(),
                conversationId
        );

        return ResponseEntity.noContent().build();
    }
}
