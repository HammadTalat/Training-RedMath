package com.example.newsmanagementsystem.news;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

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
    public News create(News news) {
        news.setNewsId(null);
        return newsRepository.save(news);
    }


    public News update(Long newsId, News news) {
        Optional<News> e = newsRepository.findById(newsId);
        News existingNews = null;
        try {
            if (e.isPresent()) {
                existingNews = e.get();
                existingNews.setTitle(news.getTitle());
                existingNews.setDetails(news.getDetails());
                existingNews.setReportedBy(news.getReportedBy());
                return newsRepository.save(existingNews);

            }
        }
        catch(Exception ex){

        }
        return null;
    }


    @Transactional
    public void delete(Long newsId) {
        News news = findOne(newsId);
        newsRepository.delete(news);
    }
}
