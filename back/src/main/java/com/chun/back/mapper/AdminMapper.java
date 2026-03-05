package com.chun.back.mapper;

import com.chun.back.pojo.Admin;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface AdminMapper {

    @Select("SELECT COUNT(*) FROM admin")
    long countAdmins();

    @Select("SELECT id, username, password AS password_hash, nickname, email, status, create_time, update_time " +
            "FROM admin WHERE username = #{username} LIMIT 1")
    Admin findByUsername(String username);

    @Select("SELECT id, username, password AS password_hash, nickname, email, status, create_time, update_time " +
            "FROM admin WHERE id = #{id} LIMIT 1")
    Admin selectById(Long id);

    @Insert("INSERT INTO admin(username, password, nickname, email, status, create_time, update_time) " +
            "VALUES(#{username}, #{passwordHash}, #{nickname}, #{email}, #{status}, NOW(), NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Admin admin);

    @Update("UPDATE admin SET update_time = NOW() WHERE id = #{id}")
    int updateLoginInfo(@Param("id") Long id, @Param("ip") String ip);
    
    @Select("SELECT id, status FROM admin WHERE id = #{id} LIMIT 1")
    Admin selectBasicById(Long id);
}