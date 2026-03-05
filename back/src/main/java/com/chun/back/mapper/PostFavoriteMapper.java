package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PostFavoriteMapper {

    @Select("SELECT COUNT(*) FROM post_favorite WHERE post_id=#{postId} AND user_id=#{userId}")
    int exists(Long postId, Long userId);

    @Insert("INSERT IGNORE INTO post_favorite(post_id, user_id, create_time) VALUES(#{postId}, #{userId}, NOW())")
    int insert(Long postId, Long userId);

    @Delete("DELETE FROM post_favorite WHERE post_id=#{postId} AND user_id=#{userId}")
    int delete(Long postId, Long userId);
}
