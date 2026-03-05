package com.chun.back.interceptors;

import com.chun.back.mapper.UserMapper;
import com.chun.back.pojo.Result;
import com.chun.back.pojo.User;
import com.chun.back.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;

@Component
public class LoginInterceptor implements HandlerInterceptor {

    private final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    private UserMapper userMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        // 预检请求直接放行（避免跨域时被拦）
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) return true;

        String authorization = request.getHeader("Authorization");
        if (authorization == null || authorization.isBlank()) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(Result.error("需要登录")));
            return false;
        }

        String token = authorization.startsWith("Bearer ") ? authorization.substring(7) : authorization;

        try {
            Map<String, Object> claims = JwtUtil.parseToken(token);

            Object idObj = claims.get("id");
            if (idObj == null) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error("需要登录")));
                return false;
            }

            Long userId = Long.valueOf(idObj.toString());
            User u = userMapper.selectById(userId);
            if (u == null) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error("用户不存在")));
                return false;
            }

            // ⭐ 封禁校验：建议用 401，让前端清 token 并强制退出
            if (u.getStatus() != null && u.getStatus() == 0) {
                response.setStatus(401);
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write(mapper.writeValueAsString(Result.error("账号已被封禁")));
                return false;
            }

            request.setAttribute("claims", claims);
            return true;
        } catch (Exception e) {
            response.setStatus(401);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(mapper.writeValueAsString(Result.error("需要登录")));
            return false;
        }
    }
}
