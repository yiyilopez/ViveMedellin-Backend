package com.vivemedellin.integration.services;

import com.vivemedellin.models.Category;
import com.vivemedellin.payloads.CategoryDto;
import com.vivemedellin.repositories.CategoryRepo;
import com.vivemedellin.services.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para CategoryService
 * Verifica la lógica de negocio de categorías integrada con la base de datos
 */
@SpringBootTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración - CategoryService")
public class CategoryServiceIntegrationTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    public void setUp() {
        categoryRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe crear una categoría exitosamente")
    public void testCreateCategorySuccess() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setCategoryTitle("Deportes");
        categoryDto.setCategoryDescription("Eventos deportivos");

        CategoryDto createdCategory = categoryService.createCategory(categoryDto);

        assertNotNull(createdCategory);
        assertNotNull(createdCategory.getCategoryId());
        assertEquals("Deportes", createdCategory.getCategoryTitle());
        assertEquals("Eventos deportivos", createdCategory.getCategoryDescription());
    }

    @Test
    @DisplayName("Debe obtener una categoría por ID")
    public void testGetCategoryByIdSuccess() {
        // Crear categoría
        Category category = new Category();
        category.setCategoryTitle("Música");
        category.setCategoryDescription("Eventos musicales");
        Category savedCategory = categoryRepo.save(category);

        // Obtener categoría
        CategoryDto retrievedCategory = categoryService.getCategoryById(savedCategory.getCategoryId());

        assertNotNull(retrievedCategory);
        assertEquals("Música", retrievedCategory.getCategoryTitle());
    }

    @Test
    @DisplayName("Debe obtener todas las categorías")
    public void testGetAllCategoriesSuccess() {
        // Crear múltiples categorías
        Category category1 = new Category();
        category1.setCategoryTitle("Arte");
        category1.setCategoryDescription("Eventos artísticos");
        categoryRepo.save(category1);

        Category category2 = new Category();
        category2.setCategoryTitle("Gastronomía");
        category2.setCategoryDescription("Eventos gastronómicos");
        categoryRepo.save(category2);

        // Obtener todas
        List<CategoryDto> allCategories = categoryService.getAllCategories();

        assertNotNull(allCategories);
        assertEquals(2, allCategories.size());
    }

    @Test
    @DisplayName("Debe actualizar una categoría")
    public void testUpdateCategorySuccess() {
        // Crear categoría
        Category category = new Category();
        category.setCategoryTitle("Cine");
        category.setCategoryDescription("Proyecciones");
        Category savedCategory = categoryRepo.save(category);

        // Actualizar
        CategoryDto updateDto = new CategoryDto();
        updateDto.setCategoryTitle("Cine Actualizado");
        updateDto.setCategoryDescription("Proyecciones actualizadas");

        CategoryDto updatedCategory = categoryService.updateCategory(updateDto, savedCategory.getCategoryId());

        assertNotNull(updatedCategory);
        assertEquals("Cine Actualizado", updatedCategory.getCategoryTitle());
    }

    @Test
    @DisplayName("Debe eliminar una categoría")
    public void testDeleteCategorySuccess() {
        // Crear categoría
        Category category = new Category();
        category.setCategoryTitle("Teatro");
        category.setCategoryDescription("Funciones teatrales");
        Category savedCategory = categoryRepo.save(category);

        // Verificar que existe
        assertTrue(categoryRepo.existsById(savedCategory.getCategoryId()));

        // Eliminar
        categoryService.deleteCategory(savedCategory.getCategoryId());

        // Verificar que fue eliminada
        assertFalse(categoryRepo.existsById(savedCategory.getCategoryId()));
    }

    @Test
    @DisplayName("Debe lanzar excepción cuando la categoría no existe")
    public void testGetNonExistentCategoryThrowsException() {
        assertThrows(Exception.class, () -> {
            categoryService.getCategoryById(999);
        });
    }
}
