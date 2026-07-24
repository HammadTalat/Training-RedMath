package com.example.newsmanagementsystem.news;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
@SuppressWarnings("PMD.UnitTestContainsTooManyAsserts")
class NewsControllerFindOneTests {

  private static final Long NEWS_ID = 42L;
  private static final String OWNER_USERNAME = "owner-reporter";
  private static final String OTHER_USERNAME = "different-user";
  private static final String FORBIDDEN_MESSAGE =
      "You are not allowed to access this news";
  private static final LocalDateTime REPORTED_AT =
      LocalDateTime.of(2026, 7, 24, 10, 30);

  @Mock
  private NewsService newsService;

  @InjectMocks
  private NewsController newsController;

  @Test
  void findOneReturnsNewsWhenReporterOwnsIt() {
    News news = createNews();
    Authentication authentication =
        authentication(OWNER_USERNAME, "ROLE_REPORTER");
    when(newsService.findOne(NEWS_ID)).thenReturn(news);

    ResponseEntity<?> response =
        newsController.findOne(NEWS_ID, authentication);

    assertSuccessfulResponse(response);
    verify(newsService).findOne(NEWS_ID);
  }

  @Test
  void findOneReturnsNewsWhenDifferentUserIsAdmin() {
    News news = createNews();
    Authentication authentication =
        authentication(OTHER_USERNAME, "ROLE_ADMIN");
    when(newsService.findOne(NEWS_ID)).thenReturn(news);

    ResponseEntity<?> response =
        newsController.findOne(NEWS_ID, authentication);

    assertSuccessfulResponse(response);
    verify(newsService).findOne(NEWS_ID);
  }

  @Test
  void findOneReturnsForbiddenWhenDifferentUserIsReporter() {
    News news = createNews();
    Authentication authentication =
        authentication(OTHER_USERNAME, "ROLE_REPORTER");
    when(newsService.findOne(NEWS_ID)).thenReturn(news);

    ResponseEntity<?> response =
        newsController.findOne(NEWS_ID, authentication);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
    assertThat(response.getBody()).isEqualTo(FORBIDDEN_MESSAGE);
    verify(newsService).findOne(NEWS_ID);
  }

  private static void assertSuccessfulResponse(ResponseEntity<?> response) {
    NewsResponse expectedBody = new NewsResponse(
        NEWS_ID,
        "Practice title",
        "Practice details",
        OWNER_USERNAME,
        REPORTED_AT
    );

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(response.getBody()).isEqualTo(expectedBody);
  }

  private static Authentication authentication(
      String username,
      String authority
  ) {
    return new UsernamePasswordAuthenticationToken(
        username,
        null,
        List.of(new SimpleGrantedAuthority(authority))
    );
  }

  private static News createNews() {
    News news = new News();
    news.setNewsId(NEWS_ID);
    news.setTitle("Practice title");
    news.setDetails("Practice details");
    news.setReportedBy(OWNER_USERNAME);
    news.setReportedAt(REPORTED_AT);
    return news;
  }
}
