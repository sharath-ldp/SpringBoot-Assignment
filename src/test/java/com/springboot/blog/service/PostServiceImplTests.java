package com.springboot.blog.service;

import com.springboot.blog.entity.Category;
import com.springboot.blog.entity.Post;
import com.springboot.blog.payload.PostDto;
import com.springboot.blog.payload.PostResponse;
import com.springboot.blog.repository.CategoryRepository;
import com.springboot.blog.repository.PostRepository;
import com.springboot.blog.service.impl.PostServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PostServiceImplTests {

    @Mock
    private PostRepository postRepository;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks //@InjectMock creates an instance of the class and injects the mocks that are marked with the annotations @Mock into it
    private PostServiceImpl postService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreatePost() {
        // Mock the necessary data
        PostDto postDto = new PostDto();
        postDto.setTitle("Test Title");
        postDto.setDescription("Test Description");
        postDto.setContent("Test Content");
        postDto.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);

        Post post = new Post();
        post.setId(1L);
        post.setTitle("Test Title");
        post.setDescription("Test Description");
        post.setContent("Test Content");
        post.setCategory(category);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(modelMapper.map(postDto, Post.class)).thenReturn(post);
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Create the post
        PostDto createdPost = postService.createPost(postDto);

        // Verify the interactions and assertions
        verify(categoryRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        verify(modelMapper, times(1)).map(post, PostDto.class);
        assertEquals(postDto, createdPost);
    }

    @Test
    public void testGetAllPosts() {
        // Mock the necessary data
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "title";
        String sortDirAsc = "asc";
        String sortDirDesc = "desc";

        Sort sortAsc = Sort.by(Sort.Direction.ASC, sortBy);

        PageRequest pageRequest = PageRequest.of(pageNo, pageSize, sortAsc);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());
        Page<Post> postPage = new PageImpl<>(posts, pageRequest, posts.size());

        when(postRepository.findAll(pageRequest)).thenReturn(postPage);

        // Get all posts
        PostResponse postResponse = postService.getAllPosts(pageNo, pageSize, sortBy, sortDirAsc);

        // Verify the interactions and assertions
        verify(postRepository, times(1)).findAll(pageRequest);
        assertEquals(posts.size(), postResponse.getContent().size());
        assertEquals(pageNo, postResponse.getPageNo());
        assertEquals(pageSize, postResponse.getPageSize());
        assertEquals(posts.size(), postResponse.getTotalElements());
        assertEquals(1, postResponse.getTotalPages());
        assertTrue(postResponse.isLast());

    }

    @Test
    void getAllPosts_withDescendingSort_shouldReturnPostResponse() {
        // Prepare
        int pageNo = 0;
        int pageSize = 10;
        String sortBy = "createdAt";
        String sortDir = "desc";
        Sort sort = Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNo, pageSize, sort);

        // Create a list of posts
        List<Post> posts = new ArrayList<>();
        // Add some posts to the list

        // Create a Page object with the list of posts
        Page<Post> postPage = new PageImpl<>(posts, pageable, posts.size());

        // Mock the post repository's findAll method to return the Page object
        when(postRepository.findAll(pageable)).thenReturn(postPage);

        // Act
        PostResponse result = postService.getAllPosts(pageNo, pageSize, sortBy, sortDir);

        // Assert
        assertNotNull(result);
        assertEquals(pageNo, result.getPageNo());
        assertEquals(pageSize, result.getPageSize());
        assertEquals(posts.size(), result.getContent().size());
        // Add more assertions as needed
    }


//    @Test
//    public void testGetAllPotsDesc(){
//        // Mock the necessary data
//        int pageNo = 0;
//        int pageSize = 10;
//        String sortBy = "title";
//        String sortDirAsc = "asc";
//        String sortDirDesc = "desc";
//        Sort sortDesc = Sort.by(Sort.Direction.DESC, sortBy);
//        PageRequest pageRequest2 = PageRequest.of(pageNo, pageSize, sortDesc);
//
//        List<Post> posts2 = new ArrayList<>();
//        posts2.add(new Post());
//        posts2.add(new Post());
//        Page<Post> postPage2 = new PageImpl<>(posts2, pageRequest2, posts2.size());
//
//        when(postRepository.findAll(pageRequest2)).thenReturn(postPage2);
//
//        // Get all posts
//        PostResponse postResponse2 = postService.getAllPosts(pageNo, pageSize, sortBy, sortDirAsc);
//
//        // Verify the interactions and assertions
//        verify(postRepository, times(1)).findAll(pageRequest2);
//        assertEquals(posts2.size(), postResponse2.getContent().size());
//        assertEquals(pageNo, postResponse2.getPageNo());
//        assertEquals(pageSize, postResponse2.getPageSize());
//        assertEquals(posts2.size(), postResponse2.getTotalElements());
//        assertEquals(1, postResponse2.getTotalPages());
//        assertTrue(postResponse2.isLast());
//    }

    @Test
    public void testGetPostById() {
        // Mock the necessary data
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(modelMapper.map(post, PostDto.class)).thenReturn(new PostDto());

        // Get the post by ID
        PostDto retrievedPost = postService.getPostById(postId);

        // Verify the interactions and assertions
        verify(postRepository, times(1)).findById(postId);
        verify(modelMapper, times(1)).map(post, PostDto.class);
        assertNotNull(retrievedPost);
    }

    @Test
    public void testUpdatePost() {
        // Mock the necessary data
        long postId = 1L;
        PostDto postDto = new PostDto();
        postDto.setTitle("Updated Title");
        postDto.setDescription("Updated Description");
        postDto.setContent("Updated Content");
        postDto.setCategoryId(1L);

        Category category = new Category();
        category.setId(1L);

        Post post = new Post();
        post.setId(postId);
        post.setTitle("Test Title");
        post.setDescription("Test Description");
        post.setContent("Test Content");
        post.setCategory(category);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(postRepository.save(post)).thenReturn(post);
        when(modelMapper.map(post, PostDto.class)).thenReturn(postDto);

        // Update the post
        PostDto updatedPost = postService.updatePost(postDto, postId);

        // Verify the interactions and assertions
        verify(postRepository, times(1)).findById(postId);
        verify(categoryRepository, times(1)).findById(1L);
        verify(postRepository, times(1)).save(post);
        verify(modelMapper, times(1)).map(post, PostDto.class);
        assertEquals(postDto, updatedPost);
    }

    @Test
    public void testDeletePostById() {
        // Mock the necessary data
        long postId = 1L;
        Post post = new Post();
        post.setId(postId);

        when(postRepository.findById(postId)).thenReturn(Optional.of(post));

        // Delete the post
        postService.deletePostById(postId);

        // Verify the interactions
        verify(postRepository, times(1)).findById(postId);
        verify(postRepository, times(1)).delete(post);
    }

    @Test
    public void testGetPostsByCategory() {
        // Mock the necessary data
        long categoryId = 1L;
        Category category = new Category();
        category.setId(categoryId);

        List<Post> posts = new ArrayList<>();
        posts.add(new Post());
        posts.add(new Post());

        when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
        when(postRepository.findByCategoryId(categoryId)).thenReturn(posts);
        when(modelMapper.map(any(Post.class), eq(PostDto.class))).thenReturn(new PostDto());

        // Get posts by category
        List<PostDto> retrievedPosts = postService.getPostsByCategory(categoryId);

        // Verify the interactions and assertions
        verify(categoryRepository, times(1)).findById(categoryId);
        verify(postRepository, times(1)).findByCategoryId(categoryId);
        verify(modelMapper, times(posts.size())).map(any(Post.class), eq(PostDto.class));
        assertEquals(posts.size(), retrievedPosts.size());
    }
}
