package com.chun.back.mapper;

import com.chun.back.pojo.Comment;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CommentMapper {

    @Select("""
        SELECT c.id, c.post_id, c.user_id, c.parent_id, c.root_id, c.reply_user_id,
               c.content, c.is_deleted, c.create_time,
               CASE
                 WHEN u.id IS NULL THEN '用户不存在'
                 WHEN u.status = 0 THEN '账号已封禁'
                 ELSE COALESCE(NULLIF(u.nickname,''), u.username)
               END AS author,
               CASE
                 WHEN u.status = 0 THEN NULL
                 ELSE u.user_pic
               END AS author_pic,
               CASE
                 WHEN ru.id IS NULL THEN NULL
                 WHEN ru.status = 0 THEN '账号已封禁'
                 ELSE COALESCE(NULLIF(ru.nickname,''), ru.username)
               END AS reply_user_name
               
        FROM comment c
        LEFT JOIN `user` u ON c.user_id = u.id
        LEFT JOIN `user` ru ON c.reply_user_id = ru.id
        WHERE c.post_id = #{postId}
        ORDER BY c.create_time ASC, c.id ASC
        """)
    List<Comment> selectByPostId(Long postId);

    @Select("SELECT id, post_id, user_id, parent_id, root_id, reply_user_id, content, is_deleted, create_time FROM comment WHERE id=#{id}")
    Comment selectById(Long id);

    @Insert("INSERT INTO comment(post_id, user_id, parent_id, root_id, reply_user_id, content, is_deleted, create_time) " +
            "VALUES(#{postId}, #{userId}, #{parentId}, #{rootId}, #{replyUserId}, #{content}, 0, NOW())")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Comment comment);

    @Update("UPDATE comment SET root_id=#{rootId} WHERE id=#{id}")
    int updateRootId(Long id, Long rootId);

    @Select("SELECT COUNT(*) FROM comment WHERE id=#{commentId} AND user_id=#{userId} AND is_deleted=0")
    int isOwnerAndNotDeleted(Long commentId, Long userId);

    @Update("UPDATE comment SET is_deleted=1 WHERE id=#{commentId} AND user_id=#{userId} AND is_deleted=0")
    int softDelete(Long commentId, Long userId);
}
