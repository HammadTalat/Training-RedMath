package com.example.newsmanagementsystem.Cucumber;

import com.example.newsmanagementsystem.NewsManagementSystemApplication;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@CucumberContextConfiguration
@SpringBootTest(
        classes = NewsManagementSystemApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.MOCK
)
@ActiveProfiles("test")
@MockitoBean(types = {
        ChatModel.class,
        EmbeddingModel.class,
        VectorStore.class
})
public class CucumberSpringConfiguration {
}
