package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface ArticleFavoriteMapper {

    @Select("SELECT COUNT(*) FROM article_favorite WHERE article_id=#{articleId} AND user_id=#{userId}")
    int exists(Long articleId, Long userId);

    @Insert("INSERT IGNORE INTO article_favorite(article_id, user_id, create_time) VALUES(#{articleId}, #{userId}, NOW())")
    int insert(Long articleId, Long userId);

    @Delete("DELETE FROM article_favorite WHERE article_id=#{articleId} AND user_id=#{userId}")
    int delete(Long articleId, Long userId);
}
