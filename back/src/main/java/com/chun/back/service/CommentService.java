package com.chun.back.service;

import com.chun.back.pojo.Comment;

import java.util.List;

public interface CommentService {

    List<Comment> listTree(Long postId);

    Long create(Long postId, Long userId, String content, Long parentId, Long replyUserId);

    boolean delete(Long commentId, Long userId);
}
