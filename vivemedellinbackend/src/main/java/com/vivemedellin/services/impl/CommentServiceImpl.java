package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.Role;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.payloads.UserResponseDto;
import com.vivemedellin.repositories.CommentRepo;
import com.vivemedellin.repositories.PostRepo;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.services.CommentService;
import com.vivemedellin.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private CommentRepo commentRepo;

    @Autowired
    private NotificationService notificationService;

    @Override
    public CommentDto createComment(CommentDto commentDto, Integer postId, Principal principal) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        String username = principal.getName();
        User user = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        Comment comment = new Comment();
        comment.setContent(commentDto.getContent());
        comment.setUser(user);
        comment.setPost(post);
        comment.setCreatedDate(new Date());
        
        Comment savedComment = this.commentRepo.save(comment);

        notificationService.createNotificationForCommentOnOwnPost(post, savedComment, user);
        notificationService.createNotificationForNewCommentOnSavedPost(post, savedComment, user);

        return mapToDto(savedComment, false);
    }

    @Override
    public CommentDto replyToComment(CommentDto commentDto, Integer parentCommentId, Principal principal) {
        Comment parentComment = this.commentRepo.findById(parentCommentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", parentCommentId));

        String username = principal.getName();
        User user = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        Comment reply = new Comment();
        reply.setContent(commentDto.getContent());
        reply.setUser(user);
        reply.setPost(parentComment.getPost());
        reply.setParentComment(parentComment);
        reply.setCreatedDate(new Date());

        Comment savedReply = this.commentRepo.save(reply);

        notificationService.createNotificationForReplyToComment(parentComment, savedReply, user);
        notificationService.createNotificationForNewCommentOnSavedPost(parentComment.getPost(), savedReply, user);

        return mapToDto(savedReply, false);
    }

    @Override
    public List<CommentDto> getCommentsByPost(Integer postId) {
        Post post = this.postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        List<Comment> mainComments = post.getComments().stream()
                .filter(comment -> comment.getParentComment() == null)
                .collect(Collectors.toList());

        return mainComments.stream()
                .map(comment -> mapToDto(comment, true))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentWithReplies(Integer commentId) {
        Comment comment = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", commentId));

        return mapToDto(comment, true);
    }

    @Override
    public CommentDto updateComment(CommentDto commentDto, Integer commentId, Principal principal) {
        Comment comment = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", commentId));

        String username = principal.getName();
        User currentUser = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        if (!comment.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("No tienes permisos para editar este comentario");
        }

        comment.setContent(commentDto.getContent());
        comment.setEditedDate(new Date());
        Comment updatedComment = this.commentRepo.save(comment);

        return mapToDto(updatedComment, false);
    }

    @Override
    public void deleteComment(Integer commentId, Principal principal) {
        Comment comment = this.commentRepo.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment", "commentId", commentId));

        String username = principal.getName();
        User currentUser = this.userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        boolean isOwner = comment.getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRoles().contains(Role.ROLE_ADMIN);

        if (!isOwner && !isAdmin) {
            throw new AccessDeniedException("No tienes permisos para eliminar este comentario");
        }

        this.commentRepo.delete(comment);
    }

    private CommentDto mapToDto(Comment comment, boolean includeReplies) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setCreatedDate(comment.getCreatedDate());
        dto.setEditedDate(comment.getEditedDate());

        UserResponseDto userDto = new UserResponseDto();
        userDto.setId(comment.getUser().getId());
        userDto.setName(comment.getUser().getName());
        dto.setUser(userDto);

        if (comment.getParentComment() != null) {
            dto.setParentCommentId(comment.getParentComment().getId());
        }

        if (includeReplies && comment.getReplies() != null && !comment.getReplies().isEmpty()) {
            List<CommentDto> replyDtos = comment.getReplies().stream()
                    .map(reply -> mapToDto(reply, true))
                    .collect(Collectors.toList());
            dto.setReplies(replyDtos);
        }

        return dto;
    }
}
