package com.atguigu.examsystem.vo;

import lombok.Data;

/**
 * 统一返回Result类
 * @param <T>
 */
@Data
public class Result<T> {

    /**
     * code码
     */
    private Integer code;
    /**
     * msg消息
     */
    private String msg;
    /**
     * data
     */
    private T data;

    public Result() {

    }

    public static <T> Result<T> ok() {
        Result res = new Result<>();
        res.setCode(200);
        res.setMsg("响应成功");
        return res;
    }

    public static <T> Result<T> okMsg(String msg) {
        Result res = new Result<>();
        res.setCode(200);
        res.setMsg(msg);
        return res;
    }

    public static <T> Result<T> okData(T data) {
        Result<T> res = new Result<T>();
        res.setCode(200);
        res.setMsg("响应成功");
        res.setData(data);
        return res;
    }

    public static <T> Result<T> error() {
        Result res = new Result();
        res.setCode(500);
        res.setMsg("响应失败");
        return res;
    }

    public static <T> Result<T> errorMsg(String msg) {
        Result<T> res = new Result<>();
        res.setCode(500);
        res.setMsg(msg);
        return res;
    }
}
