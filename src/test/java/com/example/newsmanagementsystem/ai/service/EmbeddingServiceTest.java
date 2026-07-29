package com.example.newsmanagementsystem.ai.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.EmbeddingResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.embedding.EmbeddingModel;

@ExtendWith(MockitoExtension.class)
class EmbeddingServiceTest {

  @Mock
  private EmbeddingModel embeddingModel;

  @InjectMocks
  private EmbeddingService embeddingService;

  @Test
  void createsEmbeddingWithTenValuePreview() {
    // Arrange
    float[] fullVector = {
        0.1F, 0.2F, 0.3F, 0.4F, 0.5F, 0.6F,
        0.7F, 0.8F, 0.9F, 1.0F, 1.1F, 1.2F
    };
    when(embeddingModel.embed("spring ai")).thenReturn(fullVector);

    // Act
    EmbeddingResponse response = embeddingService.createEmbedding("spring ai");

    // Assert
    assertThat(response).usingRecursiveComparison().isEqualTo(
        new EmbeddingResponse(
            "spring ai",
            12,
            new float[] {
                0.1F, 0.2F, 0.3F, 0.4F, 0.5F,
                0.6F, 0.7F, 0.8F, 0.9F, 1.0F
            }
        )
    );
  }

  @Test
  void rejectsNullText() {
    // Act and Assert
    assertThatThrownBy(() -> embeddingService.createEmbedding(null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Text must not be empty");
  }

  @Test
  void rejectsBlankText() {
    // Act and Assert
    assertThatThrownBy(() -> embeddingService.createEmbedding("   "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Text must not be empty");
  }
}
