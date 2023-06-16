package com.springboot.blog.controller;

import com.springboot.blog.controller.CommentController;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.service.CommentService;
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

public class CommentControllerTest {

    private CommentController commentController;

    @Mock
    private CommentService commentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentController = new CommentController(commentService);
    }

    @Test
    void createComment_shouldReturnCreatedStatus() {
        // Prepare
        long postId = 1L;
        CommentDto commentDto = new CommentDto();
        commentDto.setName("Test Comment");
        commentDto.setEmail("test@example.com");
        commentDto.setBody("Test Comment Body");

        CommentDto savedComment = new CommentDto();
        savedComment.setId(1L);
        savedComment.setName("Test Comment");
        savedComment.setEmail("test@example.com");
        savedComment.setBody("Test Comment Body");

        when(commentService.createComment(postId, commentDto)).thenReturn(savedComment);

        // Act
        ResponseEntity<CommentDto> response = commentController.createComment(postId, commentDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedComment, response.getBody());
    }

    @Test
    void getCommentsByPostId_shouldReturnListOfComments() {
        // Prepare
        long postId = 1L;
        List<CommentDto> comments = new ArrayList<>();
        comments.add(new CommentDto());
        comments.add(new CommentDto());

        when(commentService.getCommentsByPostId(postId)).thenReturn(comments);

        // Act
        List<CommentDto> response = commentController.getCommentsByPostId(postId);

        // Assert
        assertEquals(comments, response);
    }

    @Test
    void getCommentById_withExistingPostIdAndCommentId_shouldReturnComment() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setId(commentId);
        commentDto.setName("Test Comment");
        commentDto.setEmail("test@example.com");
        commentDto.setBody("Test Comment Body");

        when(commentService.getCommentById(postId, commentId)).thenReturn(commentDto);

        // Act
        ResponseEntity<CommentDto> response = commentController.getCommentById(postId, commentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(commentDto, response.getBody());
    }

    @Test
    void updateComment_withExistingPostIdAndCommentId_shouldReturnUpdatedComment() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;

        CommentDto commentDto = new CommentDto();
        commentDto.setName("Updated Comment");
        commentDto.setEmail("updated@example.com");
        commentDto.setBody("Updated Comment Body");

        CommentDto updatedComment = new CommentDto();
        updatedComment.setId(commentId);
        updatedComment.setName("Updated Comment");
        updatedComment.setEmail("updated@example.com");
        updatedComment.setBody("Updated Comment Body");

        when(commentService.updateComment(postId, commentId, commentDto)).thenReturn(updatedComment);

        // Act
        ResponseEntity<CommentDto> response = commentController.updateComment(postId, commentId, commentDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedComment, response.getBody());
    }

    @Test
    void deleteComment_withExistingPostIdAndCommentId_shouldReturnOkStatus() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;

        // Act
        ResponseEntity<String> response = commentController.deleteComment(postId, commentId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Comment deleted successfully", response.getBody());

        verify(commentService, times(1)).deleteComment(postId, commentId);
    }
}
