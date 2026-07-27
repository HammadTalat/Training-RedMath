package com.example.newsmanagementsystem.ai.service;

import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import com.example.newsmanagementsystem.ai.dto.SearchResultdto;
import com.google.genai.Documents;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import javax.naming.directory.SearchResult;
import javax.print.Doc;
import java.util.ArrayList;
import java.util.List;


@Service
public class SemanticSearch {

    private static final int top_k = 3;
    private static final double similarity_thresh = 0.0;
    private final VectorStore store;

    SemanticSearch(VectorStore store) {

        this.store = store;
    }

    public List<SearchResultdto> search(SearchRequestdto request) {

        SearchRequest searchRequest = SearchRequest.builder().
                query(request.query()).topK(top_k).similarityThreshold(similarity_thresh).build();

        List<Document>documents = store.similaritySearch(searchRequest);
        List<SearchResultdto>finalResult=new ArrayList<>();

        for(Document d : documents){
            finalResult.add(new SearchResultdto(d.getId(),d.getText(),d.getMetadata(),d.getScore()));
        }
        return finalResult;

    }

}
