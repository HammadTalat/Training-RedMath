package com.example.newsmanagementsystem.ai.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @Mock
    private ChatClient chatClient;

    @Mock
    private ChatMemory chatMemory;

    @InjectMocks
    private ChatService chatService;

    @Test
    void usesHammadsUsernameInTheMemoryId() {
        chatService.clearConversation("hammad", "spring-learning");

        verify(chatMemory).clear("hammad:spring-learning");
    }

    @Test
    void usesAlisUsernameInTheMemoryId() {
        chatService.clearConversation("ali", "spring-learning");

        verify(chatMemory).clear("ali:spring-learning");
    }
}
