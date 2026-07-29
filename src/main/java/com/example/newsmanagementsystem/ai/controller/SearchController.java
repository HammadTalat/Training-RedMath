package com.example.newsmanagementsystem.ai.controller;

import com.example.newsmanagementsystem.ai.dto.RagResultdto;
import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import com.example.newsmanagementsystem.ai.dto.SearchResultdto;
import com.example.newsmanagementsystem.ai.service.RagNativeService;
import com.example.newsmanagementsystem.ai.service.RagService;
import com.example.newsmanagementsystem.ai.service.SemanticSearch;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
public class SearchController {

    SemanticSearch searchService;
    RagService service;

    public SearchController(SemanticSearch searchService, @Qualifier("RagAdvanceService")RagService service) {
        this.searchService = searchService;
        this.service = service;
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResultdto>>search(@RequestBody SearchRequestdto request){

        return ResponseEntity.ok(searchService.search(request));
    }
    @PostMapping("/ask")
    public ResponseEntity<RagResultdto>ask(@RequestBody SearchRequestdto request){

        return ResponseEntity.ok(service.ask(request));
    }
}
