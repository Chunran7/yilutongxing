package com.chun.back.service.impl;

import com.chun.back.mapper.ArticleFavoriteMapper;
import com.chun.back.mapper.ArticleLikeMapper;
import com.chun.back.mapper.ArticleMapper;
import com.chun.back.pojo.Article;
import com.chun.back.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleLikeMapper articleLikeMapper;

    @Autowired
    private ArticleFavoriteMapper articleFavoriteMapper;

    private String safeSortBy(String sortBy) {
        if (sortBy == null) return "create_time";
        return switch (sortBy) {
            case "create_time", "like_count", "views" -> sortBy;
            default -> "create_time";
        };
    }

    private String safeOrder(String order) {
        if (order == null) return "DESC";
        String up = order.toUpperCase();
        return ("ASC".equals(up) || "DESC".equals(up)) ? up : "DESC";
    }

    @Override
    public List<Article> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId) {
        int p = Math.max(page, 1);
        int ps = Math.min(Math.max(pageSize, 1), 50);
        int offset = (p - 1) * ps;

        String sb = safeSortBy(sortBy);
        String od = safeOrder(order);

        List<Article> list = articleMapper.list(ps, offset, sb, od, keyword);

        if (viewerId != null) {
            for (Article a : list) {
                a.setLiked(articleLikeMapper.exists(a.getId(), viewerId) > 0);
                a.setFavorited(articleFavoriteMapper.exists(a.getId(), viewerId) > 0);
            }
        } else {
            for (Article a : list) {
                a.setLiked(false);
                a.setFavorited(false);
            }
        }
        return list;
    }

    @Override
    public List<Article> latest(int count, Long viewerId) {
        int c = Math.min(Math.max(count, 1), 30);
        List<Article> list = articleMapper.latest(c);
        if (viewerId != null) {
            for (Article a : list) {
                a.setLiked(articleLikeMapper.exists(a.getId(), viewerId) > 0);
                a.setFavorited(articleFavoriteMapper.exists(a.getId(), viewerId) > 0);
            }
        } else {
            for (Article a : list) {
                a.setLiked(false);
                a.setFavorited(false);
            }
        }
        return list;
    }

    @Override
    public Article getById(Long id, Long viewerId) {
        articleMapper.incViews(id);
        Article a = articleMapper.selectByIdWithAuthor(id);
        if (a == null) return null;

        if (viewerId != null) {
            a.setLiked(articleLikeMapper.exists(id, viewerId) > 0);
            a.setFavorited(articleFavoriteMapper.exists(id, viewerId) > 0);
        } else {
            a.setLiked(false);
            a.setFavorited(false);
        }
        return a;
    }

    @Override
    public Long create(Article article) {
        articleMapper.insert(article);
        return article.getId();
    }

    @Override
    public List<Article> myLiked(Long userId) {
        List<Article> list = articleMapper.listLikedByUser(userId);
        for (Article a : list) {
            a.setLiked(true);
            a.setFavorited(articleFavoriteMapper.exists(a.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public List<Article> myFavorited(Long userId) {
        List<Article> list = articleMapper.listFavoritedByUser(userId);
        for (Article a : list) {
            a.setFavorited(true);
            a.setLiked(articleLikeMapper.exists(a.getId(), userId) > 0);
        }
        return list;
    }

    @Override
    public Map<String, Object> toggleLike(Long articleId, Long userId) {
        boolean existed = articleLikeMapper.exists(articleId, userId) > 0;
        if (existed) {
            articleLikeMapper.delete(articleId, userId);
        } else {
            articleLikeMapper.insert(articleId, userId);
        }
        int likeCount = articleLikeMapper.countByArticleId(articleId);
        articleMapper.updateLikeCount(articleId, likeCount);

        Map<String, Object> res = new HashMap<>();
        res.put("liked", !existed);
        res.put("likeCount", likeCount);
        return res;
    }

    @Override
    public Map<String, Object> toggleFavorite(Long articleId, Long userId) {
        boolean existed = articleFavoriteMapper.exists(articleId, userId) > 0;
        if (existed) {
            articleFavoriteMapper.delete(articleId, userId);
        } else {
            articleFavoriteMapper.insert(articleId, userId);
        }
        Map<String, Object> res = new HashMap<>();
        res.put("favorited", !existed);
        return res;
    }
}
