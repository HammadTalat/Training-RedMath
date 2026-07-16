package com.example.newsmanagementsystem.news;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping({"/api/v1/news", "/api/news"})
public class NewsController {

    private final NewsService newsService;

    public NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<News>> findAll() {
        return ResponseEntity.ok(newsService.findAll());
    }

    @GetMapping("/{newsId}")
    public ResponseEntity<News> findOne(@PathVariable("newsId") Long newsId) {
        return ResponseEntity.ok(newsService.findOne(newsId));
    }

    @PostMapping
    public ResponseEntity<News> create(@RequestBody News news, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newsService.create(news, authentication.getName()));
    }

    @PutMapping("/{newsId}")
    public ResponseEntity<News> update(@PathVariable("newsId") Long newsId, @RequestBody News news) {
        return ResponseEntity.ok(newsService.update(newsId, news));
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete(@PathVariable("newsId") Long newsId) {
        newsService.delete(newsId);
        return ResponseEntity.noContent().build();
    }
}
