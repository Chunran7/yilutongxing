package com.chun.back.mapper;

import com.chun.back.pojo.Post;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface PostMapper {

  @Select("""
      SELECT p.id, p.user_id, p.title, p.content, p.views, p.like_count, p.reply_count, p.is_deleted, p.create_time, p.update_time,
             CASE
               WHEN u.id IS NULL THEN '用户不存在'
               WHEN u.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(u.nickname,''), u.username)
             END AS author,
             CASE
               WHEN u.status = 0 THEN NULL
               ELSE u.user_pic
             END AS author_pic

      FROM post p
      LEFT JOIN `user` u ON p.user_id = u.id
      WHERE p.id = #{id} AND p.is_deleted = 0
      """)
  Post selectByIdWithAuthor(Long id);

  @Select("""
      SELECT p.id, p.user_id, p.title, p.content, p.views, p.like_count, p.reply_count, p.is_deleted, p.create_time, p.update_time,
             CASE
               WHEN u.id IS NULL THEN '用户不存在'
               WHEN u.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(u.nickname,''), u.username)
             END AS author,
             CASE
               WHEN u.status = 0 THEN NULL
               ELSE u.user_pic
             END AS author_pic

      FROM post p
      LEFT JOIN `user` u ON p.user_id = u.id
      WHERE p.is_deleted = 0
        AND (#{keyword} IS NULL OR #{keyword} = '' OR p.title LIKE CONCAT('%', #{keyword}, '%') OR p.content LIKE CONCAT('%', #{keyword}, '%'))
      ORDER BY ${sortBy} ${order}
      LIMIT #{pageSize} OFFSET #{offset}
      """)
  List<Post> list(int pageSize, int offset, String sortBy, String order, String keyword);

  @Insert("INSERT INTO post(user_id, title, content, views, like_count, reply_count, is_deleted, create_time, update_time) "
      +
      "VALUES(#{userId}, #{title}, #{content}, 0, 0, 0, 0, NOW(), NOW())")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Post post);

  // 注意：post.update_time 在 DB 中通常带有 ON UPDATE CURRENT_TIMESTAMP。
  // 仅“查看/浏览”不应被视为“编辑”，因此这里显式写 update_time = update_time
  // 以避免触发自动更新时间戳。
  @Update("UPDATE post SET views = views + 1, update_time = update_time WHERE id = #{postId} AND is_deleted=0")
  int incViews(Long postId);

  // 回复数变化不应影响“编辑时间”
  @Update("UPDATE post SET reply_count = reply_count + 1, update_time = update_time WHERE id = #{postId} AND is_deleted=0")
  int incReplyCount(Long postId);

  // 回复数变化不应影响“编辑时间”
  @Update("UPDATE post SET reply_count = IF(reply_count>0, reply_count-1, 0), update_time = update_time WHERE id = #{postId} AND is_deleted=0")
  int decReplyCount(Long postId);

  // 点赞数变化不应影响“编辑时间”
  @Update("UPDATE post SET like_count = #{likeCount}, update_time = update_time WHERE id = #{postId} AND is_deleted=0")
  int updateLikeCount(Long postId, Integer likeCount);

  // ------------------- 我的帖子管理 -------------------

  @Select("""
      SELECT p.id, p.user_id, p.title, p.content, p.views, p.like_count, p.reply_count, p.is_deleted, p.create_time, p.update_time,
             CASE
               WHEN u.id IS NULL THEN '用户不存在'
               WHEN u.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(u.nickname,''), u.username)
             END AS author,
             CASE
               WHEN u.status = 0 THEN NULL
               ELSE u.user_pic
             END AS author_pic

      FROM post p
      LEFT JOIN `user` u ON p.user_id = u.id
      WHERE p.is_deleted = 0 AND p.user_id = #{userId}
      ORDER BY p.create_time DESC, p.id DESC
      """)
  List<Post> listByUser(Long userId);

  @Update("UPDATE post SET title=#{title}, content=#{content}, update_time=NOW() WHERE id=#{postId} AND user_id=#{userId} AND is_deleted=0")
  int updateByIdAndUserId(Long userId, Long postId, String title, String content);

  @Update("UPDATE post SET is_deleted=1, update_time=NOW() WHERE id=#{postId} AND user_id=#{userId} AND is_deleted=0")
  int softDeleteByIdAndUserId(Long userId, Long postId);

  // ------------------- 我的点赞/收藏 -------------------

  @Select("""
      SELECT p.id, p.user_id, p.title, p.content, p.views, p.like_count, p.reply_count, p.is_deleted, p.create_time, p.update_time,
             CASE
               WHEN u.id IS NULL THEN '用户不存在'
               WHEN u.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(u.nickname,''), u.username)
             END AS author,
             CASE
               WHEN u.status = 0 THEN NULL
               ELSE u.user_pic
             END AS author_pic

      FROM post_like pl
      JOIN post p ON pl.post_id = p.id
      LEFT JOIN `user` u ON p.user_id = u.id
      WHERE pl.user_id = #{userId} AND p.is_deleted = 0
      ORDER BY pl.create_time DESC
      """)
  List<Post> listLikedByUser(Long userId);

  @Select("""
      SELECT p.id, p.user_id, p.title, p.content, p.views, p.like_count, p.reply_count, p.is_deleted, p.create_time, p.update_time,
             CASE
               WHEN u.id IS NULL THEN '用户不存在'
               WHEN u.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(u.nickname,''), u.username)
             END AS author,
             CASE
               WHEN u.status = 0 THEN NULL
               ELSE u.user_pic
             END AS author_pic

      FROM post_favorite pf
      JOIN post p ON pf.post_id = p.id
      LEFT JOIN `user` u ON p.user_id = u.id
      WHERE pf.user_id = #{userId} AND p.is_deleted = 0
      ORDER BY pf.create_time DESC
      """)
  List<Post> listFavoritedByUser(Long userId);

  // 管理后台统计
  @Select("SELECT COUNT(*) FROM post WHERE is_deleted = 0")
  long countNotDeleted();

}
