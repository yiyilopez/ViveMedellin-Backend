package com.vivemedellin.repositories;

import com.vivemedellin.models.Post;
import com.vivemedellin.models.SavedPost;
import com.vivemedellin.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SavedPostRepo extends JpaRepository<SavedPost, Integer> {
    
    List<SavedPost> findByUser(User user);
    
    List<SavedPost> findByPost(Post post);
    
    Optional<SavedPost> findByUserAndPost(User user, Post post);
    
    boolean existsByUserAndPost(User user, Post post);
}
