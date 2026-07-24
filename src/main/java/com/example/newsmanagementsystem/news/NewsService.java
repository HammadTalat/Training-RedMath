package com.example.newsmanagementsystem.news;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional(readOnly = true)
public class NewsService {

    private final NewsRepository newsRepository;

    public NewsService(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    public List<News> findAll() {
        return newsRepository.findAll();
    }

    @Cacheable("news")
    public News findOne(Long newsId) {
        return newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));
    }

    @Transactional
    public News create(NewsRequest request, String reportedBy) {
        News news = new News();
        news.setTitle(request.title());
        news.setDetails(request.details());
        news.setReportedBy(reportedBy);
        return newsRepository.save(news);
    }

    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public News update(Long newsId, NewsRequest request) {
        News existingNews = findOne(newsId);
        existingNews.setTitle(request.title());
        existingNews.setDetails(request.details());
        return newsRepository.save(existingNews);
    }

    @Transactional
    @CacheEvict(value = "news", allEntries = true)
    public void delete(Long newsId) {
        News news = findOne(newsId);
        newsRepository.delete(news);
    }

    @Async
    public CompletableFuture<Long> countNewsAsync() {
        long totalNews = newsRepository.count();
        return CompletableFuture.completedFuture(totalNews);
    }

    @Scheduled(fixedDelay = 60_000)
    @SuppressWarnings("PMD.SystemPrintln") // Console output is intentional for this practice method.
    public void showNewsCount() {
        long totalNews = newsRepository.count();
        System.out.println("Total news: " + totalNews);
    }
}
