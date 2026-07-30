package com.example.newsmanagementsystem.MCP.Service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class FileService {

    private final ChatClient chatClient;
    private final ToolCallbackProvider mcpTools;

    public FileService(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider mcpTools
    ) {
        this.chatClient = chatClientBuilder.build();
        this.mcpTools = mcpTools;
    }

    public String execute(String userMessage) {

        return chatClient.prompt()
                .system("""
                        You are a file assistant.

                        Use the available filesystem tools when
                        the user asks you to read, create, edit,
                        list, move, or inspect files.

                        Do not delete or overwrite a file unless
                        the user clearly asks you to do so.
                        """)
                .user(userMessage)
                .tools(mcpTools)
                .call()
                .content();
    }
}