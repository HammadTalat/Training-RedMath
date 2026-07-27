package com.example.newsmanagementsystem.ai.dto;

import java.util.List;

public record RagResultdto(

    String question,
    String answer,
    List<RagSourcesdto>sources

) {
}
