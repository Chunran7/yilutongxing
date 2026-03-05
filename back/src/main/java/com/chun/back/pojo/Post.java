package com.chun.back.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Post {
    private Long id;
    private Long userId;
    private String title;
    private String content;

    private Integer views;
    private Integer likeCount;
    private Integer replyCount;
    private Integer isDeleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 详情/列表展示扩展字段
    private String author;
    private String authorPic;

    private Boolean liked;
    private Boolean favorited;
}
