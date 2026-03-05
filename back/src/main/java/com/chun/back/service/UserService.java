package com.chun.back.service;

import com.chun.back.pojo.User;

import java.util.List;

public interface UserService {

    User findByUserName(String username);

    void register(String username, String password);

    User getMe(Long userId);

    User getProfile(Long targetId, Long viewerId);

    void updateProfile(Long userId, String nickname, String email, String userPic);

    /**
     * 仅更新头像
     */
    void updateAvatar(Long userId, String userPic);

    /**
     * 关注 / 取消关注
     * 
     * @return 更新后的 profile（followed + 计数）
     */
    User toggleFollow(Long followerId, Long followingId);

    /**
     * 我关注的人列表
     */
    List<User> listFollowing(Long userId);

    /** 重置用户密码（直接根据邮箱查找用户并更新密码） */
    void resetPasswordByEmail(String email, String newPassword);
}
