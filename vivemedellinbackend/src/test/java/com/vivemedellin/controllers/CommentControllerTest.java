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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    //CP-002
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

    //CP-002 Camino excepción
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

    // Test: Eliminación exitosa de un comentario propio
    @Test
    @WithMockUser(username = "usuario@test.com", roles = {"USER"})
    void shouldDeleteOwnComment_whenAuthenticatedUserIsAuthor() throws Exception {
        // Arrange (Given que el usuario está autenticado en la plataforma
        // And es el autor del comentario)
        int commentId = 1;
        int postId = 123;

        // Mock: El servicio elimina el comentario sin lanzar excepción
        // (no necesitamos configurar when porque deleteComment es void)
        
        // Mock: Después de eliminar, la lista de comentarios no incluye el eliminado
        when(commentService.getCommentsByPost(postId))
                .thenReturn(List.of()); // Lista vacía después de eliminar

        // Act (When selecciona la opción para eliminar y confirma la acción)
        mockMvc.perform(delete("/api/comment/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Comment deleted Successfully"))
                .andExpect(jsonPath("$.success").value(true));

        // Assert (Then el comentario se elimina)
        verify(commentService).deleteComment(commentId);

        // Assert (And desaparece de la vista de todos los usuarios)
        mockMvc.perform(get("/api/posts/" + postId + "/comments"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }
}
