package com.example.newsmanagementsystem.ai.service;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
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
    public RagAdvanceService(VectorStore store, ChatClient chatClient) {
        this.store = store;
        this.chatClient = chatClient;

    }

    @Override
    public RagResultdto ask(SearchRequestdto request) {

        SearchRequest searchRequest =
                SearchRequest.builder()
                        .topK(10)
                        .similarityThreshold(0.5)
                        .build();


        QuestionAnswerAdvisor questionAnswerAdvisor =
                QuestionAnswerAdvisor.builder(store)
                        .searchRequest(searchRequest)
                        .build();
        String ans = chatClient.prompt()
                .user(request.query())
                .advisors(questionAnswerAdvisor)
                .advisors(advisor -> advisor.param(ChatMemory.CONVERSATION_ID,"default"))
                .call()
                .content();

        return new RagResultdto( request.query(), ans, List.of());

    }


}
