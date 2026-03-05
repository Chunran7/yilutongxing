package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface VideoFavoriteMapper {

    @Select("SELECT COUNT(*) FROM video_favorite WHERE video_id=#{videoId} AND user_id=#{userId}")
    int exists(Long videoId, Long userId);

    @Insert("INSERT IGNORE INTO video_favorite(video_id, user_id, create_time) VALUES(#{videoId}, #{userId}, NOW())")
    int insert(Long videoId, Long userId);

    @Delete("DELETE FROM video_favorite WHERE video_id=#{videoId} AND user_id=#{userId}")
    int delete(Long videoId, Long userId);
}
