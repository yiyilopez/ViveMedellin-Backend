package com.vivemedellin.services.impl;

import com.vivemedellin.exceptions.ResourceNotFoundException;
import com.vivemedellin.models.Post;
import com.vivemedellin.models.SavedPost;
import com.vivemedellin.models.User;
import com.vivemedellin.payloads.PostDto;
import com.vivemedellin.repositories.PostRepo;
import com.vivemedellin.repositories.SavedPostRepo;
import com.vivemedellin.repositories.UserRepo;
import com.vivemedellin.services.SavedPostService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SavedPostServiceImpl implements SavedPostService {

    @Autowired
    private SavedPostRepo savedPostRepo;

    @Autowired
    private PostRepo postRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public void savePost(Integer postId, Principal principal) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        if (savedPostRepo.existsByUserAndPost(user, post)) {
            throw new IllegalStateException("Post already saved");
        }

        SavedPost savedPost = new SavedPost();
        savedPost.setUser(user);
        savedPost.setPost(post);

        savedPostRepo.save(savedPost);
    }

    @Override
    public void unsavePost(Integer postId, Principal principal) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        SavedPost savedPost = savedPostRepo.findByUserAndPost(user, post)
                .orElseThrow(() -> new ResourceNotFoundException("SavedPost", "user and post", ""));

        savedPostRepo.delete(savedPost);
    }

    @Override
    public List<PostDto> getSavedPostsByUser(Principal principal) {
        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        return mapSavedPosts(savedPostRepo.findByUser(user));
    }

    @Override
    public boolean isPostSavedByUser(Integer postId, Principal principal) {
        Post post = postRepo.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post", "postId", postId));

        String username = principal.getName();
        User user = userRepo.findByEmail(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", username));

        return savedPostRepo.existsByUserAndPost(user, post);
    }

        @Override
        public List<PostDto> getSavedPostsByUserId(Integer userId) {
                User user = userRepo.findById(userId)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "userId", userId));
                return mapSavedPosts(savedPostRepo.findByUser(user));
        }

        @Override
        public List<PostDto> getSavedPostsByEmail(String email) {
                User user = userRepo.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
                return mapSavedPosts(savedPostRepo.findByUser(user));
        }

        private List<PostDto> mapSavedPosts(List<SavedPost> savedPosts) {
                return savedPosts.stream()
                                .map(savedPost -> modelMapper.map(savedPost.getPost(), PostDto.class))
                                .collect(Collectors.toList());
        }
}
