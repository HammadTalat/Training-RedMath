package com.example.newsmanagementsystem.ai.dto;

import java.util.Map;

public record SearchResultdto(
        String documentId,
        String content,
        Map<String, Object> metadata,
        Double score
) {
    public SearchResultdto {
        metadata = Map.copyOf(metadata);
    }

    @Override
    public Map<String, Object> metadata() {
        return Map.copyOf(metadata);
    }
}
