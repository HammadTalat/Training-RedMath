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
import java.util.Optional;

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
    @PreAuthorize("hasAnyRole('ADMIN', 'REPORTER')")
    public ResponseEntity<?> findOne(
            @PathVariable("newsId") Long newsId,
            Authentication auth
    ) {

        News news = newsService.findOne(newsId);

        boolean isAdmin = auth.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                );
        if(auth.getName().equals(news.getReportedBy()) || isAdmin){
            return ResponseEntity.ok(news);
        }

        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body("You are not allowed to access this news");
    }
    @PostMapping
    public ResponseEntity<News> create(@RequestBody News news, Authentication authentication) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(newsService.create(news, authentication.getName()));
    }

    @PutMapping("/{newsId}")
    @PreAuthorize("hasAnyRole('ADMIN' , 'REPORTER')")
    public ResponseEntity<News> update(@PathVariable("newsId") Long newsId, @RequestBody News news) {
        return ResponseEntity.ok(newsService.update(newsId, news));
    }

    @DeleteMapping("/{newsId}")
    public ResponseEntity<Void> delete(@PathVariable("newsId") Long newsId) {
        newsService.delete(newsId);
        return ResponseEntity.noContent().build();
    }
}
