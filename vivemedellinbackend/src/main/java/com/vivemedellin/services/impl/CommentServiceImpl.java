package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.repositories.CommentRepo;
import com.vivemedellin.repositories.PostRepo;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.services.CommentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer postId, Principal principal) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        String username = principal.getName();
        User user = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        Comment comment = this.modelMapper.map(commentDto, Comment.class);
        comment.setUser(user);
        comment.setPost(post);
        Comment savedComment = this.commentRepo.save(comment);

        return this.modelMapper.map(savedComment, CommentDto.class);
    }

    @Override
    public List<CommentDto> getCommentsByPost(Integer postId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        List<Comment> comments = post.getComments();

        List<CommentDto> commentDtos = comments.stream()
                .map(comment -> this.modelMapper.map(comment, CommentDto.class))
                .toList();
        return commentDtos;
    }


    @Override
    public void deleteComment(Integer commentId) {
        Comment comment = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "CommentId", commentId));
        this.commentRepo.delete(comment);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, Integer commentId, Principal principal) {
        Comment comment = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "CommentId", commentId));

        String username = principal.getName();

        // Validar que el usuario es el autor del comentario
        if (!comment.getUser().getEmail().equals(username)) {
            throw new org.springframework.security.access.AccessDeniedException("No tienes permiso para editar este comentario");
        }

        comment.setContent(commentDto.getContent());
        Comment updatedComment = this.commentRepo.save(comment);

        return this.modelMapper.map(updatedComment, CommentDto.class);
    }
}
