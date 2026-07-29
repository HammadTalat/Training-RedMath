package com.example.newsmanagementsystem.ai.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatModel;

class AiConfigTest {

  @Test
  void createsWindowBasedChatMemory() {
    // Arrange
    AiConfig config = new AiConfig();

    // Act
    ChatMemory chatMemory = config.chatMemory();

    // Assert
    assertThat(chatMemory).isInstanceOf(MessageWindowChatMemory.class);
  }

  @Test
  void createsChatClient() {
    // Arrange
    AiConfig config = new AiConfig();
    ChatModel chatModel = mock(ChatModel.class);
    ChatMemory chatMemory = mock(ChatMemory.class);

    // Act
    ChatClient chatClient = config.chatClient(chatModel, chatMemory);

    // Assert
    assertThat(chatClient).isNotNull();
  }
}
