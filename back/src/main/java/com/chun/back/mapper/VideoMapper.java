package com.chun.back.mapper;

import com.chun.back.pojo.Video;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface VideoMapper {

  @Select("""
      SELECT v.id, v.user_id, v.title, v.url, v.cover, v.description,
             v.views, v.like_count, v.is_deleted, v.create_time, v.update_time,
             CASE
               WHEN ad.id IS NULL THEN '管理员不存在'
               WHEN ad.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(ad.nickname,''), ad.username)
             END AS author,
             CASE
               WHEN ad.status = 0 THEN NULL
               ELSE ad.admin_pic
             END AS author_pic

      FROM video v
      LEFT JOIN `admin` ad ON v.user_id = ad.id
      WHERE v.id = #{id} AND v.is_deleted = 0
      """)
  Video selectByIdWithAuthor(Long id);

  @Select("""
      SELECT v.id, v.user_id, v.title, v.url, v.cover, v.description,
             v.views, v.like_count, v.is_deleted, v.create_time, v.update_time,
             CASE
               WHEN ad.id IS NULL THEN '管理员不存在'
               WHEN ad.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(ad.nickname,''), ad.username)
             END AS author,
             CASE
               WHEN ad.status = 0 THEN NULL
               ELSE ad.admin_pic
             END AS author_pic

      FROM video v
      LEFT JOIN `admin` ad ON v.user_id = ad.id
      WHERE v.is_deleted = 0
        AND (#{keyword} IS NULL OR #{keyword} = '' OR v.title LIKE CONCAT('%', #{keyword}, '%') OR v.description LIKE CONCAT('%', #{keyword}, '%'))
      ORDER BY ${sortBy} ${order}
      LIMIT #{pageSize} OFFSET #{offset}
      """)
  List<Video> list(int pageSize, int offset, String sortBy, String order, String keyword);

  @Select("""
      SELECT v.id, v.user_id, v.title, v.url, v.cover, v.description,
             v.views, v.like_count, v.is_deleted, v.create_time, v.update_time,
             CASE
               WHEN ad.id IS NULL THEN '管理员不存在'
               WHEN ad.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(ad.nickname,''), ad.username)
             END AS author,
             CASE
               WHEN ad.status = 0 THEN NULL
               ELSE ad.admin_pic
             END AS author_pic

      FROM video v
      LEFT JOIN `admin` ad ON v.user_id = ad.id
      WHERE v.is_deleted = 0
      ORDER BY v.create_time DESC
      LIMIT #{count}
      """)
  List<Video> latest(Integer count);

  @Insert("INSERT INTO video(user_id, title, url, cover, description, views, like_count, is_deleted, create_time, update_time) "
      +
      "VALUES(#{userId}, #{title}, #{url}, #{cover}, #{description}, 0, 0, 0, NOW(), NOW())")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  int insert(Video video);

  // 仅“查看/浏览”不应改变更新时间（避免 ON UPDATE 自动更新时间戳）
  @Update("UPDATE video SET views = views + 1, update_time = update_time WHERE id = #{videoId} AND is_deleted=0")
  int incViews(Long videoId);

  // 点赞数变化不应影响“编辑/更新时间”展示
  @Update("UPDATE video SET like_count = #{likeCount}, update_time = update_time WHERE id = #{videoId} AND is_deleted=0")
  int updateLikeCount(Long videoId, Integer likeCount);

  // ------------------- 我的点赞/收藏 -------------------

  @Select("""
      SELECT v.id, v.user_id, v.title, v.url, v.cover, v.description,
             v.views, v.like_count, v.is_deleted, v.create_time, v.update_time,
             CASE
               WHEN ad.id IS NULL THEN '管理员不存在'
               WHEN ad.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(ad.nickname,''), ad.username)
             END AS author,
             CASE
               WHEN ad.status = 0 THEN NULL
               ELSE ad.admin_pic
             END AS author_pic

      FROM video_like vl
      JOIN video v ON vl.video_id = v.id
      LEFT JOIN `admin` ad ON v.user_id = ad.id
      WHERE vl.user_id = #{userId} AND v.is_deleted = 0
      ORDER BY vl.create_time DESC
      """)
  List<Video> listLikedByUser(Long userId);

  @Select("""
      SELECT v.id, v.user_id, v.title, v.url, v.cover, v.description,
             v.views, v.like_count, v.is_deleted, v.create_time, v.update_time,
             CASE
               WHEN ad.id IS NULL THEN '管理员不存在'
               WHEN ad.status = 0 THEN '账号已封禁'
               ELSE COALESCE(NULLIF(ad.nickname,''), ad.username)
             END AS author,
             CASE
               WHEN ad.status = 0 THEN NULL
               ELSE ad.admin_pic
             END AS author_pic

      FROM video_favorite vf
      JOIN video v ON vf.video_id = v.id
      LEFT JOIN `admin` ad ON v.user_id = ad.id
      WHERE vf.user_id = #{userId} AND v.is_deleted = 0
      ORDER BY vf.create_time DESC
      """)
  List<Video> listFavoritedByUser(Long userId);

  // 管理后台统计
  @Select("SELECT COUNT(*) FROM video WHERE is_deleted = 0")
  long countNotDeleted();

}
