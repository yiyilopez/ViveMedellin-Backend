package com.vivemedellin.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.models.Category;
import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.repositories.CategoryRepo;
import com.vivemedellin.repositories.CommentRepo;
import com.vivemedellin.repositories.PostRepo;
import com.vivemedellin.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CommentController
 * Verifica la integración completa del flujo de comentarios
 */
@DisplayName("Pruebas de Integración - CommentController")
public class CommentControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    private Post post;
    private User user;
    private Category category;

    @BeforeEach
    public void setUp() {
        commentRepo.deleteAll();
        postRepo.deleteAll();
        userRepo.deleteAll();
        categoryRepo.deleteAll();

        // Crear datos de prueba
        category = new Category();
        category.setCategoryTitle("Eventos");
        category.setCategoryDescription("Eventos variados");
        category = categoryRepo.save(category);

        user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        user = userRepo.save(user);

        post = new Post();
        post.setPostTitle("Evento de Prueba");
        post.setPostContent("Descripción del evento");
        post.setCategory(category);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        post = postRepo.save(post);
    }

    @Test
    @DisplayName("Debe crear un nuevo comentario exitosamente")
    public void testCreateCommentSuccess() throws Exception {
        CommentDto commentDto = new CommentDto();
        commentDto.setCommentContent("Excelente evento!");
        commentDto.setPostId(post.getPostId());
        commentDto.setUserId(user.getUserId());

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(commentDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentContent", equalTo("Excelente evento!")))
                .andExpect(jsonPath("$.postId", equalTo(post.getPostId().intValue())))
                .andExpect(jsonPath("$.userId", equalTo(user.getUserId().intValue())));
    }

    @Test
    @DisplayName("Debe obtener todos los comentarios")
    public void testGetAllCommentsSuccess() throws Exception {
        // Crear comentarios de prueba
        Comment comment1 = new Comment();
        comment1.setCommentContent("Primer comentario");
        comment1.setPost(post);
        comment1.setUser(user);
        comment1.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment1);

        Comment comment2 = new Comment();
        comment2.setCommentContent("Segundo comentario");
        comment2.setPost(post);
        comment2.setUser(user);
        comment2.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment2);

        mockMvc.perform(get("/api/comments")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Debe obtener un comentario por ID")
    public void testGetCommentByIdSuccess() throws Exception {
        Comment comment = new Comment();
        comment.setCommentContent("Comentario de prueba");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepo.save(comment);

        mockMvc.perform(get("/api/comments/{commentId}", savedComment.getCommentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentContent", equalTo("Comentario de prueba")));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el comentario no existe")
    public void testGetCommentByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/comments/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe actualizar un comentario existente")
    public void testUpdateCommentSuccess() throws Exception {
        Comment comment = new Comment();
        comment.setCommentContent("Comentario original");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepo.save(comment);

        CommentDto updateDto = new CommentDto();
        updateDto.setCommentContent("Comentario actualizado");
        updateDto.setPostId(post.getPostId());
        updateDto.setUserId(user.getUserId());

        mockMvc.perform(put("/api/comments/{commentId}", savedComment.getCommentId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentContent", equalTo("Comentario actualizado")));
    }

    @Test
    @DisplayName("Debe eliminar un comentario")
    public void testDeleteCommentSuccess() throws Exception {
        Comment comment = new Comment();
        comment.setCommentContent("Comentario a eliminar");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        Comment savedComment = commentRepo.save(comment);

        mockMvc.perform(delete("/api/comments/{commentId}", savedComment.getCommentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/api/comments/{commentId}", savedComment.getCommentId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe obtener comentarios por post")
    public void testGetCommentsByPostSuccess() throws Exception {
        Comment comment = new Comment();
        comment.setCommentContent("Comentario para este post");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment);

        mockMvc.perform(get("/api/comments/post/{postId}", post.getPostId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Debe obtener comentarios por usuario")
    public void testGetCommentsByUserSuccess() throws Exception {
        Comment comment = new Comment();
        comment.setCommentContent("Comentario de este usuario");
        comment.setPost(post);
        comment.setUser(user);
        comment.setCreatedAt(LocalDateTime.now());
        commentRepo.save(comment);

        mockMvc.perform(get("/api/comments/user/{userId}", user.getUserId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Debe rechazar comentario sin contenido")
    public void testCreateCommentWithoutContentFails() throws Exception {
        CommentDto invalidComment = new CommentDto();
        invalidComment.setCommentContent("");  // Contenido vacío
        invalidComment.setPostId(post.getPostId());
        invalidComment.setUserId(user.getUserId());

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Debe rechazar comentario con post inexistente")
    public void testCreateCommentWithInvalidPostFails() throws Exception {
        CommentDto invalidComment = new CommentDto();
        invalidComment.setCommentContent("Comentario sin post válido");
        invalidComment.setPostId(99999L);
        invalidComment.setUserId(user.getUserId());

        mockMvc.perform(post("/api/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidComment)))
                .andExpect(status().isNotFound());
    }
}
