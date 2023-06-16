package com.springboot.blog.service;

import com.springboot.blog.entity.Category;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CategoryDto;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.service.impl.CategoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CategoryServiceImplTest {

    private CategoryRepository categoryRepository;
    private ModelMapper modelMapper;
    private CategoryServiceImpl categoryService;

    @BeforeEach
    void setUp() {
        categoryRepository = mock(CategoryRepository.class);
        modelMapper = mock(ModelMapper.class);
        categoryService = new CategoryServiceImpl(categoryRepository, modelMapper);
    }

    @Test
    void addCategory_shouldReturnAddedCategory() {
        // Prepare
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Test Category");
        categoryDto.setDescription("Test Description");

        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Test Category");
        savedCategory.setDescription("Test Description");

        when(modelMapper.map(categoryDto, Category.class)).thenReturn(savedCategory);
        when(categoryRepository.save(savedCategory)).thenReturn(savedCategory);
        when(modelMapper.map(savedCategory, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.addCategory(categoryDto);

        // Assert
        assertEquals(savedCategory.getName(), result.getName());
        assertEquals(savedCategory.getDescription(), result.getDescription());
    }

    @Test
    void getCategory_withExistingCategoryId_shouldReturnCategory() {
        // Prepare
        Long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);
        category.setName("Test Category");
        category.setDescription("Test Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(new CategoryDto());

        // Act
        CategoryDto result = categoryService.getCategory(categoryId);

        // Assert
        assertNotNull(result);
    }

    @Test
    void getCategory_withNonExistingCategoryId_shouldThrowResourceNotFoundException() {
        // Prepare
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategory(categoryId));
    }

    @Test
    void getAllCategories_withExistingCategories_shouldReturnListOfCategories() {
        // Prepare
        List<Category> categories = new ArrayList<>();
        categories.add(new Category());
        categories.add(new Category());

        when(categoryRepository.findAll()).thenReturn(categories);
        when(modelMapper.map(any(Category.class), eq(CategoryDto.class))).thenReturn(new CategoryDto());

        // Act
        List<CategoryDto> result = categoryService.getAllCategories();

        // Assert
        assertNotNull(result);
        assertEquals(categories.size(), result.size());
    }

    @Test
    void updateCategory_withExistingCategoryId_shouldReturnUpdatedCategory() {
        // Prepare
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        categoryDto.setDescription("Updated Description");

        Category category = new Category();
        category.setId(categoryId);
        category.setName("Original Category");
        category.setDescription("Original Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(categoryRepository.save(category)).thenReturn(category);
        when(modelMapper.map(category, CategoryDto.class)).thenReturn(categoryDto);

        // Act
        CategoryDto result = categoryService.updateCategory(categoryDto, categoryId);

        // Assert
        assertNotNull(result);
        assertEquals(categoryDto.getName(), result.getName());
        assertEquals(categoryDto.getDescription(), result.getDescription());
    }

    @Test
    void updateCategory_withNonExistingCategoryId_shouldThrowResourceNotFoundException() {
        // Prepare
        Long categoryId = 1L;
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Updated Category");
        categoryDto.setDescription("Updated Description");

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.updateCategory(categoryDto, categoryId));
    }

    @Test
    void deleteCategory_withExistingCategoryId_shouldDeleteCategory() {
        // Prepare
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(new Category()));

        // Act
        categoryService.deleteCategory(categoryId);

        // Assert
        verify(categoryRepository, times(1)).delete(any(Category.class));
    }

    @Test
    void deleteCategory_withNonExistingCategoryId_shouldThrowResourceNotFoundException() {
        // Prepare
        Long categoryId = 1L;

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.deleteCategory(categoryId));
    }
}
