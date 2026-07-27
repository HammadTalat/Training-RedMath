package com.example.newsmanagementsystem.ai.dto;

public record EmbeddingResponse(
        String originalText,
        int dimensions,
        float[] vectorPreview
) {
}