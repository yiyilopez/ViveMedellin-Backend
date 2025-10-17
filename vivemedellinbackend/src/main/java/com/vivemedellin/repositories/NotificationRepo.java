package com.vivemedellin.repositories;

import com.vivemedellin.models.Notification;
import com.vivemedellin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepo extends JpaRepository<Notification, Integer> {
    
    List<Notification> findByUserOrderByCreatedDateDesc(User user);
    
    List<Notification> findByUserAndIsReadOrderByCreatedDateDesc(User user, Boolean isRead);
    
    Long countByUserAndIsRead(User user, Boolean isRead);
}
