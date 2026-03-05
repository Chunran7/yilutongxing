package com.chun.back.pojo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class Comment {
    private Long id;
    private Long postId;
    private Long userId;
    private Long parentId;
    private Long rootId;
    private Long replyUserId;

    private String content;
    private Integer isDeleted;
    private LocalDateTime createTime;

    // 扩展字段（查询 join `user`）
    private String author;
    private String authorPic;
    private String replyUserName;

    // 前端树状渲染
    private List<Comment> replies;
}
