package com.example.newsmanagementsystem.news;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NewsNotFoundException extends RuntimeException {

    public NewsNotFoundException(Long newsId) {
        super("News not found with id: " + newsId);
    }
}
