package com.example.newsmanagementsystem.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/property-examples")
public class PropertyExamplesController {

    private final NewsProperties newsProperties;

    @Value("${app.sample-message}")
    private String sampleMessage;

    public PropertyExamplesController(NewsProperties newsProperties) {
        this.newsProperties = newsProperties;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> showPropertyExamples() {

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("properties", newsProperties);
        response.put("message", sampleMessage);

        return ResponseEntity.ok(response);
    }

}
