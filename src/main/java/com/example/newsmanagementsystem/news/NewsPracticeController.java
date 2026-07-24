package com.example.newsmanagementsystem.news;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/news/practice")
@PreAuthorize("hasRole('ADMIN')")
public class NewsPracticeController {

    private final NewsService newsService;

    NewsPracticeController(NewsService newsService) {
        this.newsService = newsService;
    }

    @GetMapping("/cache/{newsId}")
    public NewsResponse findCachedNews(@PathVariable Long newsId) {
        return NewsResponse.from(newsService.findOne(newsId));
    }

    @GetMapping("/async-count")
    public CompletableFuture<Long> countNewsAsync() {
        return newsService.countNewsAsync();
    }

    @GetMapping("/scheduled-count")
    public String runScheduledCountNow() {
        newsService.showNewsCount();
        return "News count was printed in the console";
    }
}
