package com.springboot.blog.controller;

import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.service.PostService;
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

public class PostControllerTest {

    private PostController postController;

    @Mock
    private PostService postService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        postController = new PostController(postService);
    }

    @Test
    void createPost_shouldReturnCreatedStatus() {
        // Prepare
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Post");
        postDto.setContent("Test Content");

        PostDto savedPost = new PostDto();
        savedPost.setId(1L);
        savedPost.setTitle("Test Post");
        savedPost.setContent("Test Content");

        when(postService.createPost(postDto)).thenReturn(savedPost);

        // Act
        ResponseEntity<PostDto> response = postController.createPost(postDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedPost, response.getBody());
    }

    @Test
    void getAllPosts_shouldReturnPostList() {
        // Prepare
        int pageNo = 1;
        int pageSize = 10;
        String sortBy = "title";
        String sortDir = "asc";

        List<PostDto> posts = new ArrayList<>();
        posts.add(new PostDto());
        posts.add(new PostDto());

        PostResponse postResponse = new PostResponse(posts, pageNo, pageSize, posts.size(), 1, true);

        when(postService.getAllPosts(pageNo, pageSize, sortBy, sortDir)).thenReturn(postResponse);

        // Act
        PostResponse response = postController.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        // Assert
        assertEquals(posts, response.getContent());
        assertEquals(pageNo, response.getPageNo());
        assertEquals(pageSize, response.getPageSize());
        assertEquals(posts.size(), response.getTotalElements());
        assertEquals(1, response.getTotalPages());
        assertEquals(true, response.isLast());
    }

    @Test
    void getPostById_withExistingId_shouldReturnPost() {
        // Prepare
        long postId = 1L;

        PostDto postDto = new PostDto();
        postDto.setId(postId);
        postDto.setTitle("Test Post");
        postDto.setContent("Test Content");

        when(postService.getPostById(postId)).thenReturn(postDto);

        // Act
        ResponseEntity<PostDto> response = postController.getPostById(postId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());
    }

    @Test
    void updatePost_withExistingId_shouldReturnUpdatedPost() {
        // Prepare
        long postId = 1L;

        PostDto postDto = new PostDto();
        postDto.setTitle("Updated Post");
        postDto.setContent("Updated Content");

        PostDto updatedPost = new PostDto();
        updatedPost.setId(postId);
        updatedPost.setTitle("Updated Post");
        updatedPost.setContent("Updated Content");

        when(postService.updatePost(postDto, postId)).thenReturn(updatedPost);

        // Act
        ResponseEntity<PostDto> response = postController.updatePost(postDto, postId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPost, response.getBody());
    }

    @Test
    void deletePost_withExistingId_shouldReturnSuccessMessage() {
        // Prepare
        long postId = 1L;

        // Act
        ResponseEntity<String> response = postController.deletePost(postId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Post entity deleted successfully.", response.getBody());
        verify(postService, times(1)).deletePostById(postId);
    }
    @Test
    void getPostsByCategory_withExistingCategoryId_shouldReturnPostList() {
        // Prepare
        Long categoryId = 1L;

        List<PostDto> postDtos = new ArrayList<>();
        postDtos.add(new PostDto());
        postDtos.add(new PostDto());

        when(postService.getPostsByCategory(categoryId)).thenReturn(postDtos);

        // Act
        ResponseEntity<List<PostDto>> response = postController.getPostsByCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDtos, response.getBody());
    }
}
