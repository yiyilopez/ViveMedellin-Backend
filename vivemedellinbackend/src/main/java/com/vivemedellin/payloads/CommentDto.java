package com.vivemedellin.payloads;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class CommentDto {

    private int id;

    private String content;

    private Date createdDate;

    private UserResponseDto user;

    private Integer parentCommentId;

    private List<CommentDto> replies = new ArrayList<>();

}
