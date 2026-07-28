package com.example.newsmanagementsystem.ai.service.VectorDB;

import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentRequest;
import com.example.newsmanagementsystem.ai.dto.VectorDB.SaveDocumentResponse;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class DocumentStorageService {

    private final VectorStore vectorStore;
    private final TokenTextSplitter textSplitter;

    public DocumentStorageService(
            VectorStore vectorStore
    ) {
        this.vectorStore = vectorStore;
        this.textSplitter = TokenTextSplitter.builder()
                .withChunkSize(300)
                .withMinChunkSizeChars(100)
                .withMinChunkLengthToEmbed(20)
                .withKeepSeparator(true)
                .build();
    }

    public SaveDocumentResponse saveDocument(
            SaveDocumentRequest request
    ) {
        String documentId =
                UUID.randomUUID().toString();

        Map<String, Object> metadata =
                new HashMap<>();

        metadata.put("document_id", documentId);
        metadata.put("title", request.title());

        Document originalDocument =
                new Document(
                        request.content(),
                        metadata
                );
        List<Document> chunks =
                textSplitter.apply(
                        List.of(originalDocument)
                );

        for (int index = 0;
             index < chunks.size();
             index++) {

            Document chunk = chunks.get(index);

            chunk.getMetadata().put(
                    "chunk_number",
                    index + 1
            );
        }

        vectorStore.add(chunks);

        return new SaveDocumentResponse(
                documentId,
                request.title(),
                chunks.size(),
                "Document stored successfully"
        );
    }
}