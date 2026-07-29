package com.example.newsmanagementsystem.ai.controller.VectorDB;

import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentRequest;
import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentResponse;
import com.example.newsmanagementsystem.ai.service.VectorDB.DocumentStorageService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/rag/documents")
public class DocumentStorageController {

    private final DocumentStorageService documentStorageService;

    public DocumentStorageController(
            DocumentStorageService documentStorageService
    ) {
        this.documentStorageService =
                documentStorageService;
    }

    @PostMapping
    public ResponseEntity<SaveDocumentResponse> saveDocument(
            @Valid @RequestBody
            SaveDocumentRequest request
    ) {

        SaveDocumentResponse response =
                documentStorageService.saveDocument(
                        request
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
