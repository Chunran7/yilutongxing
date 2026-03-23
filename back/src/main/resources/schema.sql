-- =========================================================
-- schema.sql (cleaned - 纯内容展示网站版本)
-- 已移除所有交互功能相关表：post, video, comment, 点赞，收藏，关注
-- =========================================================
-- 0. Create DB
-- 统一数据库名：dbproject
CREATE DATABASE IF NOT EXISTS `dbproject` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE `dbproject`;

-- 为了方便重建（尤其是有外键时）
SET FOREIGN_KEY_CHECKS = 0;

-- =========================================================
-- 1) user（用户表 - 仅保留基础信息）
-- =========================================================
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '用户名',
  `password` VARCHAR(255) NOT NULL COMMENT '密码 (MD5/其他哈希后的字符串)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `email` VARCHAR(128) DEFAULT NULL COMMENT '邮箱',
  `user_pic` VARCHAR(255) DEFAULT NULL COMMENT '用户头像地址',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '用户表';

-- =========================================================
-- 2) Article (文章表 - 移除 like_count 字段)
-- =========================================================
DROP TABLE IF EXISTS `article`;

CREATE TABLE `article` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '文章 ID',
  `user_id` BIGINT NOT NULL COMMENT '作者用户 ID',
  `title` VARCHAR(255) NOT NULL COMMENT '文章标题',
  `first_picture` VARCHAR(255) DEFAULT NULL COMMENT '首图 URL',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '文章摘要/描述',
  `content` LONGTEXT COMMENT '文章正文',
  `views` INT NOT NULL DEFAULT 0 COMMENT '浏览量',
  `is_deleted` INT NOT NULL DEFAULT 0 COMMENT '逻辑删除 (0 未删，1 已删)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_article_user_time` (`user_id`, `create_time`),
  KEY `idx_article_time` (`create_time`),
  CONSTRAINT `fk_article_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_article_is_deleted` CHECK (`is_deleted` IN (0, 1))
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '文章表';

-- =========================================================
-- 3) Admin (管理员表 - 保留后台管理功能)
-- =========================================================
DROP TABLE IF EXISTS `admin`;

CREATE TABLE `admin` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '管理员 ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录名',
  `password` VARCHAR(64) NOT NULL COMMENT '密码 (MD5/BCrypt 后的密文)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `admin_pic` VARCHAR(255) DEFAULT NULL COMMENT '头像',
  `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1 启用，0 停用)',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_admin_username` (`username`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '管理员表';

-- 恢复外键检查
SET FOREIGN_KEY_CHECKS = 1;
