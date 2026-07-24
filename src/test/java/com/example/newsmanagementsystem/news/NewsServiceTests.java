package com.example.newsmanagementsystem.news;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class NewsServiceTests {

    private static final Long NEWS_ID = 42L;

    @Mock
    private NewsRepository newsRepository;

    @InjectMocks
    private NewsService newsService;

    @Test
    void findAllReturnsRepositoryResults() {
        List<News> expectedNews = List.of(createNews(NEWS_ID), createNews(43L));
        when(newsRepository.findAll()).thenReturn(expectedNews);

        List<News> actualNews = newsService.findAll();

        assertThat(actualNews).isSameAs(expectedNews);
        verify(newsRepository).findAll();
    }

    @Test
    void findOneReturnsNewsWhenItExists() {
        News expectedNews = createNews(NEWS_ID);
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.of(expectedNews));

        News actualNews = newsService.findOne(NEWS_ID);

        assertThat(actualNews).isSameAs(expectedNews);
        verify(newsRepository).findById(NEWS_ID);
    }

    @Test
    void findOneThrowsWhenNewsDoesNotExist() {
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.findOne(NEWS_ID))
                .isInstanceOf(NewsNotFoundException.class)
                .hasMessage("News not found with id: " + NEWS_ID);
        verify(newsRepository).findById(NEWS_ID);
    }

    @Test
    void createMapsRequestAndReporterAndReturnsSavedNews() {
        NewsRequest request = new NewsRequest("New title", "New details");
        News persistedNews = createNews(NEWS_ID);
        when(newsRepository.save(any(News.class))).thenReturn(persistedNews);

        News createdNews = newsService.create(request, "reporter");

        ArgumentCaptor<News> newsCaptor = ArgumentCaptor.forClass(News.class);
        verify(newsRepository).save(newsCaptor.capture());
        assertThat(createdNews).isSameAs(persistedNews);
        assertThat(newsCaptor.getValue())
                .extracting(News::getTitle, News::getDetails, News::getReportedBy)
                .containsExactly("New title", "New details", "reporter");
    }

    @Test
    void updateChangesRequestFieldsAndPreservesExistingMetadata() {
        LocalDateTime reportedAt = LocalDateTime.of(2026, 7, 24, 10, 30);
        News existingNews = createNews(NEWS_ID);
        existingNews.setReportedAt(reportedAt);
        News persistedNews = createNews(NEWS_ID);
        NewsRequest request = new NewsRequest("Updated title", "Updated details");
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.of(existingNews));
        when(newsRepository.save(existingNews)).thenReturn(persistedNews);

        News updatedNews = newsService.update(NEWS_ID, request);

        assertThat(updatedNews).isSameAs(persistedNews);
        assertThat(existingNews)
                .extracting(
                        News::getNewsId,
                        News::getTitle,
                        News::getDetails,
                        News::getReportedBy,
                        News::getReportedAt)
                .containsExactly(
                        NEWS_ID,
                        "Updated title",
                        "Updated details",
                        "original-reporter",
                        reportedAt);
        verify(newsRepository).findById(NEWS_ID);
        verify(newsRepository).save(existingNews);
    }

    @Test
    void updateThrowsAndDoesNotSaveWhenNewsDoesNotExist() {
        NewsRequest request = new NewsRequest("Updated title", "Updated details");
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.update(NEWS_ID, request))
                .isInstanceOf(NewsNotFoundException.class)
                .hasMessage("News not found with id: " + NEWS_ID);
        verify(newsRepository).findById(NEWS_ID);
        verify(newsRepository, never()).save(any(News.class));
    }

    @Test
    void deleteRemovesNewsWhenItExists() {
        News existingNews = createNews(NEWS_ID);
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.of(existingNews));

        assertThatCode(() -> newsService.delete(NEWS_ID)).doesNotThrowAnyException();

        verify(newsRepository).findById(NEWS_ID);
        verify(newsRepository).delete(existingNews);
    }

    @Test
    void deleteThrowsAndDoesNotDeleteWhenNewsDoesNotExist() {
        when(newsRepository.findById(NEWS_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> newsService.delete(NEWS_ID))
                .isInstanceOf(NewsNotFoundException.class)
                .hasMessage("News not found with id: " + NEWS_ID);
        verify(newsRepository).findById(NEWS_ID);
        verify(newsRepository, never()).delete(any(News.class));
    }

    private static News createNews(Long newsId) {
        News news = new News();
        news.setNewsId(newsId);
        news.setTitle("Original title");
        news.setDetails("Original details");
        news.setReportedBy("original-reporter");
        return news;
    }
}
