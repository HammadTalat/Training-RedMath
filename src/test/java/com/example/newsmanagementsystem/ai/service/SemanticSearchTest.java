package com.example.newsmanagementsystem.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import com.example.newsmanagementsystem.ai.dto.SearchResultdto;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class SemanticSearchTest {

  @Mock
  private VectorStore vectorStore;

  @InjectMocks
  private SemanticSearch semanticSearch;

  @Test
  void searchesForThreeDocumentsAndMapsTheResults() {
    // Arrange
    AtomicReference<SearchRequest> capturedRequest = new AtomicReference<>();
    Document firstDocument = Document.builder()
        .id("doc-1")
        .text("Spring AI connects applications to AI models.")
        .metadata(Map.of("title", "Spring AI"))
        .score(0.91)
        .build();
    Document secondDocument = Document.builder()
        .id("doc-2")
        .text("Vector stores support semantic search.")
        .metadata(Map.of("title", "Vector Search"))
        .score(0.82)
        .build();
    when(vectorStore.similaritySearch(any(SearchRequest.class)))
        .thenAnswer(invocation -> {
          capturedRequest.set(invocation.getArgument(0));
          return List.of(firstDocument, secondDocument);
        });

    // Act
    List<SearchResultdto> results = semanticSearch.search(
        new SearchRequestdto("How does semantic search work?")
    );
    SearchRequest requestSentToStore = capturedRequest.get();
    SearchOutcome actual = new SearchOutcome(
        requestSentToStore.getQuery(),
        requestSentToStore.getTopK(),
        requestSentToStore.getSimilarityThreshold(),
        results
    );

    // Assert
    assertThat(actual).isEqualTo(
        new SearchOutcome(
            "How does semantic search work?",
            3,
            0.0,
            List.of(
                new SearchResultdto(
                    "doc-1",
                    "Spring AI connects applications to AI models.",
                    Map.of("title", "Spring AI"),
                    0.91
                ),
                new SearchResultdto(
                    "doc-2",
                    "Vector stores support semantic search.",
                    Map.of("title", "Vector Search"),
                    0.82
                )
            )
        )
    );
  }

  private record SearchOutcome(
      String query,
      int topK,
      double similarityThreshold,
      List<SearchResultdto> results
  ) {
  }
}
