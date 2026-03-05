package com.chun.back.pojo.dto;

import lombok.Data;

@Data
public class AdminRegisterDTO {
    private String username;
    private String password;
    private String nickname;
    private String email;
}
