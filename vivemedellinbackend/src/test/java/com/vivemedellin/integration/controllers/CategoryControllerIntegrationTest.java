package com.vivemedellin.integration.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vivemedellin.integration.IntegrationTestBase;
import com.vivemedellin.models.Category;
import com.vivemedellin.payloads.CategoryDto;
import com.vivemedellin.repositories.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integración para CategoryController
 * Verifica la integración completa del flujo de categorías
 */
@DisplayName("Pruebas de Integración - CategoryController")
public class CategoryControllerIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    public void setUp() {
        categoryRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe crear una nueva categoría exitosamente")
    public void testCreateCategorySuccess() throws Exception {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryTitle("Deportes");
        categoryDto.setCategoryDescription("Categoría de eventos deportivos");

        mockMvc.perform(post("/api/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Deportes")))
                .andExpect(jsonPath("$.categoryDescription", equalTo("Categoría de eventos deportivos")));
    }

    @Test
    @DisplayName("Debe obtener todas las categorías")
    public void testGetAllCategoriesSuccess() throws Exception {
        // Crear categorías de prueba
        Category category1 = new Category();
        category1.setCategoryTitle("Música");
        category1.setCategoryDescription("Eventos musicales");
        categoryRepo.save(category1);

        Category category2 = new Category();
        category2.setCategoryTitle("Arte");
        category2.setCategoryDescription("Eventos artísticos");
        categoryRepo.save(category2);

        mockMvc.perform(get("/api/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].categoryTitle", notNullValue()))
                .andExpect(jsonPath("$[1].categoryTitle", notNullValue()));
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID")
    public void testGetCategoryByIdSuccess() throws Exception {
        Category category = new Category();
        category.setCategoryTitle("Gastronomía");
        category.setCategoryDescription("Eventos gastronómicos");
        Category savedCategory = categoryRepo.save(category);

        mockMvc.perform(get("/api/categories/{categoryId}", savedCategory.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Gastronomía")));
    }

    @Test
    @DisplayName("Debe retornar 404 cuando la categoría no existe")
    public void testGetCategoryByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/categories/999")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Debe actualizar una categoría existente")
    public void testUpdateCategorySuccess() throws Exception {
        Category category = new Category();
        category.setCategoryTitle("Cine");
        category.setCategoryDescription("Proyecciones de películas");
        Category savedCategory = categoryRepo.save(category);

        CategoryDto updateDto = new CategoryDto();
        updateDto.setCategoryTitle("Cine Actualizado");
        updateDto.setCategoryDescription("Proyecciones de películas actualizadas");

        mockMvc.perform(put("/api/categories/{categoryId}", savedCategory.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.categoryTitle", equalTo("Cine Actualizado")));
    }

    @Test
    @DisplayName("Debe eliminar una categoría")
    public void testDeleteCategorySuccess() throws Exception {
        Category category = new Category();
        category.setCategoryTitle("Teatro");
        category.setCategoryDescription("Funciones teatrales");
        Category savedCategory = categoryRepo.save(category);

        mockMvc.perform(delete("/api/categories/{categoryId}", savedCategory.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // Verificar que fue eliminada
        mockMvc.perform(get("/api/categories/{categoryId}", savedCategory.getCategoryId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
