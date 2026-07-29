package com.example.newsmanagementsystem.ai.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.EmbeddingResponse;
import com.example.newsmanagementsystem.ai.service.EmbeddingService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmbeddingControllerTest {

  private static final String TEXT = "A short news article";

  @Mock
  private EmbeddingService embeddingService;

  @InjectMocks
  private EmbeddingController embeddingController;

  @Test
  void returnsTheEmbeddingCreatedByTheService() {
    // Arrange
    EmbeddingResponse expected = new EmbeddingResponse(TEXT, 3, new float[] {0.1F, 0.2F, 0.3F});
    when(embeddingService.createEmbedding(TEXT)).thenReturn(expected);

    // Act
    ResponseEntity<EmbeddingResponse> response =
        embeddingController.createEmbedding(TEXT);

    // Assert
    assertThat(response)
        .extracting(ResponseEntity::getStatusCode, ResponseEntity::getBody)
        .containsExactly(HttpStatus.OK, expected);
  }
}
