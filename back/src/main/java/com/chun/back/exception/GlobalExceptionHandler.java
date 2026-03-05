package com.chun.back.exception;

import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.chun.back.pojo.Result;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        // 取当前异常或其 root cause 的 message，避免出现 message 为空只能返回“操作失败”的情况。
        String msg = null;
        Throwable cur = e;
        while (cur != null) {
            if (StringUtils.hasLength(cur.getMessage())) {
                msg = cur.getMessage();
                break;
            }
            cur = cur.getCause();
        }
        return Result.error(StringUtils.hasLength(msg) ? msg : "操作失败");
    }
}
