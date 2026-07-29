package com.example.newsmanagementsystem.ai.service;


import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.RagSourcesdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("RagNativeService")
public class RagNativeService implements RagService {

    private final VectorStore store;

    private final ChatClient chatClient;

    @SuppressFBWarnings(
            value = "EI_EXPOSE_REP2",
            justification = "Spring intentionally shares the injected VectorStore collaborator.")
    public RagNativeService(VectorStore store, ChatClient client) {
        this.store = store;
        this.chatClient = client;
    }

    public RagResultdto ask(SearchRequestdto request) {

        SearchRequest searchRequest = SearchRequest.builder().
                query(request.query()).topK(10).similarityThreshold(0.1).build();

        List<Document>documents = store.similaritySearch(searchRequest);

        if(documents.isEmpty()){
            return  new RagResultdto(
                    request.query(),
                    "No documents found",
                    List.of()
            );

        }
        StringBuilder context = new StringBuilder();
        for(Document d : documents){
            context.append(d.getText()).append('\n');
        }
        String  answer = chatClient.prompt().system("""
                        You are a helpful document assistant.

                        Answer the question only using the provided context
                        and if something asked that you know and its not present in
                        the document do answer it as well.


                        """).user(user->user.text("""
                        Context : {Context}
                        Question : {Question}
                """
        ).param("Context",context.toString()).param("Question",request.query())).advisors(advisor -> advisor.param(
                ChatMemory.CONVERSATION_ID,
                "default"
        )).call().content();

        List<RagSourcesdto>sources=new ArrayList<>();
        for(Document d : documents){
            sources.add(new RagSourcesdto(
                    String.valueOf(d.getMetadata().get("title")),
                    convertChunkNumber(
                            d.getMetadata().get("chunk_number")
                    ),
                    d.getScore(),
                    d.getText()
            ));
        }
        return new RagResultdto(request.query(),
                answer,
                sources
                );



    }

    static Integer convertChunkNumber(Object chunkNumber) {
        if (chunkNumber == null) {
            return null;
        }

        if (chunkNumber instanceof Number number) {
            return number.intValue();
        }

        return Integer.valueOf(chunkNumber.toString());
    }
}
