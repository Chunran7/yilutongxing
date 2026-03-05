package com.chun.back.pojo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Video {
    private Long id;
    private Long userId;
    private String title;
    private String url;
    private String cover;
    private String description;

    private Integer views;
    private Integer likeCount;
    private Integer isDeleted;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 扩展字段
    private String author;
    private String authorPic;

    private Boolean liked;
    private Boolean favorited;
}
