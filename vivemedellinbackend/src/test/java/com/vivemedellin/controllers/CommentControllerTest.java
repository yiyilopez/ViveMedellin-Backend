package com.vivemedellin.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.filters.JwtAuthenticationFilter;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.security.CustomUserDetailService;
import com.vivemedellin.services.CommentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentController.class)
public class CommentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommentService commentService;

    // Mock security-related beans to satisfy the context in @WebMvcTest
    @MockBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockBean
    private CustomUserDetailService customUserDetailService;

    @Test
    @WithMockUser(username = "usuario@test.com", roles = {"USER"})
    void shouldCreateAndDisplayRichComment_whenAuthenticatedUserPosts() throws Exception {
        int postId = 123;
        String richContent = "<p>Gran evento!</p>\\n![foto](http://example.com/img.png)";

        CommentDto requestDto = new CommentDto();
        requestDto.setContent(richContent);

        CommentDto createdDto = new CommentDto();
        createdDto.setId(1);
        createdDto.setContent(richContent);

        when(commentService.createComment(any(CommentDto.class), anyInt(), any()))
                .thenReturn(createdDto);
        when(commentService.getCommentsByPost(anyInt()))
                .thenReturn(List.of(createdDto));

        // Create comment
        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.content").value(richContent));

        // Immediately fetch comments and expect the created one to be present
        mockMvc.perform(get("/api/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].content").value(richContent));
    }

    @Test
    void shouldRejectCommentCreation_whenUserNotAuthenticated() throws Exception {
        int postId = 456;

        CommentDto requestDto = new CommentDto();
        requestDto.setContent("Comentario sin autenticación");

        mockMvc.perform(post("/api/posts/" + postId + "/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isUnauthorized());
    }
}
