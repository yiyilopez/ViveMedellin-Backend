package com.vivemedellin.repositories;

import com.vivemedellin.models.Category;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepo extends JpaRepository<Post, Integer> {
    List<Post> findByUser(User user);
    Page<Post> findByCategory(Category category, Pageable pageable);
    Page<Post> findByPostTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String title, String content, Pageable pageable);
}
