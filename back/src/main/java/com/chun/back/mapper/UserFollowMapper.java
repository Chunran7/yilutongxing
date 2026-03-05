package com.chun.back.mapper;

import org.apache.ibatis.annotations.*;

@Mapper
public interface UserFollowMapper {

    @Select("SELECT COUNT(*) FROM user_follow WHERE follower_id=#{followerId} AND followee_id=#{followingId}")
    int exists(Long followerId, Long followingId);

    @Insert("INSERT IGNORE INTO user_follow(follower_id, followee_id, create_time) VALUES(#{followerId}, #{followingId}, NOW())")
    int insert(Long followerId, Long followingId);

    @Delete("DELETE FROM user_follow WHERE follower_id=#{followerId} AND followee_id=#{followingId}")
    int delete(Long followerId, Long followingId);
}
