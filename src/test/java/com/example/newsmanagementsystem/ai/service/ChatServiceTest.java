package com.example.newsmanagementsystem.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

  @Mock
  private ChatClient chatClient;

  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;

  @Mock
  private ChatClient.CallResponseSpec responseSpec;

  @Mock
  private ChatClient.AdvisorSpec advisorSpec;

  @Mock
  private ChatMemory chatMemory;

  @InjectMocks
  private ChatService chatService;

  @Test
  void sendsMessageWithConversationMemoryId() {
    // Arrange
    AtomicReference<String> capturedMessage = new AtomicReference<>();
    AtomicReference<String> capturedMemoryId = new AtomicReference<>();
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.user(anyString())).thenAnswer(invocation -> {
      capturedMessage.set(invocation.getArgument(0));
      return requestSpec;
    });
    when(requestSpec.advisors(anyAdvisorConsumer())).thenAnswer(invocation -> {
      Consumer<ChatClient.AdvisorSpec> advisor = invocation.getArgument(0);
      advisor.accept(advisorSpec);
      return requestSpec;
    });
    when(advisorSpec.param(eq(ChatMemory.CONVERSATION_ID), any()))
        .thenAnswer(invocation -> {
          capturedMemoryId.set(invocation.getArgument(1));
          return advisorSpec;
        });
    when(requestSpec.call()).thenReturn(responseSpec);
    when(responseSpec.content()).thenReturn("Here is a simple explanation.");

    // Act
    String answer = chatService.chat(
        "hammad",
        "spring-learning",
        "Explain dependency injection"
    );
    ChatOutcome actual = new ChatOutcome(
        capturedMessage.get(),
        capturedMemoryId.get(),
        answer
    );

    // Assert
    assertThat(actual).isEqualTo(
        new ChatOutcome(
            "Explain dependency injection",
            "hammad:spring-learning",
            "Here is a simple explanation."
        )
    );
  }

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

  private static Consumer<ChatClient.AdvisorSpec> anyAdvisorConsumer() {
    return any();
  }

  private record ChatOutcome(
      String message,
      String memoryId,
      String answer
  ) {
  }
}
