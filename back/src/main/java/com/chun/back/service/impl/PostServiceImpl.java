package com.chun.back.service.impl;

import com.chun.back.mapper.PostFavoriteMapper;
import com.chun.back.mapper.PostLikeMapper;
import com.chun.back.mapper.PostMapper;
import com.chun.back.pojo.Post;
import com.chun.back.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PostServiceImpl implements PostService {

    @Autowired
    private PostMapper postMapper;

    @Autowired
    private PostLikeMapper postLikeMapper;

    @Autowired
    private PostFavoriteMapper postFavoriteMapper;

    private String safeSortBy(String sortBy) {
        if (sortBy == null) return "create_time";
        return switch (sortBy) {
            case "create_time", "reply_count", "like_count", "views" -> sortBy;
            default -> "create_time";
        };
    }

    private String safeOrder(String order) {
        if (order == null) return "DESC";
        String up = order.toUpperCase();
        return ("ASC".equals(up) || "DESC".equals(up)) ? up : "DESC";
    }

    @Override
    public List<Post> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId) {
        int p = Math.max(page, 1);
        int ps = Math.min(Math.max(pageSize, 1), 50);
        int offset = (p - 1) * ps;

        String sb = safeSortBy(sortBy);
        String od = safeOrder(order);

        List<Post> list = postMapper.list(ps, offset, sb, od, keyword);

        if (viewerId != null) {
            for (Post post : list) {
                post.setLiked(postLikeMapper.exists(post.getId(), viewerId) > 0);
                post.setFavorited(postFavoriteMapper.exists(post.getId(), viewerId) > 0);
            }
        } else {
            for (Post post : list) {
                post.setLiked(false);
                post.setFavorited(false);
            }
        }
        return list;
    }

    @Override
    public Post getById(Long postId, Long viewerId) {
        postMapper.incViews(postId);
        Post p = postMapper.selectByIdWithAuthor(postId);
        if (p == null) return null;

        if (viewerId != null) {
            p.setLiked(postLikeMapper.exists(postId, viewerId) > 0);
            p.setFavorited(postFavoriteMapper.exists(postId, viewerId) > 0);
        } else {
            p.setLiked(false);
            p.setFavorited(false);
        }
        return p;
    }

    @Override
    public Long create(Long userId, String title, String content) {
        Post p = new Post();
        p.setUserId(userId);
        p.setTitle(title);
        p.setContent(content);
        postMapper.insert(p);
        return p.getId();
    }

    @Override
    public List<Post> myList(Long userId) {
        List<Post> list = postMapper.listByUser(userId);
        // 自己看自己：liked/favorited 也给出正确值，便于前端在管理页展示
        for (Post p : list) {
            p.setLiked(postLikeMapper.exists(p.getId(), userId) > 0);
            p.setFavorited(postFavoriteMapper.exists(p.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public boolean updateMyPost(Long userId, Long postId, String title, String content) {
        return postMapper.updateByIdAndUserId(userId, postId, title, content) > 0;
    }

    @Override
    public boolean deleteMyPost(Long userId, Long postId) {
        return postMapper.softDeleteByIdAndUserId(userId, postId) > 0;
    }

    @Override
    public List<Post> myLiked(Long userId) {
        List<Post> list = postMapper.listLikedByUser(userId);
        for (Post p : list) {
            p.setLiked(true);
            p.setFavorited(postFavoriteMapper.exists(p.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public List<Post> myFavorited(Long userId) {
        List<Post> list = postMapper.listFavoritedByUser(userId);
        for (Post p : list) {
            p.setFavorited(true);
            p.setLiked(postLikeMapper.exists(p.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public Map<String, Object> toggleLike(Long postId, Long userId) {
        boolean existed = postLikeMapper.exists(postId, userId) > 0;
        if (existed) {
            postLikeMapper.delete(postId, userId);
        } else {
            postLikeMapper.insert(postId, userId);
        }
        int likeCount = postLikeMapper.countByPostId(postId);
        postMapper.updateLikeCount(postId, likeCount);

        Map<String, Object> res = new HashMap<>();
        res.put("liked", !existed);
        res.put("likeCount", likeCount);
        return res;
    }

    @Override
    public Map<String, Object> toggleFavorite(Long postId, Long userId) {
        boolean existed = postFavoriteMapper.exists(postId, userId) > 0;
        if (existed) {
            postFavoriteMapper.delete(postId, userId);
        } else {
            postFavoriteMapper.insert(postId, userId);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("favorited", !existed);
        return res;
    }
}
