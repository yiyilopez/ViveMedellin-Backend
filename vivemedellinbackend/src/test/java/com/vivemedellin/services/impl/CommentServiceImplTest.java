package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.repositories.CommentRepo;
import com.vivemedellin.repositories.PostRepo;
import com.vivemedellin.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.modelmapper.ModelMapper;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private PostRepo postRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private CommentRepo commentRepo;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private Principal principal;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateComment() {
        CommentDto dto = new CommentDto();
        dto.setContent("Comentario de prueba");

        Post post = new Post();
        User user = new User();
        user.setEmail("test@example.com");
        Comment comment = new Comment();
        Comment savedComment = new Comment();

        when(postRepo.findById(1)).thenReturn(Optional.of(post));
        when(principal.getName()).thenReturn("test@example.com");
        when(userRepo.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(modelMapper.map(dto, Comment.class)).thenReturn(comment);
        when(commentRepo.save(comment)).thenReturn(savedComment);
        when(modelMapper.map(savedComment, CommentDto.class)).thenReturn(dto);

        CommentDto result = commentService.createComment(dto, 1, principal);

        assertEquals("Comentario de prueba", result.getContent());
        verify(commentRepo, times(1)).save(comment);
    }

    @Test
    void testGetCommentsByPost() {
        Post post = new Post();
        Comment comment1 = new Comment();
        Comment comment2 = new Comment();
        post.setComments(Arrays.asList(comment1, comment2));

        CommentDto dto1 = new CommentDto();
        CommentDto dto2 = new CommentDto();

        when(postRepo.findById(1)).thenReturn(Optional.of(post));
        when(modelMapper.map(comment1, CommentDto.class)).thenReturn(dto1);
        when(modelMapper.map(comment2, CommentDto.class)).thenReturn(dto2);

        List<CommentDto> list = commentService.getCommentsByPost(1);

        assertEquals(2, list.size());
        verify(postRepo).findById(1);
    }

    @Test
    void testDeleteComment() {
        Comment comment = new Comment();
        when(commentRepo.findById(1)).thenReturn(Optional.of(comment));

        commentService.deleteComment(1);

        verify(commentRepo).delete(comment);
    }

    @Test
    void testUpdateComment_Success() {
        Comment comment = new Comment();
        User user = new User();
        user.setEmail("test@example.com");
        comment.setUser(user);

        CommentDto dto = new CommentDto();
        dto.setContent("Nuevo contenido");
        Comment updatedComment = new Comment();

        when(commentRepo.findById(1)).thenReturn(Optional.of(comment));
        when(principal.getName()).thenReturn("test@example.com");
        when(commentRepo.save(comment)).thenReturn(updatedComment);
        when(modelMapper.map(updatedComment, CommentDto.class)).thenReturn(dto);

        CommentDto result = commentService.updateComment(dto, 1, principal);

        assertEquals("Nuevo contenido", result.getContent());
        verify(commentRepo).save(comment);
    }

    @Test
    void testUpdateComment_AccessDenied() {
        Comment comment = new Comment();
        User user = new User();
        user.setEmail("otro@example.com");
        comment.setUser(user);

        CommentDto dto = new CommentDto();

        when(commentRepo.findById(1)).thenReturn(Optional.of(comment));
        when(principal.getName()).thenReturn("test@example.com");

        assertThrows(org.springframework.security.access.AccessDeniedException.class,
                () -> commentService.updateComment(dto, 1, principal));
    }

    @Test
    void testCreateComment_PostNotFound() {
        CommentDto dto = new CommentDto();
        when(postRepo.findById(1)).thenReturn(Optional.empty());
        when(principal.getName()).thenReturn("test@example.com");

        assertThrows(ResourceNotFoundException.class,
                () -> commentService.createComment(dto, 1, principal));
    }
}
