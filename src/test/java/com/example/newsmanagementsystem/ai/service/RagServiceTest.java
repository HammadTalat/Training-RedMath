package com.example.newsmanagementsystem.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.RagSourcesdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class RagServiceTest {

  @Mock
  private VectorStore vectorStore;

  @Mock
  private ChatClient chatClient;

  @Mock
  private ChatClient.ChatClientRequestSpec requestSpec;

  @Mock
  private ChatClient.CallResponseSpec responseSpec;

  @Mock
  private ChatClient.PromptUserSpec userSpec;

  @Mock
  private ChatClient.AdvisorSpec advisorSpec;

  @InjectMocks
  private RagNativeService ragNativeService;

  @Test
  void returnsHelpfulMessageWhenNoDocumentsMatch() {
    // Arrange
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
        .thenAnswer(invocation -> {
          capturedRequest.set(invocation.getArgument(0));
          return List.of();
        });

    // Act
    RagResultdto result = ragNativeService.ask(
        new SearchRequestdto("What is dependency injection?")
    );
    SearchRequest requestSentToStore = capturedRequest.get();
    EmptySearchOutcome actual = new EmptySearchOutcome(
        requestSentToStore.getQuery(),
        requestSentToStore.getTopK(),
        requestSentToStore.getSimilarityThreshold(),
        result
    );

    // Assert
    assertThat(actual).isEqualTo(
        new EmptySearchOutcome(
            "What is dependency injection?",
            3,
            0.1,
            new RagResultdto(
                "What is dependency injection?",
                "No documents found",
                List.of()
            )
        )
    );
  }

  @Test
  void buildsAnswerAndSourcesFromMatchingDocuments() {
    // Arrange
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    AtomicReference<String> capturedContext = new AtomicReference<>();
    AtomicReference<String> capturedQuestion = new AtomicReference<>();
    AtomicReference<String> capturedMemoryId = new AtomicReference<>();
    List<Document> documents = List.of(
        document(
            "doc-1",
            "Dependency injection supplies an object's dependencies.",
            "Spring Guide",
            "1",
            0.94
        ),
        document(
            "doc-2",
            "Constructor injection makes required dependencies explicit.",
            "Testing Guide",
            2,
            0.86
        )
    );
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
        .thenAnswer(invocation -> {
          capturedRequest.set(invocation.getArgument(0));
          return documents;
        });
    prepareChatClient(capturedContext, capturedQuestion, capturedMemoryId);

    // Act
    RagResultdto result = ragNativeService.ask(
        new SearchRequestdto("What is dependency injection?")
    );
    SearchRequest requestSentToStore = capturedRequest.get();
    RagInteraction actual = new RagInteraction(
        requestSentToStore.getQuery(),
        requestSentToStore.getTopK(),
        requestSentToStore.getSimilarityThreshold(),
        capturedContext.get(),
        capturedQuestion.get(),
        capturedMemoryId.get(),
        result
    );

    // Assert
    assertThat(actual).isEqualTo(expectedRagInteraction());
  }

  @Test
  void convertsStringChunkNumberToInteger() {
    Integer chunkNumber = RagNativeService.convertChunkNumber("4");

    assertThat(chunkNumber).isEqualTo(4);
  }

  @Test
  void acceptsNumericChunkNumber() {
    Integer chunkNumber = RagNativeService.convertChunkNumber(4);

    assertThat(chunkNumber).isEqualTo(4);
  }

  @Test
  void returnsNullWhenChunkNumberIsMissing() {
    Integer chunkNumber = RagNativeService.convertChunkNumber(null);

    assertThat(chunkNumber).isNull();
  }

  private void prepareChatClient(
      AtomicReference<String> capturedContext,
      AtomicReference<String> capturedQuestion,
      AtomicReference<String> capturedMemoryId
  ) {
    when(chatClient.prompt()).thenReturn(requestSpec);
    when(requestSpec.system(anyString())).thenReturn(requestSpec);
    when(requestSpec.user(anyUserConsumer())).thenAnswer(invocation -> {
      Consumer<ChatClient.PromptUserSpec> user = invocation.getArgument(0);
      user.accept(userSpec);
      return requestSpec;
    });
    when(userSpec.text(anyString())).thenReturn(userSpec);
    when(userSpec.param(eq("Context"), any())).thenAnswer(invocation -> {
      capturedContext.set(invocation.getArgument(1));
      return userSpec;
    });
    when(userSpec.param(eq("Question"), any())).thenAnswer(invocation -> {
      capturedQuestion.set(invocation.getArgument(1));
      return userSpec;
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
    when(responseSpec.content()).thenReturn(
        "Dependency injection provides required collaborators."
    );
  }

  private static Document document(
      String id,
      String text,
      String title,
      Object chunkNumber,
      double score
  ) {
    return Document.builder()
        .id(id)
        .text(text)
        .metadata(
            Map.of(
                "title", title,
                "chunk_number", chunkNumber
            )
        )
        .score(score)
        .build();
  }

  private static RagInteraction expectedRagInteraction() {
    return new RagInteraction(
        "What is dependency injection?",
        3,
        0.1,
        """
        Dependency injection supplies an object's dependencies.
        Constructor injection makes required dependencies explicit.
        """,
        "What is dependency injection?",
        "default",
        new RagResultdto(
            "What is dependency injection?",
            "Dependency injection provides required collaborators.",
            List.of(
                new RagSourcesdto(
                    "Spring Guide",
                    1,
                    0.94,
                    "Dependency injection supplies an object's dependencies."
                ),
                new RagSourcesdto(
                    "Testing Guide",
                    2,
                    0.86,
                    "Constructor injection makes required dependencies explicit."
                )
            )
        )
    );
  }

  private static Consumer<ChatClient.PromptUserSpec> anyUserConsumer() {
    return any();
  }

  private static Consumer<ChatClient.AdvisorSpec> anyAdvisorConsumer() {
    return any();
  }

  private record EmptySearchOutcome(
      String query,
      int topK,
      double similarityThreshold,
      RagResultdto result
  ) {
  }

  private record RagInteraction(
      String query,
      int topK,
      double similarityThreshold,
      String context,
      String question,
      String memoryId,
      RagResultdto result
  ) {
  }
}
