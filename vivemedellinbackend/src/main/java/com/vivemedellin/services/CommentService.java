package com.vivemedellin.services;

import com.vivemedellin.payloads.CommentDto;

import java.security.Principal;
import java.util.List;

public interface CommentService {
    CommentDto createComment(CommentDto commentDto, Integer postId, Principal principal);

    CommentDto replyToComment(CommentDto commentDto, Integer commentId, Principal principal);

    List<CommentDto> getCommentsByPost(Integer postId);

    CommentDto getCommentWithReplies(Integer commentId);

    CommentDto updateComment(CommentDto commentDto, Integer commentId, Principal principal);

    void deleteComment(Integer commentId, Principal principal);
}
