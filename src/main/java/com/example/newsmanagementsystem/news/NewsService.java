package com.example.newsmanagementsystem.news;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
    public News update(Long newsId, NewsRequest request) {
        News existingNews = findOne(newsId);
        existingNews.setTitle(request.title());
        existingNews.setDetails(request.details());
        return newsRepository.save(existingNews);
    }

    @Transactional
    public void delete(Long newsId) {
        News news = findOne(newsId);
        newsRepository.delete(news);
    }
}
