package com.chun.back.service.impl;

import com.chun.back.mapper.ArticleMapper;
import com.chun.back.pojo.Article;
import com.chun.back.service.ArticleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {

    @Autowired
    private ArticleMapper articleMapper;

    private String safeSortBy(String sortBy) {
        if (sortBy == null) return "create_time";
        return switch (sortBy) {
            case "create_time", "views" -> sortBy;
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

        return articleMapper.list(ps, offset, sb, od, keyword);
    }

    @Override
    public List<Article> latest(int count, Long viewerId) {
        int c = Math.min(Math.max(count, 1), 30);
        return articleMapper.latest(c);
    }

    @Override
    public Article getById(Long id, Long viewerId) {
        articleMapper.incViews(id);
        return articleMapper.selectByIdWithAuthor(id);
    }

    @Override
    public Long create(Article article) {
        articleMapper.insert(article);
        return article.getId();
    }
}
