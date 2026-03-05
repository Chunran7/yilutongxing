package com.chun.back.controller;

import com.chun.back.pojo.Result;
import com.chun.back.service.CommentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    private Long getLoginUserId(HttpServletRequest request) {
        Object obj = request.getAttribute("claims");
        if (obj instanceof Map<?, ?> m) {
            Object id = m.get("id");
            if (id != null) return Long.valueOf(id.toString());
        }
        return null;
    }

    @PostMapping("/comment")
    public Result create(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        Long userId = getLoginUserId(request);

        Long postId = body.get("postId") == null ? null : Long.valueOf(body.get("postId").toString());
        String content = body.get("content") == null ? null : body.get("content").toString();
        Long parentId = body.get("parentId") == null ? 0L : Long.valueOf(body.get("parentId").toString());
        Long replyUserId = body.get("replyUserId") == null ? null : Long.valueOf(body.get("replyUserId").toString());

        if (postId == null || content == null || content.isBlank()) {
            return Result.error("参数错误");
        }

        Long id = commentService.create(postId, userId, content, parentId, replyUserId);
        return Result.success(id);
    }

    @DeleteMapping("/comment/{id}")
    public Result delete(@PathVariable Long id, HttpServletRequest request) {
        Long userId = getLoginUserId(request);
        boolean ok = commentService.delete(id, userId);
        return ok ? Result.success() : Result.error("删除失败");
    }
}
