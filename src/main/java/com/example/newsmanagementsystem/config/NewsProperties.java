package com.example.newsmanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Reads the app.news.title and app.news.page-size properties together.
 * YAML's "page-size" is automatically mapped to the Java name "pageSize".
 */
@ConfigurationProperties(prefix = "app.news")
public record NewsProperties(String title, int pageSize) {
}
