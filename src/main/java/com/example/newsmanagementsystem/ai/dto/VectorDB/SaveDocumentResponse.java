package com.example.newsmanagementsystem.ai.dto.VectorDB;

public record SaveDocumentResponse(
        String documentId,
        String title,
        int chunksStored,
        String message
) {
}