package com.example.newsmanagementsystem.news;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NewsNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public NewsNotFoundException(Long newsId) {
        super("News not found with id: " + newsId);
    }
}
