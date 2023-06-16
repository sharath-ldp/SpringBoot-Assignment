package com.springboot.blog.controller;

import com.springboot.blog.controller.CategoryController;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CategoryControllerTest {

    private CategoryController categoryController;

    @Mock
    private CategoryService categoryService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        categoryController = new CategoryController(categoryService);
    }

    @Test
    void addCategory_shouldReturnCreatedStatus() {
        // Prepare
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Test Category");
        categoryDto.setDescription("Test Description");

        CategoryDto savedCategory = new CategoryDto();
        savedCategory.setId(1L);
        savedCategory.setName("Test Category");
        savedCategory.setDescription("Test Description");

        when(categoryService.addCategory(categoryDto)).thenReturn(savedCategory);

        // Act
        ResponseEntity<CategoryDto> response = categoryController.addCategory(categoryDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedCategory, response.getBody());
    }

    @Test
    void getCategory_withExistingCategoryId_shouldReturnCategory() {
        // Prepare
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(categoryId);
        categoryDto.setName("Test Category");
        categoryDto.setDescription("Test Description");

        when(categoryService.getCategory(categoryId)).thenReturn(categoryDto);

        // Act
        ResponseEntity<CategoryDto> response = categoryController.getCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categoryDto, response.getBody());
    }

    @Test
    void getCategories_shouldReturnListOfCategories() {
        // Prepare
        List<CategoryDto> categories = new ArrayList<>();
        categories.add(new CategoryDto());
        categories.add(new CategoryDto());

        when(categoryService.getAllCategories()).thenReturn(categories);

        // Act
        ResponseEntity<List<CategoryDto>> response = categoryController.getCategories();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(categories, response.getBody());
    }

    @Test
    void updateCategory_withExistingCategoryId_shouldReturnUpdatedCategory() {
        // Prepare
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        categoryDto.setDescription("Updated Description");

        CategoryDto updatedCategory = new CategoryDto();
        updatedCategory.setId(categoryId);
        updatedCategory.setName("Updated Category");
        updatedCategory.setDescription("Updated Description");

        when(categoryService.updateCategory(categoryDto, categoryId)).thenReturn(updatedCategory);

        // Act
        ResponseEntity<CategoryDto> response = categoryController.updateCategory(categoryDto, categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedCategory, response.getBody());
    }

    @Test
    void deleteCategory_withExistingCategoryId_shouldReturnOkStatus() {
        // Prepare
        Long categoryId = 1L;

        // Act
        ResponseEntity<String> response = categoryController.deleteCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Category deleted successfully!.", response.getBody());

        verify(categoryService, times(1)).deleteCategory(categoryId);
    }
}
