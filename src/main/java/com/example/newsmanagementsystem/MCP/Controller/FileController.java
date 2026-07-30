package com.example.newsmanagementsystem.MCP.Controller;

import com.example.newsmanagementsystem.MCP.Service.FileService;
import com.example.newsmanagementsystem.MCP.Service.FileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/ai/files")
public class FileController {

    private final FileService fileService;

    public FileController(
            FileService fileAssistantService
    ) {
        this.fileService = fileAssistantService;
    }

    @PostMapping("/ask")
    public ResponseEntity<String> ask(
            @RequestBody Map<String, String> body
    ) {

        String message = body.get("message");

        if (message == null || message.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("Message is required");
        }

        String answer =
                fileService.execute(message);

        return ResponseEntity.ok(answer);
    }
}