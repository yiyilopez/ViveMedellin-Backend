package com.vivemedellin.services;

import com.vivemedellin.payloads.PostDto;

import java.security.Principal;
import java.util.List;

public interface SavedPostService {
    
    void savePost(Integer postId, Principal principal);
    
    void unsavePost(Integer postId, Principal principal);
    
    List<PostDto> getSavedPostsByUser(Principal principal);

    // New convenience methods for admin or external queries
    List<PostDto> getSavedPostsByUserId(Integer userId);

    List<PostDto> getSavedPostsByEmail(String email);
    
    boolean isPostSavedByUser(Integer postId, Principal principal);
}
