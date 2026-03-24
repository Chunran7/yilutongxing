package com.chun.back.service;

import com.chun.back.pojo.User;

import java.util.List;

public interface UserService {

    User findByUserName(String username);

    void register(String username, String password);

    User getMe(Long userId);

    User getProfile(Long targetId, Long viewerId);

    void updateProfile(Long userId, String nickname, String email, String userPic);

}
