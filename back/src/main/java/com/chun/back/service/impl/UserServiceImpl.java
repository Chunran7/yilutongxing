package com.chun.back.service.impl;

import com.chun.back.mapper.UserMapper;
import com.chun.back.pojo.User;
import com.chun.back.service.UserService;
import com.chun.back.utils.Md5Util;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User findByUserName(String username) {
        return userMapper.findByUserName(username);
    }

    @Override
    public void register(String username, String password) {
        String md5 = Md5Util.getMD5String(password);
        User u = new User();
        u.setUsername(username);
        u.setPasswordHash(md5);
        try {
            int rows = userMapper.insert(u);
            if (rows != 1) {
                throw new RuntimeException("注册失败");
            }
        } catch (DuplicateKeyException e) {
            // 并发下的兜底：即使 Controller 做了查重，也可能被并发插入打穿
            throw new RuntimeException("用户名已存在");
        }
    }

    @Override
    public User getMe(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public User getProfile(Long targetId, Long viewerId) {
        return userMapper.selectById(targetId);
    }

    @Override
    public void updateProfile(Long userId, String nickname, String email, String userPic) {
        // 允许邮箱/昵称为空：空字符串会导致
        // 1) 列表 author 的 COALESCE 被空昵称截断（显示成“匿名用户”）
        // 2) user.email 的唯一索引在多用户都填空字符串时冲突
        if (nickname != null && nickname.trim().isEmpty())
            nickname = null;
        if (email != null && email.trim().isEmpty())
            email = null;
        if (userPic != null && userPic.trim().isEmpty())
            userPic = null;
        User u = new User();
        u.setId(userId);
        u.setNickname(nickname);
        u.setEmail(email);
        u.setUserPic(userPic);
        userMapper.updateProfile(u);
    }

    @Override
    public void updateAvatar(Long userId, String userPic) {
        userMapper.updateAvatar(userId, userPic);
    }

    @Override
    public void resetPasswordByEmail(String email, String newPassword) {
        if (email == null || email.isBlank())
            throw new RuntimeException("邮箱不能为空");
        if (newPassword == null || newPassword.isBlank())
            throw new RuntimeException("密码不能为空");
        User u = userMapper.findByEmail(email);
        if (u == null)
            throw new RuntimeException("该邮箱未绑定任何用户");
        String md5 = Md5Util.getMD5String(newPassword);
        int rows = userMapper.updatePasswordById(u.getId(), md5);
        if (rows != 1)
            throw new RuntimeException("更新密码失败");
    }
}
