package com.vivemedellin.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.models.Category;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.PostDto;
import com.vivemedellin.repositories.CategoryRepo;
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
 * Pruebas de integración para PostController
 * Verifica la integración completa del flujo de posts/eventos
 */
@DisplayName("Pruebas de Integración - PostController")
public class PostControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private CategoryRepo categoryRepo;

    @Autowired
    private UserRepo userRepo;

    private Category category;
    private User user;

    @BeforeEach
    public void setUp() {
        postRepo.deleteAll();
        categoryRepo.deleteAll();
        userRepo.deleteAll();

        // Crear datos de prueba
        category = new Category();
        category.setCategoryTitle("Conciertos");
        category.setCategoryDescription("Eventos musicales");
        category = categoryRepo.save(category);

        user = new User();
        user.setUserName("testuser");
        user.setUserEmail("test@example.com");
        user.setUserPassword("password123");
        user = userRepo.save(user);
    }

    @Test
    @DisplayName("Debe crear un nuevo post/evento exitosamente")
    public void testCreatePostSuccess() throws Exception {
        PostDto postDto = new PostDto();
        postDto.setPostTitle("Concierto de Rock");
        postDto.setPostContent("Un increíble concierto de rock en Medellín");
        postDto.setCategoryId(category.getCategoryId());

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postTitle", equalTo("Concierto de Rock")))
                .andExpect(jsonPath("$.categoryId", equalTo(category.getCategoryId().intValue())));
    }

    @Test
    @DisplayName("Debe obtener todos los posts")
    public void testGetAllPostsSuccess() throws Exception {
        // Crear posts de prueba
        Post post1 = new Post();
        post1.setPostTitle("Evento 1");
        post1.setPostContent("Contenido del evento 1");
        post1.setCategory(category);
        post1.setUser(user);
        post1.setCreatedAt(LocalDateTime.now());
        postRepo.save(post1);

        Post post2 = new Post();
        post2.setPostTitle("Evento 2");
        post2.setPostContent("Contenido del evento 2");
        post2.setCategory(category);
        post2.setUser(user);
        post2.setCreatedAt(LocalDateTime.now());
        postRepo.save(post2);

        mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("Debe obtener un post por ID")
    public void testGetPostByIdSuccess() throws Exception {
        Post post = new Post();
        post.setPostTitle("Festival de Música");
        post.setPostContent("Descripción del festival");
        post.setCategory(category);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepo.save(post);

        mockMvc.perform(get("/api/posts/{postId}", savedPost.getPostId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle", equalTo("Festival de Música")));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando el post no existe")
    public void testGetPostByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/posts/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe actualizar un post existente")
    public void testUpdatePostSuccess() throws Exception {
        Post post = new Post();
        post.setPostTitle("Evento Original");
        post.setPostContent("Contenido original");
        post.setCategory(category);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepo.save(post);

        PostDto updateDto = new PostDto();
        updateDto.setPostTitle("Evento Actualizado");
        updateDto.setPostContent("Contenido actualizado");
        updateDto.setCategoryId(category.getCategoryId());

        mockMvc.perform(put("/api/posts/{postId}", savedPost.getPostId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle", equalTo("Evento Actualizado")));
    }

    @Test
    @DisplayName("Debe eliminar un post")
    public void testDeletePostSuccess() throws Exception {
        Post post = new Post();
        post.setPostTitle("Evento a Eliminar");
        post.setPostContent("Será eliminado");
        post.setCategory(category);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        Post savedPost = postRepo.save(post);

        mockMvc.perform(delete("/api/posts/{postId}", savedPost.getPostId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/api/posts/{postId}", savedPost.getPostId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe obtener posts por categoría")
    public void testGetPostsByCategorySuccess() throws Exception {
        Post post = new Post();
        post.setPostTitle("Evento de Categoría");
        post.setPostContent("Evento especial");
        post.setCategory(category);
        post.setUser(user);
        post.setCreatedAt(LocalDateTime.now());
        postRepo.save(post);

        mockMvc.perform(get("/api/posts/category/{categoryId}", category.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
