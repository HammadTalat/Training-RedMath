package com.example.newsmanagementsystem.news;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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

    NewsController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping
    public ResponseEntity<List<NewsResponse>> findAll() {
        List<NewsResponse> response = newsService.findAll()
                .stream()
                .map(NewsResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{newsId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'REPORTER')")
    public ResponseEntity<?> findOne(
            @PathVariable("newsId") Long newsId,
            Authentication auth
    ) {

        News news = newsService.findOne(newsId);

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        "ROLE_ADMIN".equals(authority.getAuthority())
                );
        if(auth.getName().equals(news.getReportedBy()) || isAdmin){
            return ResponseEntity.ok(NewsResponse.from(news));
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("You are not allowed to access this news");
    }
    @PostMapping
    public ResponseEntity<NewsResponse> create(
            @RequestBody NewsRequest request,
            Authentication authentication
    ) {
        News createdNews = newsService.create(request, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(NewsResponse.from(createdNews));
    }

    @PutMapping("/{newsId}")
    @PreAuthorize("hasAnyRole('ADMIN' , 'REPORTER')")
    public ResponseEntity<NewsResponse> update(
            @PathVariable("newsId") Long newsId,
            @RequestBody NewsRequest request
    ) {
        News updatedNews = newsService.update(newsId, request);
        return ResponseEntity.ok(NewsResponse.from(updatedNews));
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete(@PathVariable("newsId") Long newsId) {
        newsService.delete(newsId);
        return ResponseEntity.noContent().build();
    }
}
