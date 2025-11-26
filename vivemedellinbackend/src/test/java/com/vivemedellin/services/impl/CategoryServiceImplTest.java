package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Category;
import com.vivemedellin.payloads.CategoryDto;
import com.vivemedellin.repositories.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceImplTest {

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepo categoryRepo;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateCategory() {
        CategoryDto dto = new CategoryDto();
        dto.setCategoryTitle("Title");
        dto.setCategoryDescription("Desc");

        Category cat = new Category();
        Category savedCat = new Category();
        savedCat.setCategoryTitle("Title");
        savedCat.setCategoryDescription("Desc");

        when(modelMapper.map(dto, Category.class)).thenReturn(cat);
        when(categoryRepo.save(cat)).thenReturn(savedCat);
        when(modelMapper.map(savedCat, CategoryDto.class)).thenReturn(dto);

        CategoryDto result = categoryService.createCategory(dto);

        assertEquals("Title", result.getCategoryTitle());
        verify(categoryRepo, times(1)).save(cat);
    }

    @Test
    void testGetCategoryFound() {
        Category cat = new Category();
        cat.setCategoryTitle("Title");
        cat.setCategoryDescription("Desc");

        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));
        when(modelMapper.map(cat, CategoryDto.class)).thenReturn(new CategoryDto());

        CategoryDto dto = categoryService.getCategory(1);
        assertNotNull(dto);
        verify(categoryRepo).findById(1);
    }

    @Test
    void testGetCategoryNotFound() {
        when(categoryRepo.findById(1)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategory(1));
    }

    @Test
    void testGetAllCategories() {
        Category cat1 = new Category();
        Category cat2 = new Category();

        when(categoryRepo.findAll()).thenReturn(Arrays.asList(cat1, cat2));
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class)))
                .thenReturn(new CategoryDto());

        List<CategoryDto> list = categoryService.getAllCategories();

        assertEquals(2, list.size());
        verify(categoryRepo).findAll();
    }

    @Test
    void testDeleteCategory() {
        Category cat = new Category();
        when(categoryRepo.findById(1)).thenReturn(Optional.of(cat));

        categoryService.deleteCategory(1);

        verify(categoryRepo).delete(cat);
    }
}
