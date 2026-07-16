package com.example.newsmanagementsystem.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "app.news")
public record NewsProperties(String title, int pageSize) {
}
