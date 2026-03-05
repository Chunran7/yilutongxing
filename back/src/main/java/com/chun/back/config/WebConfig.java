package com.chun.back.config;

import com.chun.back.interceptors.AdminInterceptor;
import com.chun.back.interceptors.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@Configuration
public class WebConfig implements WebMvcConfigurer {

        @Autowired
        private LoginInterceptor loginInterceptor;

        @Autowired
        private AdminInterceptor adminInterceptor;

        @Override
        public void addInterceptors(InterceptorRegistry registry) {

                // ✅ 1) 管理员拦截器：只拦 /admin/**，但放行登录/注册/申请
                registry.addInterceptor(adminInterceptor)
                                .addPathPatterns("/admin/**")
                                .excludePathPatterns(
                                                "/admin/login",
                                                "/admin/register",
                                                "/admin/apply");

                // ✅ 2) 用户拦截器：拦所有，但必须排除 /admin/**（否则你就会“登录后立刻被踢”）
                registry.addInterceptor(loginInterceptor)
                                .addPathPatterns("/**")
                                .excludePathPatterns(
                                                // 用户自身放行
                                                "/user/login",
                                                "/user/register",
                                                // 忘记密码相关接口放行（允许未登录访问）
                                                "/user/forgot/**",
                                                "/user/profile/*",

                                                // 公共资源放行（按你项目实际补）
                                                "/uploads/**",
                                                "/static/**",
                                                "/favicon.ico",
                                                "/error",

                                                // ✅ 关键：排除所有管理员接口
                                                "/admin/**");
        }

        @Override
        public void addResourceHandlers(ResourceHandlerRegistry registry) {
                // 映射 /uploads/** 和 /api/uploads/** 到磁盘上的 uploads 目录。
                // 兼容两种常见的运行目录：
                // - 在项目根目录运行（uploads 与 back 同级） -> file:../uploads/
                // - 在 back 模块目录运行（uploads 在 back/uploads） -> file:uploads/
                registry.addResourceHandler("/uploads/**", "/api/uploads/**")
                                .addResourceLocations("file:uploads/", "file:../uploads/", "classpath:/static/uploads/")
                                .setCachePeriod(3600);

                // 兼容直接请求 /videos/** 或 /api/videos/**，从 uploads/videos 目录读取
                registry.addResourceHandler("/videos/**", "/api/videos/**")
                                .addResourceLocations("file:uploads/videos/", "file:../uploads/videos/",
                                                "classpath:/static/uploads/videos/")
                                .setCachePeriod(3600);

                // 保持静态资源默认映射
                registry.addResourceHandler("/static/**")
                                .addResourceLocations("classpath:/static/");
        }
}
