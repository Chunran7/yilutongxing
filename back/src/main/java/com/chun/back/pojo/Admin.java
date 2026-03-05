package com.chun.back.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Admin {
    private Long id;
    private String username;

    @JsonIgnore
    private String passwordHash; // 对应表里 password

    private String nickname;
    private String email;
    private Integer status;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}