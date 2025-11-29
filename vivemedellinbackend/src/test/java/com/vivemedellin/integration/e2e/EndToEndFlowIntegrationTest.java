package com.vivemedellin.integration.e2e;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.payloads.CategoryDto;
import com.vivemedellin.payloads.PostDto;
import com.vivemedellin.payloads.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Prueba de integración End-to-End (E2E)
 * Simula un flujo completo de usuario creando eventos, categorías y más
 */
@DisplayName("Pruebas E2E - Flujo Completo del Sistema")
public class EndToEndFlowIntegrationTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setUp() {
        // Aquí se inicializa el contexto de prueba
    }

    @Test
    @DisplayName("Debe realizar un flujo completo: Crear usuario → Categoría → Evento")
    public void testCompleteUserFlowE2E() throws Exception {
        // PASO 1: Crear un usuario
        UserDto newUser = new UserDto();
        newUser.setUserName("newuser");
        newUser.setUserEmail("newuser@example.com");
        newUser.setUserPassword("SecurePassword123!");

        MvcResult userResult = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.userName", equalTo("newuser")))
                .andExpect(jsonPath("$.userEmail", equalTo("newuser@example.com")))
                .andReturn();

        // PASO 2: Crear una categoría
        CategoryDto newCategory = new CategoryDto();
        newCategory.setCategoryTitle("Eventos Culturales");
        newCategory.setCategoryDescription("Eventos relacionados con la cultura de Medellín");

        MvcResult categoryResult = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Eventos Culturales")))
                .andReturn();

        // Extraer ID de la categoría creada
        String categoryResponse = categoryResult.getResponse().getContentAsString();
        int categoryId = objectMapper.readTree(categoryResponse).get("categoryId").asInt();

        // PASO 3: Obtener la categoría creada
        mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Eventos Culturales")));

        // PASO 4: Crear un evento/post en esa categoría
        PostDto newPost = new PostDto();
        newPost.setPostTitle("Festival de Música Medellín 2025");
        newPost.setPostContent("Un increíble festival de música con artistas internacionales");
        newPost.setCategoryId((long) categoryId);

        MvcResult postResult = mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newPost)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.postTitle", equalTo("Festival de Música Medellín 2025")))
                .andExpect(jsonPath("$.categoryId", equalTo(categoryId)))
                .andReturn();

        // Extraer ID del post creado
        String postResponse = postResult.getResponse().getContentAsString();
        int postId = objectMapper.readTree(postResponse).get("postId").asInt();

        // PASO 5: Obtener el post creado
        mockMvc.perform(get("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle", equalTo("Festival de Música Medellín 2025")));

        // PASO 6: Obtener todos los posts de esa categoría
        mockMvc.perform(get("/api/posts/category/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // PASO 7: Actualizar el post
        PostDto updatePost = new PostDto();
        updatePost.setPostTitle("Festival de Música Medellín 2025 - ACTUALIZADO");
        updatePost.setPostContent("Festival con nuevos artistas confirmados");
        updatePost.setCategoryId((long) categoryId);

        mockMvc.perform(put("/api/posts/{postId}", postId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatePost)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postTitle", equalTo("Festival de Música Medellín 2025 - ACTUALIZADO")));

        // PASO 8: Obtener todas las categorías
        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // PASO 9: Obtener todos los usuarios
        mockMvc.perform(get("/api/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // PASO 10: Obtener todos los posts
        mockMvc.perform(get("/api/posts")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    @DisplayName("Debe validar restricciones de datos en el flujo E2E")
    public void testDataValidationE2E() throws Exception {
        // Intentar crear usuario sin email
        UserDto invalidUser = new UserDto();
        invalidUser.setUserName("usernomail");
        invalidUser.setUserPassword("password123");

        mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest());

        // Intentar crear categoría sin título
        CategoryDto invalidCategory = new CategoryDto();
        invalidCategory.setCategoryDescription("Descripción sin título");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidCategory)))
                .andExpect(status().isBadRequest());

        // Intentar crear post con categoría inexistente
        PostDto invalidPost = new PostDto();
        invalidPost.setPostTitle("Evento Inválido");
        invalidPost.setPostContent("Contenido del evento");
        invalidPost.setCategoryId(99999L);

        mockMvc.perform(post("/api/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidPost)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe manejar operaciones CRUD completas en flujo E2E")
    public void testCompleteCRUDFlowE2E() throws Exception {
        // CREATE
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryTitle("Categoría CRUD");
        categoryDto.setCategoryDescription("Para prueba CRUD");

        MvcResult createResult = mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andReturn();

        int categoryId = objectMapper.readTree(createResult.getResponse().getContentAsString())
                .get("categoryId").asInt();

        // READ
        mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Categoría CRUD")));

        // UPDATE
        CategoryDto updateDto = new CategoryDto();
        updateDto.setCategoryTitle("Categoría CRUD Actualizada");
        updateDto.setCategoryDescription("Descripción actualizada para prueba CRUD");

        mockMvc.perform(put("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Categoría CRUD Actualizada")));

        // DELETE
        mockMvc.perform(delete("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que fue eliminado
        mockMvc.perform(get("/api/categories/{categoryId}", categoryId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
