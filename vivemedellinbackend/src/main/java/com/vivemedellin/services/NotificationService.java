package com.vivemedellin.services;

import com.vivemedellin.models.Comment;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.NotificationDto;

import java.security.Principal;
import java.util.List;

public interface NotificationService {
    
    void createNotificationForNewCommentOnSavedPost(Post post, Comment comment, User commentAuthor);
    
    void createNotificationForReplyToComment(Comment parentComment, Comment reply, User replyAuthor);
    
    void createNotificationForCommentOnOwnPost(Post post, Comment comment, User commentAuthor);
    
    List<NotificationDto> getNotificationsForUser(Principal principal);
    
    List<NotificationDto> getUnreadNotificationsForUser(Principal principal);
    
    Long getUnreadNotificationCount(Principal principal);
    
    void markAsRead(Integer notificationId, Principal principal);
    
    void markAllAsRead(Principal principal);
}
