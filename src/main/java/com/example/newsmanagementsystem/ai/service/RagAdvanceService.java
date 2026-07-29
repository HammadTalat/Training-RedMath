package com.example.newsmanagementsystem.ai.service;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;


@Service("RagAdvanceService")
public class RagAdvanceService implements RagService{

    private final VectorStore store;

    private final ChatClient chatClient;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring intentionally shares the injected VectorStore collaborator.")
    public RagAdvanceService(VectorStore store, ChatClient.Builder builder) {
        this.store = store;

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .topK(3)
                        .similarityThreshold(0.5)
                        .build();


        QuestionAnswerAdvisor questionAnswerAdvisor =
                QuestionAnswerAdvisor.builder(store)
                        .searchRequest(searchRequest)
                        .build();


        chatClient = builder
                .defaultAdvisors(questionAnswerAdvisor)
                .build();
    }

    @Override
    public RagResultdto ask(SearchRequestdto request) {
        String ans = chatClient.prompt()
                .user(request.query())
                .call()
                .content();

        return new RagResultdto( request.query(), ans, List.of());

    }
}
