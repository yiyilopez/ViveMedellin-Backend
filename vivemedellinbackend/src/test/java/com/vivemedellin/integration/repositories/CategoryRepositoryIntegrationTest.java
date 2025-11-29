package com.vivemedellin.integration.repositories;

import com.vivemedellin.models.Category;
import com.vivemedellin.repositories.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para CategoryRepository
 * Verifica la interacción directa con la base de datos
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Pruebas de Integración - CategoryRepository")
public class CategoryRepositoryIntegrationTest {

    @Autowired
    private CategoryRepo categoryRepo;

    @BeforeEach
    public void setUp() {
        categoryRepo.deleteAll();
    }

    @Test
    @DisplayName("Debe guardar una categoría en la base de datos")
    public void testSaveCategorySuccess() {
        Category category = new Category();
        category.setCategoryTitle("Música");
        category.setCategoryDescription("Eventos musicales");

        Category savedCategory = categoryRepo.save(category);

        assertNotNull(savedCategory.getCategoryId());
        assertEquals("Música", savedCategory.getCategoryTitle());
    }

    @Test
    @DisplayName("Debe encontrar una categoría por ID")
    public void testFindCategoryByIdSuccess() {
        Category category = new Category();
        category.setCategoryTitle("Arte");
        category.setCategoryDescription("Eventos artísticos");
        Category savedCategory = categoryRepo.save(category);

        Optional<Category> foundCategory = categoryRepo.findById(savedCategory.getCategoryId());

        assertTrue(foundCategory.isPresent());
        assertEquals("Arte", foundCategory.get().getCategoryTitle());
    }

    @Test
    @DisplayName("Debe retornar vacío cuando la categoría no existe")
    public void testFindNonExistentCategoryReturnsEmpty() {
        Optional<Category> notFound = categoryRepo.findById(999);
        assertFalse(notFound.isPresent());
    }

    @Test
    @DisplayName("Debe obtener todas las categorías")
    public void testFindAllCategoriesSuccess() {
        Category category1 = new Category();
        category1.setCategoryTitle("Deportes");
        category1.setCategoryDescription("Eventos deportivos");
        categoryRepo.save(category1);

        Category category2 = new Category();
        category2.setCategoryTitle("Gastronomía");
        category2.setCategoryDescription("Eventos gastronómicos");
        categoryRepo.save(category2);

        List<Category> allCategories = categoryRepo.findAll();

        assertNotNull(allCategories);
        assertEquals(2, allCategories.size());
    }

    @Test
    @DisplayName("Debe actualizar una categoría")
    public void testUpdateCategorySuccess() {
        Category category = new Category();
        category.setCategoryTitle("Cine");
        category.setCategoryDescription("Proyecciones");
        Category savedCategory = categoryRepo.save(category);

        savedCategory.setCategoryTitle("Cine Actualizado");
        Category updatedCategory = categoryRepo.save(savedCategory);

        assertEquals("Cine Actualizado", updatedCategory.getCategoryTitle());
    }

    @Test
    @DisplayName("Debe eliminar una categoría")
    public void testDeleteCategorySuccess() {
        Category category = new Category();
        category.setCategoryTitle("Teatro");
        category.setCategoryDescription("Funciones teatrales");
        Category savedCategory = categoryRepo.save(category);

        categoryRepo.deleteById(savedCategory.getCategoryId());

        Optional<Category> deletedCategory = categoryRepo.findById(savedCategory.getCategoryId());
        assertFalse(deletedCategory.isPresent());
    }

    @Test
    @DisplayName("Debe contar todas las categorías")
    public void testCountCategoriesSuccess() {
        Category category1 = new Category();
        category1.setCategoryTitle("Categoría 1");
        category1.setCategoryDescription("Descripción 1");
        categoryRepo.save(category1);

        Category category2 = new Category();
        category2.setCategoryTitle("Categoría 2");
        category2.setCategoryDescription("Descripción 2");
        categoryRepo.save(category2);

        long count = categoryRepo.count();

        assertEquals(2, count);
    }

    @Test
    @DisplayName("Debe verificar si existe una categoría")
    public void testExistsByCategoryIdSuccess() {
        Category category = new Category();
        category.setCategoryTitle("Existente");
        category.setCategoryDescription("Prueba de existencia");
        Category savedCategory = categoryRepo.save(category);

        assertTrue(categoryRepo.existsById(savedCategory.getCategoryId()));
        assertFalse(categoryRepo.existsById(999));
    }
}
