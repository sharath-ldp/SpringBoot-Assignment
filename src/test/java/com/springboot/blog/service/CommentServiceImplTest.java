package com.springboot.blog.service;

import com.springboot.blog.entity.Comment;
import com.springboot.blog.entity.Post;
import com.springboot.blog.exception.BlogAPIException;
import com.springboot.blog.exception.ResourceNotFoundException;
import com.springboot.blog.payload.CommentDto;
import com.springboot.blog.repository.CommentRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CommentServiceImplTest {

    private CommentServiceImpl commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        commentService = new CommentServiceImpl(commentRepository, postRepository, modelMapper);
    }

    @Test
    void createComment_withExistingPostId_shouldReturnCreatedCommentDto() {
        // Prepare
        long postId = 1L;
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Post post = new Post();

        when(modelMapper.map(commentDto, Comment.class)).thenReturn(comment);
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // Act
        CommentDto createdComment = commentService.createComment(postId, commentDto);

        // Assert
        assertNotNull(createdComment);
        assertEquals(commentDto, createdComment);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).save(comment);
        verify(modelMapper, times(1)).map(comment,CommentDto.class);
//        verify(comment).setPost(post);
//        verify(commentRepository).save(comment);
    }

    @Test
    void createComment_withNonExistingPostId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        CommentDto commentDto = new CommentDto();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment(postId, commentDto));
        verify(commentRepository, never()).save(any());
    }

    @Test
    void getCommentsByPostId_withExistingPostId_shouldReturnCommentDtoList() {
        // Prepare
        long postId = 1L;
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        List<Comment> comments = new ArrayList<>();
        comments.add(comment1);
        comments.add(comment2);
        List<CommentDto> commentDtos = new ArrayList<>();
        commentDtos.add(new CommentDto());
        commentDtos.add(new CommentDto());

        when(commentRepository.findByPostId(postId)).thenReturn(comments);
        when(modelMapper.map(comment1, CommentDto.class)).thenReturn(commentDtos.get(0));
        when(modelMapper.map(comment2, CommentDto.class)).thenReturn(commentDtos.get(1));

        // Act
        List<CommentDto> retrievedComments = commentService.getCommentsByPostId(postId);

        // Assert
        assertNotNull(retrievedComments);
        assertEquals(commentDtos.size(), retrievedComments.size());
        assertEquals(commentDtos, retrievedComments);
        verify(modelMapper,times(2)).map(comment1,CommentDto.class);
       // verify(modelMapper,times(1)).map(comment2,CommentDto.class);
    }

    @Test
    void getCommentsByPostId_withNonExistingPostId_shouldReturnEmptyList() {
        // Prepare
        long postId = 1L;

        when(commentRepository.findByPostId(postId)).thenReturn(new ArrayList<>());

        // Act
        List<CommentDto> retrievedComments = commentService.getCommentsByPostId(postId);

        // Assert
        assertNotNull(retrievedComments);
        assertTrue(retrievedComments.isEmpty());
    }

    @Test
    void getCommentById_withExistingPostIdAndCommentId_shouldReturnCommentDto() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(new CommentDto());

        // Act
        CommentDto retrievedComment = commentService.getCommentById(postId, commentId);

        // Assert
        assertNotNull(retrievedComment);
        verify(modelMapper, times(1)).map(comment, CommentDto.class);
    }


    @Test
    void getCommentById_withNonExistingPostId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentById(postId, commentId));
        verify(commentRepository, never()).findById(commentId);
    }

    @Test
    void getCommentById_withNonExistingCommentId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment();
        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.getCommentById(postId, commentId));

    }

    @Test
    void getCommentById_withCommentNotBelongingToPost_shouldThrowBlogAPIException() {
        // Prepare
        long postId1 = 1L;
        long postId2 = 2L;
        long commentId = 1L;
        Comment comment = new Comment();
        Post post1 = new Post();
        Post post2 = new Post();

        post1.setId(postId1);
        post2.setId(postId2);
        comment.setPost(post1);

        when(postRepository.findById(postId2)).thenReturn(Optional.of(post2));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act and Assert
        assertThrows(BlogAPIException.class,
                () -> commentService.getCommentById(postId2, commentId));
    }

    @Test
    void updateComment_withExistingPostIdAndCommentId_shouldReturnUpdatedCommentDto() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // Act
        CommentDto updatedComment = commentService.updateComment(postId, commentId, commentDto);

        // Assert
        assertNotNull(updatedComment);
        assertEquals(commentDto, updatedComment);
        verify(modelMapper,times(1)).map(comment,CommentDto.class);
        verify(commentRepository,times(1)).findById(commentId);
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).save(comment);

    }

    @Test
    void updateComment_withNonExistingPostId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(postId, commentId, commentDto));
        verify(commentRepository, never()).findById(commentId);
    }

    @Test
    void updateComment_withNonExistingCommentId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Post post = new Post();

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.updateComment(postId, commentId, commentDto));
        verify(commentRepository, never()).save(any());
    }


    @Test
    void updateComment_withCommentNotBelongingToPost_shouldThrowBlogAPIException() {
        long postId = 1L;
        long errorPostId = 2L;
        long commentId = 1L;
        CommentDto commentDto = new CommentDto();
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(postId);
        Post post2 = new Post();
        post2.setId(2L);
        comment.setPost( post2);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        when(commentRepository.save(comment)).thenReturn(comment);
        when(modelMapper.map(comment, CommentDto.class)).thenReturn(commentDto);

        // Act and Assert
        assertThrows(BlogAPIException.class,
                () -> commentService.updateComment(postId, commentId, commentDto));

        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, never()).save(any());
    }


    @Test
    void deleteComment_withExistingPostIdAndCommentId_shouldDeleteComment() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteComment(postId, commentId);

        // Assert
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findById(commentId);
        verify(commentRepository, times(1)).delete(comment);
    }

    @Test
    void deleteComment_withNonExistingPostId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;

        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(postId, commentId));
        verify(commentRepository, never()).findById(commentId);
        verify(commentRepository, never()).delete(any());
    }

    @Test
    void deleteComment_withNonExistingCommentId_shouldThrowResourceNotFoundException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment();
        comment.setId(commentId);
        Post post = new Post();
        post.setId(postId);
        comment.setPost(post);


        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(commentRepository.findById(commentId)).thenReturn(Optional.empty());

        // Act and Assert
        assertThrows(ResourceNotFoundException.class,
                () -> commentService.deleteComment(postId, commentId));
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findById(commentId);
    }

    @Test
    void deleteComment_withCommentNotBelongingToPost_shouldThrowBlogAPIException() {
        // Prepare
        long postId = 1L;
        long commentId = 1L;
        Comment comment = new Comment();
        Post post = new Post();
        post.setId(2L);
        comment.setPost(post);

        when(postRepository.findById(postId)).thenReturn(Optional.of(new Post()));
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));

        // Act and Assert
        assertThrows(BlogAPIException.class,
                () -> commentService.deleteComment(postId, commentId));
        verify(postRepository, times(1)).findById(postId);
        verify(commentRepository, times(1)).findById(commentId);
    }
}
