package com.chun.back.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class User {
    private Long id;
    private String username;

    @JsonIgnore
    private String passwordHash;

    private String nickname;
    private String email;
    private String userPic;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 公开主页/个人中心扩展字段
    private Integer followerCount;
    private Integer followingCount;
    private Boolean followed; // viewer 是否关注了该用户
}
