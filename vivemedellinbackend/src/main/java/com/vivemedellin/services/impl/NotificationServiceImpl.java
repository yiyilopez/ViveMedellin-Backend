package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Notification;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.SavedPost;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.NotificationDto;
import com.vivemedellin.payloads.UserResponseDto;
import com.vivemedellin.repositories.NotificationRepo;
import com.vivemedellin.repositories.SavedPostRepo;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepo notificationRepo;

    @Autowired
    private SavedPostRepo savedPostRepo;

    @Autowired
    private UserRepo userRepo;

    @Override
    public void createNotificationForNewCommentOnSavedPost(Post post, Comment comment, User commentAuthor) {
        List<SavedPost> savedPosts = savedPostRepo.findByPost(post);

        for (SavedPost savedPost : savedPosts) {
            User userToNotify = savedPost.getUser();

            if (userToNotify.getId().equals(commentAuthor.getId())) {
                continue;
            }

            if (userToNotify.getId().equals(post.getUser().getId())) {
                continue;
            }

            Notification notification = new Notification();
            notification.setUser(userToNotify);
            notification.setType(Notification.NotificationType.NEW_COMMENT_ON_SAVED_POST);
            notification.setMessage(commentAuthor.getName() + " comentó en el evento \"" + post.getPostTitle() + "\" que guardaste");
            notification.setPost(post);
            notification.setComment(comment);
            notification.setTriggeredByUser(commentAuthor);

            notificationRepo.save(notification);
        }
    }

    @Override
    public void createNotificationForReplyToComment(Comment parentComment, Comment reply, User replyAuthor) {
        User parentCommentAuthor = parentComment.getUser();

        if (parentCommentAuthor.getId().equals(replyAuthor.getId())) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(parentCommentAuthor);
        notification.setType(Notification.NotificationType.REPLY_TO_COMMENT);
        notification.setMessage(replyAuthor.getName() + " respondió a tu comentario en \"" + parentComment.getPost().getPostTitle() + "\"");
        notification.setPost(parentComment.getPost());
        notification.setComment(reply);
        notification.setTriggeredByUser(replyAuthor);

        notificationRepo.save(notification);
    }

    @Override
    public void createNotificationForCommentOnOwnPost(Post post, Comment comment, User commentAuthor) {
        User postAuthor = post.getUser();

        if (postAuthor.getId().equals(commentAuthor.getId())) {
            return;
        }

        Notification notification = new Notification();
        notification.setUser(postAuthor);
        notification.setType(Notification.NotificationType.NEW_COMMENT_ON_OWN_POST);
        notification.setMessage(commentAuthor.getName() + " comentó en tu evento \"" + post.getPostTitle() + "\"");
        notification.setPost(post);
        notification.setComment(comment);
        notification.setTriggeredByUser(commentAuthor);

        notificationRepo.save(notification);
    }

    @Override
    public List<NotificationDto> getNotificationsForUser(Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        List<Notification> notifications = notificationRepo.findByUserOrderByCreatedDateDesc(user);

        return notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDto> getUnreadNotificationsForUser(Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        List<Notification> notifications = notificationRepo.findByUserAndIsReadOrderByCreatedDateDesc(user, false);

        return notifications.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadNotificationCount(Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        return notificationRepo.countByUserAndIsRead(user, false);
    }

    @Override
    public void markAsRead(Integer notificationId, Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new AccessDeniedException("No tienes permisos para marcar esta notificación");
        }

        notification.setIsRead(true);
        notificationRepo.save(notification);
    }

    @Override
    public void markAllAsRead(Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        List<Notification> unreadNotifications = notificationRepo.findByUserAndIsReadOrderByCreatedDateDesc(user, false);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        notificationRepo.saveAll(unreadNotifications);
    }

    private NotificationDto mapToDto(Notification notification) {
        NotificationDto dto = new NotificationDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType().name());
        dto.setMessage(notification.getMessage());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedDate(notification.getCreatedDate());

        if (notification.getPost() != null) {
            dto.setPostId(notification.getPost().getPostId());
            dto.setPostTitle(notification.getPost().getPostTitle());
        }

        if (notification.getComment() != null) {
            dto.setCommentId(notification.getComment().getId());
        }

        if (notification.getTriggeredByUser() != null) {
            UserResponseDto userDto = new UserResponseDto();
            userDto.setId(notification.getTriggeredByUser().getId());
            userDto.setName(notification.getTriggeredByUser().getName());
            dto.setTriggeredByUser(userDto);
        }

        return dto;
    }
}
