package com.example.newsmanagementsystem.ai.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefensiveCopyTest {

    private static final String ORIGINAL_TITLE = "Original";
    private static final String TITLE_KEY = "title";

    @Test
    void embeddingResponseCopiesVectorPreviewDuringConstruction() {
        float[] vectorPreview = {1.0F, 2.0F};
        EmbeddingResponse response =
                new EmbeddingResponse("example", vectorPreview.length, vectorPreview);

        vectorPreview[0] = 99.0F;

        assertThat(response.vectorPreview()).containsExactly(1.0F, 2.0F);
    }

    @Test
    void embeddingResponseReturnsDefensiveVectorPreviewCopy() {
        EmbeddingResponse response =
                new EmbeddingResponse("example", 2, new float[] {1.0F, 2.0F});

        float[] returnedPreview = response.vectorPreview();
        returnedPreview[0] = 99.0F;

        assertThat(response.vectorPreview()).containsExactly(1.0F, 2.0F);
    }

    @Test
    void ragResultCopiesSourcesDuringConstruction() {
        RagSourcesdto source =
                new RagSourcesdto("policy.txt", 1, 0.9, "Policy content");
        List<RagSourcesdto> sources = new ArrayList<>(List.of(source));
        RagResultdto result = new RagResultdto("Question?", "Answer", sources);

        sources.clear();

        assertThat(result.sources()).containsExactly(source);
    }

    @Test
    void ragResultReturnsUnmodifiableSources() {
        RagSourcesdto source =
                new RagSourcesdto("policy.txt", 1, 0.9, "Policy content");
        RagResultdto result =
                new RagResultdto("Question?", "Answer", List.of(source));

        List<RagSourcesdto> returnedSources = result.sources();

        assertThatThrownBy(returnedSources::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void searchResultCopiesMetadataDuringConstruction() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put(TITLE_KEY, ORIGINAL_TITLE);
        SearchResultdto result =
                new SearchResultdto("document-1", "Content", metadata, 0.9);

        metadata.put(TITLE_KEY, "Changed");

        assertThat(result.metadata()).containsEntry(TITLE_KEY, ORIGINAL_TITLE);
    }

    @Test
    void searchResultReturnsUnmodifiableMetadata() {
        SearchResultdto result =
                new SearchResultdto(
                        "document-1",
                        "Content",
                        Map.of(TITLE_KEY, ORIGINAL_TITLE),
                        0.9);

        Map<String, Object> returnedMetadata = result.metadata();

        assertThatThrownBy(() -> returnedMetadata.put(TITLE_KEY, "Changed"))
                .isInstanceOf(UnsupportedOperationException.class);
    }
}
