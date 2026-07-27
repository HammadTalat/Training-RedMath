package com.example.newsmanagementsystem.ai.service;

import com.example.newsmanagementsystem.ai.dto.EmbeddingResponse;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class EmbeddingService {

    private static final int PREVIEW_SIZE = 10;

    private final EmbeddingModel embeddingModel;

    public EmbeddingService(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public EmbeddingResponse createEmbedding(String text) {

        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(
                    "Text must not be empty"
            );
        }

        float[] vector = embeddingModel.embed(text);

        int previewLength = Math.min(
                PREVIEW_SIZE,
                vector.length
        );

        float[] vectorPreview = Arrays.copyOf(
                vector,
                previewLength
        );

        return new EmbeddingResponse(
                text,
                vector.length,
                vectorPreview
        );
    }
}