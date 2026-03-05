package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ArticleLikeMapper {

    @Select("SELECT COUNT(*) FROM article_like WHERE article_id=#{articleId} AND user_id=#{userId}")
    int exists(Long articleId, Long userId);

    @Insert("INSERT IGNORE INTO article_like(article_id, user_id, create_time) VALUES(#{articleId}, #{userId}, NOW())")
    int insert(Long articleId, Long userId);

    @Delete("DELETE FROM article_like WHERE article_id=#{articleId} AND user_id=#{userId}")
    int delete(Long articleId, Long userId);

    @Select("SELECT COUNT(*) FROM article_like WHERE article_id=#{articleId}")
    int countByArticleId(Long articleId);
}
