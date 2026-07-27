package com.example.newsmanagementsystem.ai.service;

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

    private static final Logger log =
            LoggerFactory.getLogger(DocumentLoader.class);

    private final VectorStore vectorStore;

    public DocumentLoader(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @Override
    public void run(ApplicationArguments args) {

        List<Document> documents = List.of(

                new Document(
                        "Employees receive 18 annual paid leaves.",
                        Map.of(
                                "title", "Annual Leave Policy",
                                "category", "employee-policy",
                                "source", "hard-coded-demo"
                        )
                ),

                new Document(
                        "Employees can work remotely for two days every week.",
                        Map.of(
                                "title", "Remote Work Policy",
                                "category", "employee-policy",
                                "source", "hard-coded-demo"
                        )
                ),

                new Document(
                        "The office working hours are from 9:00 AM to 6:00 PM.",
                        Map.of(
                                "title", "Office Working Hours",
                                "category", "employee-policy",
                                "source", "hard-coded-demo"
                        )
                ),

                new Document(
                        """
                        Reporters can create and update news articles.
                        Administrators can create, update, and delete news articles.
                        """,
                        Map.of(
                                "title", "News Application Roles",
                                "category", "application-security",
                                "source", "hard-coded-demo"
                        )
                )
        );

        vectorStore.add(documents);

        log.info(
                "Loaded {} demo documents into the vector store",
                documents.size()
        );
    }
}