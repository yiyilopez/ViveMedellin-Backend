package com.vivemedellin.services;

import com.vivemedellin.payloads.PostDto;
import com.vivemedellin.payloads.PostResponse;
import org.springframework.data.domain.Page;

import java.security.Principal;
import java.util.List;

public interface PostService {

    PostDto createPost(PostDto postDto, Integer userId, Integer categoryId);
    PostDto updatePost(PostDto postDto, Integer postId);
    void deletePost(Integer postId, Principal principal);
    PostResponse getAllPost(Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    PostDto getPostById(Integer postId);
    PostResponse getPostsByCategory(Integer categoryId, Integer pageNumber, Integer pageSize, String sortBy, String sortDir);
    List<PostDto> getPostsByUser(Integer userId);
    Page<PostDto> searchPost(String keyword, int page, int size);

}
