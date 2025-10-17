package com.vivemedellin.controllers;

import com.vivemedellin.payloads.ApiResponse;
import com.vivemedellin.payloads.CommentDto;
import com.vivemedellin.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentDto> addComment(@RequestBody CommentDto commentDto,
                                                 @PathVariable Integer postId,
                                                 Principal principal) {
        CommentDto createdComment = this.commentService.createComment(commentDto, postId, principal);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    @PostMapping("/comments/{commentId}/replies")
    public ResponseEntity<CommentDto> replyToComment(@RequestBody CommentDto commentDto,
                                                     @PathVariable Integer commentId,
                                                     Principal principal) {
        CommentDto reply = this.commentService.replyToComment(commentDto, commentId, principal);
        return new ResponseEntity<>(reply, HttpStatus.CREATED);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentDto>> getCommentsByPost(@PathVariable Integer postId) {
        List<CommentDto> comments = this.commentService.getCommentsByPost(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }

    @GetMapping("/comments/{commentId}")
    public ResponseEntity<CommentDto> getCommentWithReplies(@PathVariable Integer commentId) {
        CommentDto comment = this.commentService.getCommentWithReplies(commentId);
        return new ResponseEntity<>(comment, HttpStatus.OK);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Integer commentId, Principal principal) {
        this.commentService.deleteComment(commentId, principal);
        return new ResponseEntity<>(new ApiResponse("Comment deleted Successfully", true), HttpStatus.OK);
    }
}
