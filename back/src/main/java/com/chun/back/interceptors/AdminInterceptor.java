package com.chun.back.interceptors;

import com.chun.back.mapper.AdminMapper;
import com.chun.back.pojo.Admin;
import com.chun.back.pojo.Result;
import com.chun.back.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AdminMapper adminMapper; // ✅ 注入进来，否则你就会“无法解析符号”

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()))
            return true;

        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return unauthorized(response, "需要管理员登录");
        }
        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;

        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);

            Object type = claims.get("type");
            Object adminIdObj = claims.get("adminId");
            if (!"admin".equals(String.valueOf(type)) || adminIdObj == null) {
                return unauthorized(response, "无管理员权限");
            }

            Long adminId = Long.valueOf(adminIdObj.toString());

            // ✅ 再校验：管理员是否真实存在、是否停用（防止伪造token）
            Admin me = adminMapper.selectBasicById(adminId);
            if (me == null)
                return unauthorized(response, "管理员不存在");
            if (me.getStatus() != null && me.getStatus() == 0)
                return unauthorized(response, "管理员账号已停用");

            request.setAttribute("adminClaims", claims);
            return true;

        } catch (Exception e) {
            return unauthorized(response, "需要管理员登录");
        }
    }

    private boolean unauthorized(HttpServletResponse response, String msg) throws Exception {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(mapper.writeValueAsString(Result.error(msg)));
        return false;
    }
}
