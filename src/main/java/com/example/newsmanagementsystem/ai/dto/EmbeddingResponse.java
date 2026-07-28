package com.example.newsmanagementsystem.ai.dto;

public record EmbeddingResponse(
        String originalText,
        int dimensions,
        float[] vectorPreview
) {
    public EmbeddingResponse {
        vectorPreview = vectorPreview.clone();
    }

    @Override
    public float[] vectorPreview() {
        return vectorPreview.clone();
    }
}
