package com.chun.back.service.impl;

import com.chun.back.mapper.CommentMapper;
import com.chun.back.mapper.PostMapper;
import com.chun.back.pojo.Comment;
import com.chun.back.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private PostMapper postMapper;

    @Override
    public List<Comment> listTree(Long postId) {
        // 仅返回两层结构：
        //  - 第一层：根评论（parent_id=0）
        //  - 第二层：该根评论下的所有回复（无论回复的是谁，都统一挂在 root 下）
        // 删除策略：
        //  - 被删除的评论直接隐藏
        //  - 若删除的是根评论，则隐藏该根评论及其所有后续回复
        List<Comment> all = commentMapper.selectByPostId(postId);
        if (all == null || all.isEmpty()) return Collections.emptyList();

        Map<Long, Comment> map = new HashMap<>();
        for (Comment c : all) {
            c.setReplies(new ArrayList<>());
            map.put(c.getId(), c);
        }

        // 兼容：有些历史数据 root_id 可能为 0，这里基于 parent 链推断
        for (Comment c : all) {
            if (c.getParentId() != null && c.getParentId() != 0) {
                long rid = inferRootId(c, map);
                c.setRootId(rid);
            } else {
                c.setRootId(0L);
            }
        }

        // 找出“被删除的根评论”集合，后续全部过滤
        Set<Long> deletedRootIds = new HashSet<>();
        for (Comment c : all) {
            boolean isRoot = (c.getParentId() == null || c.getParentId() == 0);
            if (isRoot && c.getIsDeleted() != null && c.getIsDeleted() == 1) {
                deletedRootIds.add(c.getId());
            }
        }

        // 第一层：未删除的根评论
        List<Comment> roots = new ArrayList<>();
        for (Comment c : all) {
            boolean isRoot = (c.getParentId() == null || c.getParentId() == 0);
            if (!isRoot) continue;
            if (c.getIsDeleted() != null && c.getIsDeleted() == 1) continue;
            roots.add(c);
        }

        // 第二层：将所有非根评论统一挂到对应 root 下
        Map<Long, List<Comment>> childrenByRoot = new HashMap<>();
        for (Comment c : all) {
            boolean isRoot = (c.getParentId() == null || c.getParentId() == 0);
            if (isRoot) continue;
            if (c.getIsDeleted() != null && c.getIsDeleted() == 1) continue;
            long rid = (c.getRootId() == null ? 0L : c.getRootId());
            if (rid == 0L) {
                // 推断失败/父链断裂：兜底挂到 parent（可能是根）
                rid = c.getParentId() == null ? 0L : c.getParentId();
            }
            if (rid == 0L) continue;
            if (deletedRootIds.contains(rid)) continue;
            childrenByRoot.computeIfAbsent(rid, k -> new ArrayList<>()).add(c);
        }

        for (Comment root : roots) {
            List<Comment> children = childrenByRoot.get(root.getId());
            if (children != null) {
                root.setReplies(children);
            }
        }

        return roots;
    }

    /**
     * 根据 parent 链推断 rootId：返回根评论 id（parent_id=0 的那条）
     */
    private long inferRootId(Comment c, Map<Long, Comment> map) {
        if (c.getRootId() != null && c.getRootId() != 0L) return c.getRootId();
        Long pid = c.getParentId();
        int guard = 0;
        while (pid != null && pid != 0L && guard++ < 32) {
            Comment parent = map.get(pid);
            if (parent == null) break;
            if (parent.getParentId() == null || parent.getParentId() == 0L) {
                return parent.getId();
            }
            pid = parent.getParentId();
        }
        return 0L;
    }

    @Override
    public Long create(Long postId, Long userId, String content, Long parentId, Long replyUserId) {
        long pid = parentId == null ? 0L : parentId;

        Comment c = new Comment();
        c.setPostId(postId);
        c.setUserId(userId);
        c.setParentId(pid);
        c.setReplyUserId(replyUserId);

        long rootId = 0L;
        if (pid != 0L) {
            Comment parent = commentMapper.selectById(pid);
            if (parent != null) {
                if (parent.getRootId() != null && parent.getRootId() != 0L) {
                    rootId = parent.getRootId();
                } else {
                    rootId = parent.getId();
                }
                // 默认回复父评论作者
                if (replyUserId == null) {
                    c.setReplyUserId(parent.getUserId());
                }
            }
        }
        c.setRootId(rootId);
        c.setContent(content);

        commentMapper.insert(c);

        // 顶层评论 root_id = 0（按 schema 默认）
        postMapper.incReplyCount(postId);

        return c.getId();
    }

    @Override
    public boolean delete(Long commentId, Long userId) {
        // 先查 post_id，用于更新 reply_count
        Comment c = commentMapper.selectById(commentId);
        if (c == null) return false;

        int ok = commentMapper.softDelete(commentId, userId);
        if (ok > 0) {
            postMapper.decReplyCount(c.getPostId());
            return true;
        }
        return false;
    }
}
