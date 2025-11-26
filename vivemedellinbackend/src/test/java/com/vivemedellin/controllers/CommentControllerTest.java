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

import org.springframework.security.access.AccessDeniedException;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureTestDatabase
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
//    @Test
//    void shouldRejectCommentCreation_whenUserNotAuthenticated() throws Exception {
//        int postId = 456;
//
//        CommentDto requestDto = new CommentDto();
//        requestDto.setContent("Comentario sin autenticación");
//
  //      mockMvc.perform(post("/api/posts/" + postId + "/comments")
//                        .contentType(MediaType.APPLICATION_JSON)
 //                       .content(objectMapper.writeValueAsString(requestDto)))
 //               .andExpect(status().isUnauthorized());
  //  }

    // Test: Eliminación exitosa de un comentario propio (CP-004)
    @Test
    @WithMockUser(username = "usuario@test.com", roles = {"USER"})
    void shouldDeleteOwnComment_whenAuthenticatedUserIsAuthor() throws Exception {
        // Arrange (Given que el usuario está autenticado en la plataforma
        // And es el autor del comentario)
        int commentId = 1;

        // Actualmente el servicio
        // no recibe Principal, pero en una implementación completa debería validarlo.
        // Este test verifica que el controlador delega correctamente al servicio.

        // Act (When selecciona la opción para eliminar y confirma la acción)
        mockMvc.perform(delete("/api/comment/" + commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Comment deleted Successfully"))
                .andExpect(jsonPath("$.success").value(true));

        // Assert (Then el comentario se elimina)
        // Verificamos que el servicio fue llamado con el commentId correcto
        verify(commentService).deleteComment(commentId);
    }

    // Test: Rechazo de eliminación de un comentario ajeno
    @Test
    @WithMockUser(username = "usuario@test.com", roles = {"USER"})
    void shouldRejectDeletion_whenUserIsNotAuthor() throws Exception {
        // Arrange
        int commentId = 1;
        String errorMessage = "No tienes permiso para eliminar este comentario";

        // Mock: el servicio lanza una excepción de autorización
        doThrow(new AccessDeniedException(errorMessage))
                .when(commentService).deleteComment(commentId);

        // Act & Assert
        mockMvc.perform(delete("/api/comment/{id}", commentId))
                .andExpect(status().isForbidden())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value(errorMessage))
                .andExpect(jsonPath("$.status").value("403 FORBIDDEN"));

        // Verify
        verify(commentService).deleteComment(commentId);
    }

    // Test: Administrador elimina un comentario exitosamente (de cualquier usuario)
    @Test
    @WithMockUser(username = "admin@test.com", roles = {"ADMIN"})
    void shouldAllowAdminToDeleteAnyComment_whenAuthenticated() throws Exception {
        // Arrange
        int commentId = 1;
        doNothing().when(commentService).deleteComment(commentId);

        // Act & Assert
        mockMvc.perform(delete("/api/comment/{id}", commentId))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.message").value("Comment deleted Successfully"))
                .andExpect(jsonPath("$.success").value(true));

        // Verify
        verify(commentService).deleteComment(commentId);
    }

    // Test: Usuario autenticado edita con éxito un comentario propio
    @Test
    @WithMockUser(username = "usuario@test.com", roles = {"USER"})
    void shouldUpdateOwnComment_whenAuthenticatedUserIsAuthor() throws Exception {
        // Arrange (Given que el usuario está autenticado en la plataforma
        // And es el autor del comentario)
        int commentId = 1;
        String updatedContent = "Comentario actualizado";

        CommentDto updateDto = new CommentDto();
        updateDto.setContent(updatedContent);

        CommentDto updatedDto = new CommentDto();
        updatedDto.setId(commentId);
        updatedDto.setContent(updatedContent);

        // Mock: El servicio actualiza el comentario exitosamente
        when(commentService.updateComment(any(CommentDto.class), eq(commentId), any()))
                .thenReturn(updatedDto);

        // Act (When el usuario edita el comentario y confirma los cambios)
        mockMvc.perform(put("/api/comment/{id}", commentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(commentId))
                .andExpect(jsonPath("$.content").value(updatedContent));

        // Assert (Then el comentario se actualiza exitosamente)
        // Verificamos que el servicio fue llamado con los parámetros correctos
        verify(commentService).updateComment(any(CommentDto.class), eq(commentId), any());
    }
}
