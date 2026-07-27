package com.example.newsmanagementsystem.ai.dto;

public record RagSourcesdto (

        String filename,
        Integer chunkNumber,
        Double similarityScore,
        String content
){

}
