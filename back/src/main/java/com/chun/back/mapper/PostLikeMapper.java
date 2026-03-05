package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface PostLikeMapper {

    @Select("SELECT COUNT(*) FROM post_like WHERE post_id=#{postId} AND user_id=#{userId}")
    int exists(Long postId, Long userId);

    @Insert("INSERT IGNORE INTO post_like(post_id, user_id, create_time) VALUES(#{postId}, #{userId}, NOW())")
    int insert(Long postId, Long userId);

    @Delete("DELETE FROM post_like WHERE post_id=#{postId} AND user_id=#{userId}")
    int delete(Long postId, Long userId);

    @Select("SELECT COUNT(*) FROM post_like WHERE post_id=#{postId}")
    int countByPostId(Long postId);
}
