package com.example.newsmanagementsystem.ai.dto;

import java.util.List;

public record RagResultdto(
        String question,
        String answer,
        List<RagSourcesdto> sources
) {
    public RagResultdto {
        sources = List.copyOf(sources);
    }

    @Override
    public List<RagSourcesdto> sources() {
        return List.copyOf(sources);
    }
}
