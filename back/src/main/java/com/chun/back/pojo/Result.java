package com.chun.back.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    private int code;      // 状态码
    private String msg;    // 提示信息
    private Object data;   // 返回数据

    // 快捷静态方法
    public static Result success() {
        return new Result(0, "操作成功", null);
    }

    
    public static Result success(Object data) {
        return new Result(0, "操作成功", data);
    }


    public static Result error(String msg) {
        return new Result(1, msg, null);
    }
}