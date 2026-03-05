package com.chun.back.service;

import com.chun.back.pojo.Post;

import java.util.List;
import java.util.Map;

public interface PostService {

    List<Post> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId);

    Post getById(Long postId, Long viewerId);

    Long create(Long userId, String title, String content);

    /** 我发布的帖子 */
    List<Post> myList(Long userId);

    /** 编辑自己的帖子 */
    boolean updateMyPost(Long userId, Long postId, String title, String content);

    /** 删除自己的帖子（逻辑删除） */
    boolean deleteMyPost(Long userId, Long postId);

    /** 我点赞的帖子 */
    List<Post> myLiked(Long userId);

    /** 我收藏的帖子 */
    List<Post> myFavorited(Long userId);

    Map<String, Object> toggleLike(Long postId, Long userId);

    Map<String, Object> toggleFavorite(Long postId, Long userId);
}
