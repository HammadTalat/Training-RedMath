    package com.example.newsmanagementsystem;

    import com.example.newsmanagementsystem.news.News;
    import com.example.newsmanagementsystem.news.NewsRepository;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.context.SpringBootTest;
    import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
    import org.springframework.http.MediaType;
    import org.springframework.test.web.servlet.MockMvc;

    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
    import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
    import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

    @SpringBootTest
    @AutoConfigureMockMvc
    class NewsManagementSystemApplicationTests {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private NewsRepository newsRepository;

        @BeforeEach
        void clearDatabase() {
            newsRepository.deleteAll();
        }

        @Test
        void performsCompleteCrudFlow() throws Exception {
            String detailsLongerThan255Characters = "Complete news details. ".repeat(15);

            mockMvc.perform(post("/api/v1/news")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Initial title",
                                      "details": "%s",
                                      "reportedBy": "Reporter"
                                    }
                                    """.formatted(detailsLongerThan255Characters)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.newsId").isNumber())
                    .andExpect(jsonPath("$.title").value("Initial title"))
                    .andExpect(jsonPath("$.details").value(detailsLongerThan255Characters))
                    .andExpect(jsonPath("$.reportedAt").isNotEmpty());

            News createdNews = newsRepository.findAll().getFirst();
            Long newsId = createdNews.getNewsId();

            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.details").value(detailsLongerThan255Characters));

            mockMvc.perform(put("/api/v1/news/{newsId}", newsId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("""
                                    {
                                      "title": "Updated title",
                                      "details": "Updated details",
                                      "reportedBy": "Updated reporter",
                                      "reportedAt": "2000-01-01T00:00:00"
                                    }
                                    """))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.newsId").value(newsId))
                    .andExpect(jsonPath("$.title").value("Updated title"))
                    .andExpect(jsonPath("$.reportedAt").value(createdNews.getReportedAt().toString()));

            mockMvc.perform(get("/api/v1/news"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].newsId").value(newsId));

            mockMvc.perform(delete("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isNoContent());

            mockMvc.perform(get("/api/v1/news/{newsId}", newsId))
                    .andExpect(status().isNotFound());
        }

    }
