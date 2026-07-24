package com.example.newsmanagementsystem.news;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(NewsServicePracticeFeaturesTests.TestConfiguration.class)
class NewsServicePracticeFeaturesTests {

    @MockitoBean
    private NewsRepository newsRepository;

    @Autowired
    private NewsService newsService;

    @Test
    void secondCallUsesTheCache() {
        News news = new News();
        news.setNewsId(1L);
        news.setTitle("Cached news");
        when(newsRepository.findById(1L)).thenReturn(Optional.of(news));

        newsService.findOne(1L);
        newsService.findOne(1L);

        verify(newsRepository).findById(1L);
    }

    @Test
    void asyncMethodReturnsNewsCount() {
        when(newsRepository.count()).thenReturn(3L);

        Long count = newsService.countNewsAsync().join();

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void scheduledMethodChecksNewsCount() {
        newsService.showNewsCount();

        verify(newsRepository).count();
    }

    @Configuration(proxyBeanMethods = false)
    @EnableCaching
    @EnableAsync
    @Import(NewsService.class)
    @SuppressWarnings("PMD.TestClassWithoutTestCases")
    static class TestConfiguration {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("news");
        }
    }
}
