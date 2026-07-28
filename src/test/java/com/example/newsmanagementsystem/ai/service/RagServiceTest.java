package com.example.newsmanagementsystem.ai.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RagServiceTest {

    @Test
    void convertsAStringChunkNumberToAnInteger() {
        Integer chunkNumber = RagService.convertChunkNumber("4");

        assertThat(chunkNumber).isEqualTo(4);
    }

    @Test
    void acceptsAChunkNumberThatIsAlreadyNumeric() {
        Integer chunkNumber = RagService.convertChunkNumber(4);

        assertThat(chunkNumber).isEqualTo(4);
    }

    @Test
    void returnsNullWhenChunkNumberIsMissing() {
        Integer chunkNumber = RagService.convertChunkNumber(null);

        assertThat(chunkNumber).isNull();
    }
}
