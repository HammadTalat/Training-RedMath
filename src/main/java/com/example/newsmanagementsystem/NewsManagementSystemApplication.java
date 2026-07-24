package com.example.newsmanagementsystem;

import com.example.newsmanagementsystem.config.NewsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableConfigurationProperties(NewsProperties.class)
@EnableCaching
@EnableAsync
@EnableScheduling
public class NewsManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsManagementSystemApplication.class, args);
    }

}
