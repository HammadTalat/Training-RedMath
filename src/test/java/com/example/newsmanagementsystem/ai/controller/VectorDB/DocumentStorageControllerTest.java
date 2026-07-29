package com.example.newsmanagementsystem.ai.controller.VectorDB;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentRequest;
import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentResponse;
import com.example.newsmanagementsystem.ai.service.VectorDB.DocumentStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class DocumentStorageControllerTest {

  @Mock
  private DocumentStorageService documentStorageService;

  @InjectMocks
  private DocumentStorageController documentStorageController;

  @Test
  void returnsCreatedResponseAfterTheServiceStoresTheDocument() {
    // Arrange: prepare the request and the service response.
    SaveDocumentRequest request =
        new SaveDocumentRequest("Testing guide", "A beginner-friendly testing guide.");
    SaveDocumentResponse savedDocument =
        new SaveDocumentResponse(
            "document-123",
            "Testing guide",
            2,
            "Document stored successfully");
    when(documentStorageService.saveDocument(request)).thenReturn(savedDocument);

    // Act: call the controller method.
    ResponseEntity<SaveDocumentResponse> response =
        documentStorageController.saveDocument(request);

    // Assert: the endpoint returns HTTP 201 and the service response.
    ResponseEntity<SaveDocumentResponse> expected =
        ResponseEntity.status(HttpStatus.CREATED).body(savedDocument);
    assertThat(response).isEqualTo(expected);
  }
}
