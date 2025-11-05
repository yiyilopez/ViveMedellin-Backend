package com.vivemedellin.payloads;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class NotificationDto {

    private Integer id;
    private String type;
    private String message;
    private Integer postId;
    private String postTitle;
    private Integer commentId;
    private UserResponseDto triggeredByUser;
    private Boolean isRead;
    private Date createdDate;
}
