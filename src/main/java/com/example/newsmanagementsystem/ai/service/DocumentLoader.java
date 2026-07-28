package com.example.newsmanagementsystem.ai.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class DocumentLoader implements ApplicationRunner {

    private static final String CATEGORY_METADATA_KEY = "category";
    private static final String CHUNK_NUMBER_METADATA_KEY = "chunk_number";
    private static final String DEMO_SOURCE = "hard-coded-demo";
    private static final String SOURCE_METADATA_KEY = "source";
    private static final String TITLE_METADATA_KEY = "title";
    private static final Logger log =
            LoggerFactory.getLogger(DocumentLoader.class);

    private final VectorStore vectorStore;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring intentionally shares the injected VectorStore collaborator.")
    public DocumentLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {

        List<Document> documents = List.of(

                new Document(
                        "Employees receive 18 annual paid leaves.",
                        Map.of(
                                TITLE_METADATA_KEY, "Annual Leave Policy",
                                CHUNK_NUMBER_METADATA_KEY, "1",
                                CATEGORY_METADATA_KEY, "employee-policy",
                                SOURCE_METADATA_KEY, DEMO_SOURCE
                        )
                ),

                new Document(
                        "Employees can work remotely for two days every week.",
                        Map.of(
                                TITLE_METADATA_KEY, "Remote Work Policy",
                                CHUNK_NUMBER_METADATA_KEY, "2",
                                CATEGORY_METADATA_KEY, "employee-policy",
                                SOURCE_METADATA_KEY, DEMO_SOURCE
                        )
                ),

                new Document(
                        "The office working hours are from 9:00 AM to 6:00 PM.",
                        Map.of(
                                TITLE_METADATA_KEY, "Office Working Hours",
                                CHUNK_NUMBER_METADATA_KEY, "3",
                                CATEGORY_METADATA_KEY, "employee-policy",
                                SOURCE_METADATA_KEY, DEMO_SOURCE
                        )
                ),

                new Document(
                        """
                        Reporters can create and update news articles.
                        Administrators can create, update, and delete news articles.
                        """,
                        Map.of(
                                TITLE_METADATA_KEY, "News Application Roles",
                                CHUNK_NUMBER_METADATA_KEY, "4",
                                CATEGORY_METADATA_KEY, "application-security",
                                SOURCE_METADATA_KEY, DEMO_SOURCE
                        )
                )
        );

        vectorStore.add(documents);

        int documentCount = documents.size();
        log.info("Loaded {} demo documents into the vector store", documentCount);
    }
}
