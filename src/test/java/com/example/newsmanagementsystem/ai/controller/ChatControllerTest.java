package com.example.newsmanagementsystem.ai.controller;

import com.example.newsmanagementsystem.ai.service.ChatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatController.class)
class ChatControllerTest {

    private static final String CONVERSATION_ID = "spring-learning";
    private static final String USERNAME = "hammad";
    private static final String MESSAGE = "Hello";
    private static final String HELLO_REQUEST = """
            {
              "message": "Hello"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ChatService chatService;

    @MockitoBean
    private CacheManager cacheManager;

    @Test
    @WithMockUser(username = USERNAME)
    void sendsTheMessageAndReturnsTheAnswer() throws Exception {
        when(chatService.chat(
                USERNAME,
                CONVERSATION_ID,
                MESSAGE
        )).thenReturn("Hello! How can I help?");

        mockMvc.perform(post(
                        "/api/v1/ai/chats/{conversationId}/messages",
                        CONVERSATION_ID
                )
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(HELLO_REQUEST))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.conversationId").value(CONVERSATION_ID))
                .andExpect(jsonPath("$.answer").value(
                        "Hello! How can I help?"
                ));

        verify(chatService).chat(USERNAME, CONVERSATION_ID, MESSAGE);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void clearsTheSelectedConversation() throws Exception {
        mockMvc.perform(delete(
                        "/api/v1/ai/chats/{conversationId}",
                        CONVERSATION_ID
                ).with(csrf()))
                .andExpect(status().isNoContent());

        verify(chatService).clearConversation(USERNAME, CONVERSATION_ID);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void rejectsABlankMessage() throws Exception {
        mockMvc.perform(post(
                        "/api/v1/ai/chats/{conversationId}/messages",
                        CONVERSATION_ID
                )
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "message": "   "
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(
                        "Message must not be blank"
                ));

        verifyNoInteractions(chatService);
    }

    @Test
    @WithMockUser(username = USERNAME)
    void returnsASafeMessageWhenGeminiFails() throws Exception {
        when(chatService.chat(
                USERNAME,
                CONVERSATION_ID,
                MESSAGE
        )).thenThrow(new RuntimeException("Provider details"));

        mockMvc.perform(post(
                        "/api/v1/ai/chats/{conversationId}/messages",
                        CONVERSATION_ID
                )
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(HELLO_REQUEST))
                .andExpect(status().isServiceUnavailable())
                .andExpect(content().string(
                        "The AI service is currently unavailable. "
                                + "Please try again later."
                ));

        verify(chatService).chat(USERNAME, CONVERSATION_ID, MESSAGE);
    }
}
