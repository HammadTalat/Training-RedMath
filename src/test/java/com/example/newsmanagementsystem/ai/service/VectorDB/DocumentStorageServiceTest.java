package com.example.newsmanagementsystem.ai.service.VectorDB;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;

import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentRequest;
import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentResponse;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;

@ExtendWith(MockitoExtension.class)
class DocumentStorageServiceTest {

  private static final String TITLE = "Spring AI guide";
  private static final String CONTENT =
      """
      Spring AI helps Java developers build useful artificial intelligence features.
      This guide explains document storage, text splitting, metadata, and vector search.
      Each stored chunk keeps enough context to be useful when answering a reader's question.
      """
          .repeat(12);

  @Mock
  private VectorStore vectorStore;

  @InjectMocks
  private DocumentStorageService documentStorageService;

  @Test
  void storesEveryChunkWithDocumentMetadataAndSequentialChunkNumbers() {
    // Arrange: remember the documents passed to the vector store.
    AtomicReference<List<Document>> savedDocuments = new AtomicReference<>();
    doAnswer(invocation -> {
      List<Document> documents = invocation.getArgument(0);
      savedDocuments.set(List.copyOf(documents));
      return null;
    }).when(vectorStore).add(anyList());
    SaveDocumentRequest request = new SaveDocumentRequest(TITLE, CONTENT);

    // Act: save one document.
    SaveDocumentResponse response = documentStorageService.saveDocument(request);

    // Assert: compare one easy-to-read snapshot of the complete result.
    StorageSnapshot actual = StorageSnapshot.from(response, savedDocuments.get());
    StorageSnapshot expected = StorageSnapshot.expected(savedDocuments.get());
    assertThat(actual).isEqualTo(expected);
  }

  private static boolean isUuid(String value) {
    try {
      UUID.fromString(value);
      return true;
    } catch (IllegalArgumentException exception) {
      return false;
    }
  }

  private record StorageSnapshot(
      boolean vectorStoreWasCalled,
      boolean validDocumentId,
      String title,
      boolean responseCountMatches,
      boolean multipleChunksWereStored,
      String message,
      boolean everyDocumentIdMatches,
      boolean everyTitleMatches,
      List<Object> chunkNumbers,
      boolean everyChunkHasText) {

    private static StorageSnapshot from(
        SaveDocumentResponse response, List<Document> documents) {
      List<Document> safeDocuments = documents == null ? List.of() : documents;
      return new StorageSnapshot(
          documents != null,
          isUuid(response.documentId()),
          response.title(),
          response.chunksStored() == safeDocuments.size(),
          safeDocuments.size() > 1,
          response.message(),
          safeDocuments.stream()
              .allMatch(document ->
                  response.documentId().equals(document.getMetadata().get("document_id"))),
          safeDocuments.stream()
              .allMatch(document -> TITLE.equals(document.getMetadata().get("title"))),
          safeDocuments.stream()
              .map(document -> document.getMetadata().get("chunk_number"))
              .toList(),
          safeDocuments.stream()
              .allMatch(document ->
                  document.getText() != null && !document.getText().isBlank()));
    }

    private static StorageSnapshot expected(List<Document> documents) {
      int numberOfDocuments = documents == null ? 0 : documents.size();
      List<Object> expectedChunkNumbers =
          IntStream.rangeClosed(1, numberOfDocuments)
              .mapToObj(number -> (Object) number)
              .toList();
      return new StorageSnapshot(
          true,
          true,
          TITLE,
          true,
          true,
          "Document stored successfully",
          true,
          true,
          expectedChunkNumbers,
          true);
    }
  }
}
