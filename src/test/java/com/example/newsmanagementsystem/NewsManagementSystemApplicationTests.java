    package com.example.newsmanagementsystem;

    import com.example.newsmanagementsystem.news.News;
    import com.example.newsmanagementsystem.news.NewsRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
    import org.springframework.http.MediaType;
    import org.springframework.security.test.context.support.WithMockUser;
    import org.springframework.test.web.servlet.MockMvc;
    import org.springframework.test.web.servlet.MvcResult;
    import tools.jackson.databind.ObjectMapper;

    import java.time.LocalDateTime;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.hamcrest.Matchers.hasSize;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
    import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @SpringBootTest
    @AutoConfigureMockMvc
    class NewsManagementSystemApplicationTests {

        private static final String NEWS_BY_ID_PATH = "/api/v1/news/{newsId}";

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private NewsRepository newsRepository;

        @Autowired
        private ObjectMapper objectMapper;

        @BeforeEach
        void clearDatabase() {
            newsRepository.deleteAll();
        }

        @Test
        @WithMockUser(roles = {"ADMIN", "REPORTER"})
        void performsCompleteCrudFlow() throws Exception {
            String detailsLongerThan255Characters = "Complete news details. ".repeat(15);

            mockMvc.perform(post("/api/v1/news")
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {%n\
                                      "title": "Initial title",%n\
                                      "details": "%s",%n\
                                      "reportedBy": "Reporter"%n\
                                    }%n\
                                    """.formatted(detailsLongerThan255Characters)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.*", hasSize(5)))
                    .andExpect(jsonPath("$.newsId").isNumber())
                    .andExpect(jsonPath("$.title").value("Initial title"))
                    .andExpect(jsonPath("$.details").value(detailsLongerThan255Characters))
                    .andExpect(jsonPath("$.reportedBy").value("user"))
                    .andExpect(jsonPath("$.reportedAt").isNotEmpty());

            News createdNews = newsRepository.findAll().getFirst();
            Long newsId = createdNews.getNewsId();

            mockMvc.perform(get(NEWS_BY_ID_PATH, newsId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.*", hasSize(5)))
                    .andExpect(jsonPath("$.details").value(detailsLongerThan255Characters));

            MvcResult updateResult = mockMvc.perform(put(NEWS_BY_ID_PATH, newsId)
                            .with(csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "newsId": 999,
                                      "title": "Updated title",
                                      "details": "Updated details",
                                      "reportedBy": "Updated reporter",
                                      "reportedAt": "2000-01-01T00:00:00"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.*", hasSize(5)))
                    .andExpect(jsonPath("$.newsId").value(newsId))
                    .andExpect(jsonPath("$.title").value("Updated title"))
                    .andExpect(jsonPath("$.reportedBy").value(createdNews.getReportedBy()))
                    .andReturn();

            String reportedAt = objectMapper.readTree(
                    updateResult.getResponse().getContentAsString()
            ).get("reportedAt").stringValue();
            assertThat(LocalDateTime.parse(reportedAt))
                    .isEqualTo(createdNews.getReportedAt());

            mockMvc.perform(get("/api/v1/news"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].*", hasSize(5)))
                    .andExpect(jsonPath("$[0].newsId").value(newsId));

            mockMvc.perform(delete(NEWS_BY_ID_PATH, newsId)
                            .with(csrf()))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get(NEWS_BY_ID_PATH, newsId))
                    .andExpect(status().isNotFound());
        }

    }
