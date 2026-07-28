package com.example.newsmanagementsystem.ai.dto.VectorDB;

import jakarta.validation.constraints.NotBlank;

public record SaveDocumentRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Content is required")
        String content

) {
}