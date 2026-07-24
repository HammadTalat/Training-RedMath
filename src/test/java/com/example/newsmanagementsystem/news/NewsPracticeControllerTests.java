package com.example.newsmanagementsystem.news;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewsPracticeControllerTests {

    @Mock
    private NewsService newsService;

    @InjectMocks
    private NewsPracticeController controller;

    @Test
    void cacheEndpointReturnsNews() {
        News news = new News();
        news.setNewsId(1L);
        news.setTitle("Simple cache example");
        when(newsService.findOne(1L)).thenReturn(news);

        NewsResponse response = controller.findCachedNews(1L);

        assertThat(response.title()).isEqualTo("Simple cache example");
    }

    @Test
    void asyncEndpointReturnsNewsCount() {
        when(newsService.countNewsAsync())
                .thenReturn(CompletableFuture.completedFuture(3L));

        Long count = controller.countNewsAsync().join();

        assertThat(count).isEqualTo(3L);
    }

    @Test
    void scheduledEndpointRunsNewsCountMethod() {
        controller.runScheduledCountNow();

        verify(newsService).showNewsCount();
    }
}
