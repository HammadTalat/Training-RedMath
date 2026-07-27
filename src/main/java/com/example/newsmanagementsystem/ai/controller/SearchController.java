package com.example.newsmanagementsystem.ai.controller;

import com.example.newsmanagementsystem.ai.dto.SearchRequestdto;
import com.example.newsmanagementsystem.ai.dto.SearchResultdto;
import com.example.newsmanagementsystem.ai.service.SemanticSearch;
import okhttp3.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/rag")
public class SearchController {

    SemanticSearch searchService;

    public SearchController(SemanticSearch searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/search")
    public ResponseEntity<List<SearchResultdto>>search(@RequestBody SearchRequestdto request){

        return ResponseEntity.ok(searchService.search(request));
    }

}
