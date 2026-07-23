package com.example.newsmanagementsystem.news;

import java.time.LocalDateTime;

public record NewsResponse(
        Long newsId,
        String title,
        String details,
        String reportedBy,
        LocalDateTime reportedAt
) {

    static NewsResponse from(News news) {
        return new NewsResponse(
                news.getNewsId(),
                news.getTitle(),
                news.getDetails(),
                news.getReportedBy(),
                news.getReportedAt()
        );
    }
}
