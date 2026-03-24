package com.chun.back.service;

import com.chun.back.pojo.Article;

import java.util.List;
import java.util.Map;

public interface ArticleService {

    List<Article> list(int page, int pageSize, String sortBy, String order, String keyword, Long viewerId);

    List<Article> latest(int count, Long viewerId);

    Article getById(Long id, Long viewerId);

    Long create(Article article);

}
