package com.example.newsmanagementsystem.ai.controller;

import com.example.newsmanagementsystem.ai.dto.EmbeddingResponse;
import com.example.newsmanagementsystem.ai.service.EmbeddingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/embeddings")
public class EmbeddingController {

    private final EmbeddingService embeddingService;

    public EmbeddingController(
            EmbeddingService embeddingService
    ) {
        this.embeddingService = embeddingService;
    }

    @GetMapping
    public ResponseEntity<EmbeddingResponse> createEmbedding(
            @RequestParam String text
    ) {

        EmbeddingResponse response =
                embeddingService.createEmbedding(text);

        return ResponseEntity.ok(response);
    }
}