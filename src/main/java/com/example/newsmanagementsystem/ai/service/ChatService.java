package com.example.newsmanagementsystem.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    ChatService(ChatClient chatClient, ChatMemory chatMemory) {
        this.chatClient = chatClient;
        this.chatMemory = chatMemory;
    }

    public String chat(
            String username,
            String conversationId,
            String message
    ) {
        String memoryId = createMemoryId(username, conversationId);

        return chatClient.prompt()
                .user(message)
                .advisors(advisor -> advisor.param(
                        ChatMemory.CONVERSATION_ID,
                        memoryId
                ))
                .call()
                .content();
    }

    public void clearConversation(String username, String conversationId) {
        String memoryId = createMemoryId(username, conversationId);
        chatMemory.clear(memoryId);
    }

    private String createMemoryId(String username, String conversationId) {
        return username + ":" + conversationId;
    }
}
