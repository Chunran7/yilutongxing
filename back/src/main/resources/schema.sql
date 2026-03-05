-- =========================================================
-- schema.sql (revised, final)
-- Forum-like project with Article / Post / Video modules
-- MySQL 8.x recommended
-- =========================================================
-- 0. Create DB
-- 统一数据库名：dbproject
CREATE DATABASE IF NOT EXISTS `dbproject` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `dbproject`;

-- 为了方便重建（尤其是有外键时）
SET
  FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1) user（与现有后端逻辑保持一致）
--   表名：`user`
--   密码列：`password`
-- =========================================================
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(MD5/其他哈希后的字符串)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `user_pic` VARCHAR(255) DEFAULT NULL COMMENT '用户头像地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

#便于管理员的解封与封禁
ALTER TABLE
  `user`
ADD
  COLUMN `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1正常,0封禁)'
AFTER
  `user_pic`;

-- =========================================================
-- 2) Article (对应 Article.java)
--   author(字符串) -> user_id(外键)
-- =========================================================
DROP TABLE IF EXISTS `article`;

CREATE TABLE `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '作者用户ID',
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `first_picture` VARCHAR(255) DEFAULT NULL COMMENT '首图URL',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要/描述',
  `content` LONGTEXT COMMENT '文章正文',
  `views` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删,1已删)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_user_time` (`user_id`, `create_time`),
  KEY `idx_article_time` (`create_time`),
  CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_article_is_deleted` CHECK (`is_deleted` IN (0, 1))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章表';

-- =========================================================
-- 3) Post (对应 Post.java)
-- =========================================================
DROP TABLE IF EXISTS `post`;

CREATE TABLE `post` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发帖用户ID',
  `title` VARCHAR(255) NOT NULL COMMENT '标题',
  `content` TEXT NOT NULL COMMENT '正文',
  `views` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `reply_count` INT NOT NULL DEFAULT 0 COMMENT '回复数',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删,1已删)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_post_user_time` (`user_id`, `create_time`),
  KEY `idx_post_time` (`create_time`),
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_post_is_deleted` CHECK (`is_deleted` IN (0, 1))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '帖子表';

-- =========================================================
-- 4) Comment (对应 Comment.java，多级回复)
-- =========================================================
DROP TABLE IF EXISTS `comment`;

CREATE TABLE `comment` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` BIGINT NOT NULL COMMENT '所属帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '发表评论的用户ID',
  `parent_id` BIGINT NOT NULL DEFAULT 0 COMMENT '父评论ID(二级回复)',
  `root_id` BIGINT NOT NULL DEFAULT 0 COMMENT '根评论ID(属于哪个一级评论)',
  `reply_user_id` BIGINT DEFAULT NULL COMMENT '被回复人的用户ID',
  `content` TEXT NOT NULL COMMENT '评论内容',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删,1已删)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
  PRIMARY KEY (`id`),
  KEY `idx_comment_post_time` (`post_id`, `create_time`),
  KEY `idx_comment_post_root_time` (`post_id`, `root_id`, `create_time`),
  KEY `idx_comment_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_comment_reply_user` FOREIGN KEY (`reply_user_id`) REFERENCES `user` (`id`) ON DELETE
  SET
    NULL ON UPDATE CASCADE,
    CONSTRAINT `ck_comment_is_deleted` CHECK (`is_deleted` IN (0, 1))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '评论表';

-- =========================================================
-- 5) Video (对应 Video.java)
--   author(字符串) -> user_id(外键)
-- =========================================================
DROP TABLE IF EXISTS `video`;

CREATE TABLE `video` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '视频ID',
  `user_id` BIGINT NOT NULL COMMENT '作者用户ID',
  `title` VARCHAR(255) NOT NULL COMMENT '视频标题',
  `url` VARCHAR(255) NOT NULL COMMENT '视频播放链接',
  `cover` VARCHAR(255) DEFAULT NULL COMMENT '视频封面图',
  `description` TEXT COMMENT '视频描述',
  `views` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` INT NOT NULL DEFAULT 0 COMMENT '点赞数',
  `is_deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除(0未删,1已删)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  KEY `idx_video_user_time` (`user_id`, `create_time`),
  KEY `idx_video_time` (`create_time`),
  CONSTRAINT `fk_video_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_video_is_deleted` CHECK (`is_deleted` IN (0, 1))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '视频表';

-- =========================================================
-- 6) Post Like (防重复点赞)
-- =========================================================
DROP TABLE IF EXISTS `post_like`;

CREATE TABLE `post_like` (
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`post_id`, `user_id`),
  KEY `idx_post_like_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_post_like_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '帖子点赞表';

-- =========================================================
-- 7) Post Favorite (收藏)
-- =========================================================
DROP TABLE IF EXISTS `post_favorite`;

CREATE TABLE `post_favorite` (
  `post_id` BIGINT NOT NULL COMMENT '帖子ID',
  `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`post_id`, `user_id`),
  KEY `idx_post_favorite_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_post_favorite_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_post_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '帖子收藏表';

-- =========================================================
-- 8) Article Like (防重复点赞)
-- =========================================================
DROP TABLE IF EXISTS `article_like`;

CREATE TABLE `article_like` (
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`article_id`, `user_id`),
  KEY `idx_article_like_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_article_like_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_article_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章点赞表';

-- =========================================================
-- 9) Article Favorite (收藏)
-- =========================================================
DROP TABLE IF EXISTS `article_favorite`;

CREATE TABLE `article_favorite` (
  `article_id` BIGINT NOT NULL COMMENT '文章ID',
  `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`article_id`, `user_id`),
  KEY `idx_article_favorite_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_article_favorite_article` FOREIGN KEY (`article_id`) REFERENCES `article` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_article_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章收藏表';

-- =========================================================
-- 10) Video Like (防重复点赞)
-- =========================================================
DROP TABLE IF EXISTS `video_like`;

CREATE TABLE `video_like` (
  `video_id` BIGINT NOT NULL COMMENT '视频ID',
  `user_id` BIGINT NOT NULL COMMENT '点赞用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`video_id`, `user_id`),
  KEY `idx_video_like_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_video_like_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_video_like_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '视频点赞表';

-- =========================================================
-- 11) Video Favorite (收藏)
-- =========================================================
DROP TABLE IF EXISTS `video_favorite`;

CREATE TABLE `video_favorite` (
  `video_id` BIGINT NOT NULL COMMENT '视频ID',
  `user_id` BIGINT NOT NULL COMMENT '收藏用户ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`video_id`, `user_id`),
  KEY `idx_video_favorite_user_time` (`user_id`, `create_time`),
  CONSTRAINT `fk_video_favorite_video` FOREIGN KEY (`video_id`) REFERENCES `video` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_video_favorite_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '视频收藏表';

-- =========================================================
-- 12) User Follow (关注)
-- =========================================================
DROP TABLE IF EXISTS `user_follow`;

CREATE TABLE `user_follow` (
  `follower_id` BIGINT NOT NULL COMMENT '关注者(粉丝)ID',
  `followee_id` BIGINT NOT NULL COMMENT '被关注者ID',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`follower_id`, `followee_id`),
  KEY `idx_followee_time` (`followee_id`, `create_time`),
  CONSTRAINT `fk_follow_follower` FOREIGN KEY (`follower_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_follow_followee` FOREIGN KEY (`followee_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户关注表';

-- =========================================================
-- 13) Admin
-- =========================================================
DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录名',
  `password` VARCHAR(64) NOT NULL COMMENT '密码(建议存MD5/BCrypt后的密文)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `admin_pic` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态(1启用,0停用)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员表';

-- 删除is_root列，不再区分管理员角色
SET
  FOREIGN_KEY_CHECKS = 1;