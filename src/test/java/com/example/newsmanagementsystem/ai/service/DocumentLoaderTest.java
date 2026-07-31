//package com.example.newsmanagementsystem.ai.service;
//
//import static org.mockito.ArgumentMatchers.argThat;
//import static org.mockito.Mockito.verify;
//
//import java.util.List;
//import java.util.Map;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.ai.document.Document;
//import org.springframework.ai.vectorstore.VectorStore;
//import org.springframework.beans.factory.xml.DocumentLoader;
//import org.springframework.boot.ApplicationArguments;
//
//@ExtendWith(MockitoExtension.class)
//class DocumentLoaderTest {
//
//  private static final String DEMO_SOURCE = "hard-coded-demo";
//  private static final String EMPLOYEE_POLICY = "employee-policy";
//  private static final List<DocumentSnapshot> EXPECTED_DOCUMENTS =
//      List.of(
//          new DocumentSnapshot(
//              "Employees receive 18 annual paid leaves.",
//              metadata("Annual Leave Policy", "1", EMPLOYEE_POLICY)),
//          new DocumentSnapshot(
//              "Employees can work remotely for two days every week.",
//              metadata("Remote Work Policy", "2", EMPLOYEE_POLICY)),
//          new DocumentSnapshot(
//              "The office working hours are from 9:00 AM to 6:00 PM.",
//              metadata("Office Working Hours", "3", EMPLOYEE_POLICY)),
//          new DocumentSnapshot(
//              """
//              Reporters can create and update news articles.
//              Administrators can create, update, and delete news articles.
//              """,
//              metadata("News Application Roles", "4", "application-security")));
//
//  @Mock
//  private VectorStore vectorStore;
//
//  @Mock
//  private ApplicationArguments applicationArguments;
//
//  @InjectMocks
//  private DocumentLoader documentLoader;
//
//  @Test
//  void loadsAllDemoDocumentsWithExpectedContentAndMetadata() {
//    documentLoader.run(applicationArguments);
//
//    verify(vectorStore)
//        .add(argThat(documents -> EXPECTED_DOCUMENTS.equals(snapshot(documents))));
//  }
//
//  private static Map<String, Object> metadata(
//      String title, String chunkNumber, String category) {
//    return Map.of(
//        "title", title,
//        "chunk_number", chunkNumber,
//        "category", category,
//        "source", DEMO_SOURCE);
//  }
//
//  private static List<DocumentSnapshot> snapshot(List<Document> documents) {
//    return documents.stream()
//        .map(document -> new DocumentSnapshot(document.getText(), document.getMetadata()))
//        .toList();
//  }
//
//  private record DocumentSnapshot(String text, Map<String, Object> metadata) {}
//}
