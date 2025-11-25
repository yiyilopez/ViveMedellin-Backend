package com.vivemedellin.controllers;

import com.vivemedellin.payloads.ApiResponse;
import com.vivemedellin.payloads.PostDto;
import com.vivemedellin.services.SavedPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/saved-posts")
public class SavedPostController {

    @Autowired
    private SavedPostService savedPostService;

    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse> savePost(@PathVariable Integer postId, Principal principal) {
        savedPostService.savePost(postId, principal);
        return new ResponseEntity<>(new ApiResponse("Post saved successfully", true), HttpStatus.CREATED);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse> unsavePost(@PathVariable Integer postId, Principal principal) {
        savedPostService.unsavePost(postId, principal);
        return new ResponseEntity<>(new ApiResponse("Post unsaved successfully", true), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<PostDto>> getSavedPosts(Principal principal) {
        List<PostDto> savedPosts = savedPostService.getSavedPostsByUser(principal);
        return new ResponseEntity<>(savedPosts, HttpStatus.OK);
    }

    // Fetch saved posts by userId (e.g. for admin dashboard)
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostDto>> getSavedPostsByUserId(@PathVariable Integer userId) {
        List<PostDto> savedPosts = savedPostService.getSavedPostsByUserId(userId);
        return new ResponseEntity<>(savedPosts, HttpStatus.OK);
    }

    // Fetch saved posts by email (alternative lookup)
    @GetMapping("/user/email/{email}")
    public ResponseEntity<List<PostDto>> getSavedPostsByEmail(@PathVariable String email) {
        List<PostDto> savedPosts = savedPostService.getSavedPostsByEmail(email);
        return new ResponseEntity<>(savedPosts, HttpStatus.OK);
    }

    @GetMapping("/{postId}/check")
    public ResponseEntity<Boolean> checkIfPostSaved(@PathVariable Integer postId, Principal principal) {
        boolean isSaved = savedPostService.isPostSavedByUser(postId, principal);
        return new ResponseEntity<>(isSaved, HttpStatus.OK);
    }
}
