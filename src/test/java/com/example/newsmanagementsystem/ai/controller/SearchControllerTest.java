package com.example.newsmanagementsystem.ai.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.RagSourcesdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import com.example.newsmanagementsystem.ai.dto.SearchResultdto;
import com.example.newsmanagementsystem.ai.service.RagNativeService;
import com.example.newsmanagementsystem.ai.service.SemanticSearch;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

  private static final String QUESTION = "What happened today?";

  @Mock
  private SemanticSearch semanticSearch;

  @Mock
  private RagNativeService ragNativeService;

  @InjectMocks
  private SearchController searchController;

  @Test
  void returnsTheSemanticSearchResults() {
    // Arrange
    SearchRequestdto request = new SearchRequestdto(QUESTION);
    List<SearchResultdto> expected = List.of(new SearchResultdto(
        "document-1",
        "The article content",
        Map.of("title", "Daily News"),
        0.95
    ));
    when(semanticSearch.search(request)).thenReturn(expected);

    // Act
    ResponseEntity<List<SearchResultdto>> response = searchController.search(request);

    // Assert
    assertThat(response)
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.OK, expected);
  }

  @Test
  void returnsTheAnswerCreatedByTheRagService() {
    // Arrange
    SearchRequestdto request = new SearchRequestdto(QUESTION);
    RagResultdto expected = new RagResultdto(
        QUESTION,
        "A concise answer",
        List.of(new RagSourcesdto("Daily News", 1, 0.95, "The article content"))
    );
    when(ragNativeService.ask(request)).thenReturn(expected);

    // Act
    ResponseEntity<RagResultdto> response = searchController.ask(request);

    // Assert
    assertThat(response)
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.OK, expected);
  }
}
