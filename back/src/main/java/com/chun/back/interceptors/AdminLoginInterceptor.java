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
public class AdminLoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private AdminMapper adminMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            return unauthorized(response, "需要管理员登录");
        }

        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;

        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);

            // 防止用户token冒充：admin token里必须带 role=admin
            Object role = claims.get("role");
            if (role == null || !"admin".equals(role.toString())) {
                return unauthorized(response, "需要管理员登录");
            }

            Object idObj = claims.get("id");
            if (idObj == null) return unauthorized(response, "需要管理员登录");

            Long adminId = Long.valueOf(idObj.toString());
            Admin a = adminMapper.selectById(adminId);
            if (a == null) return unauthorized(response, "管理员不存在");
            if (a.getStatus() != null && a.getStatus() == 0) {
                return unauthorized(response, "管理员账号已停用");
            }

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
